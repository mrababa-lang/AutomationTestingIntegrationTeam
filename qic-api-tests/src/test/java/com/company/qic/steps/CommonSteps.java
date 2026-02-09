package com.company.qic.steps;

import com.company.qic.config.Env;
import com.company.qic.config.TestConfig;
import com.company.qic.core.*;
import com.company.qic.util.Json;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommonSteps {
  private final TestConfig config = new TestConfig(new Env("application-test.properties"));
  private final DataResolver dataResolver = new DataResolver();
  private final SchemaValidator schemaValidator = new SchemaValidator();
  private final RequestSpecFactory requestSpecFactory = new RequestSpecFactory(config);
  private final ResponseSpecFactory responseSpecFactory = new ResponseSpecFactory(config);
  private final ApiClient apiClient = new ApiClient();
  private final RetryPolicy retryPolicy = new RetryPolicy(config);
  private final OpenApiContract openApiContract = new OpenApiContract(config.openApiSpecPath());

  @Given("I load request body from {string}")
  public void loadRequestBody(String path) {
    ScenarioContext context = ScenarioContextHolder.get();
    String body = dataResolver.loadAndResolve(path, context);
    context.setRequestBody(body);
  }

  @Given("I use request body:")
  public void useRequestBody(String docString) {
    ScenarioContext context = ScenarioContextHolder.get();
    context.setRequestBody(dataResolver.resolvePlaceholders(docString, context));
  }

  @And("I set json path {string} to {string}")
  public void setJsonPath(String path, String value) {
    ScenarioContext context = ScenarioContextHolder.get();
    String body = context.getRequestBody();
    if (body == null) {
      throw new IllegalStateException("Request body not set");
    }
    String resolvedValue = dataResolver.resolvePlaceholders(value, context);
    DocumentContext document = JsonPath.parse(body);
    Object parsedValue = resolvedValue;
    try {
      parsedValue = Json.MAPPER.readValue(resolvedValue, Object.class);
    } catch (Exception ignored) {
      // Use string value when not valid JSON.
    }
    document.set(path, parsedValue);
    context.setRequestBody(document.jsonString());
  }

  @And("I set header {string} to {string}")
  public void setHeader(String name, String value) {
    ScenarioContext context = ScenarioContextHolder.get();
    Map<String, String> headers = headersFromContext(context);
    headers.put(name, dataResolver.resolvePlaceholders(value, context));
    context.put("headers", headers);
  }

  @And("I remove header {string}")
  public void removeHeader(String name) {
    ScenarioContext context = ScenarioContextHolder.get();
    Map<String, String> headers = headersFromContext(context);
    headers.remove(name);
    context.put("headers", headers);
  }

  @And("I use auth basic")
  public void useAuthBasic() {
    ScenarioContextHolder.get().put("authOverride", "basic");
  }

  @And("I use no auth")
  public void useNoAuth() {
    ScenarioContextHolder.get().put("authOverride", "none");
  }

  @When("I call {string}")
  public void callEndpoint(String endpointName) {
    ScenarioContext context = ScenarioContextHolder.get();
    Endpoint endpoint = Endpoint.valueOf(endpointName);

    boolean useAuth = resolveAuth(endpoint, context);
    boolean useCompanyHeader = resolveCompanyHeader(endpoint, context);
    boolean contractEnabled = resolveContractEnabled(context);

    Response response;
    if (config.retryEnabled()) {
      response = apiClient.executeWithRetry(
          retryPolicy.create(endpoint.name()),
          () -> executeRequest(endpoint, useAuth, useCompanyHeader, contractEnabled, context));
    } else {
      response = apiClient.execute(() -> executeRequest(endpoint, useAuth, useCompanyHeader, contractEnabled, context));
    }

    context.put("response", response);

    if (shouldPoll(context, response)) {
      response = pollUntilComplete(endpoint, useAuth, useCompanyHeader, contractEnabled, context);
      context.put("response", response);
    }
    context.put("contractEnabled", contractEnabled);
  }

  @Then("response status should be {int}")
  public void responseStatusShouldBe(int code) {
    Response response = responseFromContext();
    response.then().statusCode(code);
    responseSpecFactory.validate(response);
  }

  @Then("response status should be one of {string}")
  public void responseStatusShouldBeOneOf(String codes) {
    Response response = responseFromContext();
    String[] parts = codes.split(",");
    Integer[] expected = new Integer[parts.length];
    for (int i = 0; i < parts.length; i++) {
      expected[i] = Integer.parseInt(parts[i].trim());
    }
    response.then().statusCode(Matchers.isOneOf(expected));
    responseSpecFactory.validate(response);
  }

  @And("response time should be under {long} ms")
  public void responseTimeShouldBeUnder(long ms) {
    Response response = responseFromContext();
    response.then().time(Matchers.lessThan(ms));
  }

  @And("response should match schema {string}")
  public void responseShouldMatchSchema(String schemaPath) {
    if (!resolveSchemaEnabled(ScenarioContextHolder.get())) {
      return;
    }
    Response response = responseFromContext();
    if (response.statusCode() >= 200 && response.statusCode() < 300
        && response.contentType() != null && response.contentType().contains("json")) {
      schemaValidator.validate(response, schemaPath);
    }
  }

  @And("response should satisfy OpenAPI contract")
  public void responseShouldSatisfyOpenApi() {
    ScenarioContext context = ScenarioContextHolder.get();
    boolean enabled = (boolean) context.getOrDefault("contractEnabled", false);
    if (!enabled) {
      throw new AssertionError("OpenAPI contract validation was not enabled for this scenario");
    }
  }

  @And("I save response field {string} as {string}")
  public void saveResponseField(String jsonPath, String key) {
    Response response = responseFromContext();
    Object value = response.jsonPath().get(jsonPath);
    ScenarioContextHolder.get().put(key, value);
  }

  @And("response field {string} should equal {string}")
  public void responseFieldShouldEqual(String jsonPath, String value) {
    Response response = responseFromContext();
    String actual = response.jsonPath().getString(jsonPath);
    String expected = dataResolver.resolvePlaceholders(value, ScenarioContextHolder.get());
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, "Response field mismatch");
  }

  private Response executeRequest(Endpoint endpoint, boolean useAuth, boolean useCompanyHeader,
                                  boolean contractEnabled, ScenarioContext context) {
    String body = context.getRequestBody();
    Map<String, String> headers = headersFromContext(context);
    io.restassured.specification.RequestSpecification spec = requestSpecFactory.create(
        endpoint,
        useAuth,
        useCompanyHeader,
        contractEnabled ? openApiContract.filter() : null
    );
    if (!headers.isEmpty()) {
      spec.headers(headers);
    }
    if (body != null) {
      spec.body(body);
    }
    return spec.post(endpoint.path());
  }

  private Map<String, String> headersFromContext(ScenarioContext context) {
    Object stored = context.get("headers");
    if (stored instanceof Map<?, ?> map) {
      Map<String, String> headers = new HashMap<>();
      map.forEach((key, value) -> headers.put(key.toString(), value.toString()));
      return headers;
    }
    return new HashMap<>();
  }

  private boolean resolveAuth(Endpoint endpoint, ScenarioContext context) {
    Object override = context.get("authOverride");
    Set<String> tags = tags(context);
    if (override != null) {
      return "basic".equalsIgnoreCase(override.toString());
    }
    if (tags.contains("@auth:basic")) {
      return true;
    }
    if (tags.contains("@auth:none")) {
      return false;
    }
    return endpoint.basicAuth();
  }

  private boolean resolveCompanyHeader(Endpoint endpoint, ScenarioContext context) {
    Set<String> tags = tags(context);
    if (tags.contains("@header:company")) {
      return true;
    }
    if (tags.contains("@header:none")) {
      return false;
    }
    return endpoint.companyHeader();
  }

  private boolean resolveContractEnabled(ScenarioContext context) {
    Set<String> tags = tags(context);
    if (tags.contains("@no-contract")) {
      return false;
    }
    if (tags.contains("@contract")) {
      return true;
    }
    return config.openApiEnabled();
  }

  private boolean resolveSchemaEnabled(ScenarioContext context) {
    Set<String> tags = tags(context);
    if (tags.contains("@no-schema")) {
      return false;
    }
    if (tags.contains("@schema")) {
      return true;
    }
    return config.schemaValidationEnabled();
  }

  private boolean shouldPoll(ScenarioContext context, Response response) {
    Set<String> tags = tags(context);
    if (tags.contains("@poll")) {
      return true;
    }
    return response.statusCode() == 202 && config.pollEnabled();
  }

  private Response pollUntilComplete(Endpoint endpoint, boolean useAuth, boolean useCompanyHeader,
                                     boolean contractEnabled, ScenarioContext context) {
    Instant end = Instant.now().plusSeconds(config.pollTimeoutSeconds());
    Response response = responseFromContext();
    while (Instant.now().isBefore(end)) {
      if (response.statusCode() != 202) {
        return response;
      }
      try {
        Thread.sleep(config.pollIntervalMs());
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        return response;
      }
      response = executeRequest(endpoint, useAuth, useCompanyHeader, contractEnabled, context);
      context.put("response", response);
    }
    return response;
  }

  private Response responseFromContext() {
    Object response = ScenarioContextHolder.get().get("response");
    if (response instanceof Response result) {
      return result;
    }
    throw new IllegalStateException("Response not available");
  }

  private Set<String> tags(ScenarioContext context) {
    Object stored = context.get("tags");
    if (stored instanceof Set<?> set) {
      @SuppressWarnings("unchecked")
      Set<String> tags = (Set<String>) set;
      return tags;
    }
    return Set.of();
  }
}
