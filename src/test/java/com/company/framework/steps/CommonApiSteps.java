package com.company.framework.steps;

import com.company.framework.config.TestConfig;
import com.company.framework.core.ApiClient;
import com.company.framework.core.ScenarioContext;
import com.company.framework.validation.Assertions;
import com.company.framework.validation.SchemaValidator;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CommonApiSteps {
    private final ApiClient apiClient = new ApiClient();

    @Before
    public void beforeScenario() {
        ScenarioContext.clear();
    }

    @Given("base URL is configured")
    public void baseUrlConfigured() {
        String baseUrl = TestConfig.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL is not configured");
        }
    }

    @When("I send a GET request to {string}")
    public void sendGet(String path) {
        Response response = apiClient.sendGet(path);
        ScenarioContext.set("lastResponse", response);
    }

    @When("I send a POST request to {string} with body:")
    public void sendPost(String path, String body) {
        Response response = apiClient.sendPost(path, body);
        ScenarioContext.set("lastResponse", response);
    }

    @When("I send a POST request to {string} with body from {string}")
    public void sendPostFromResource(String path, String resourcePath) {
        String body = loadResource(resourcePath);
        Response response = apiClient.sendPost(path, body);
        ScenarioContext.set("lastResponse", response);
    }

    @Then("response status should be {int}")
    public void responseStatusShouldBe(int status) {
        Response response = ScenarioContext.get("lastResponse");
        Assertions.assertStatus(response, status);
    }

    @Then("response status should be one of {string}")
    public void responseStatusShouldBeOneOf(String statuses) {
        Response response = ScenarioContext.get("lastResponse");
        String[] parts = statuses.split(",");
        int[] statusCodes = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            statusCodes[i] = Integer.parseInt(parts[i].trim());
        }
        Assertions.assertStatusIn(response, statusCodes);
    }

    @And("json path {string} should equal {string}")
    public void jsonPathShouldEqual(String jsonPath, String expected) {
        Response response = ScenarioContext.get("lastResponse");
        Assertions.assertJsonPathEquals(response, jsonPath, expected);
    }

    @And("response matches schema {string}")
    public void responseMatchesSchema(String schemaPath) {
        Response response = ScenarioContext.get("lastResponse");
        SchemaValidator.validate(response, schemaPath);
    }

    @And("I set header {string} to {string}")
    public void setHeader(String name, String value) {
        Map<String, String> headers = ScenarioContext.get("headers");
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        ScenarioContext.set("headers", headers);
    }

    @And("I use auth {string}")
    public void useAuth(String type) {
        ScenarioContext.set("authTypeOverride", type);
    }

    private String loadResource(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        try {
            Path path = Path.of(resource.toURI());
            return Files.readString(path);
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Failed to load resource: " + resourcePath, exception);
        }
    }
}
