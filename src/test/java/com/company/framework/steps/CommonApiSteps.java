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

    @Then("response status should be {int}")
    public void responseStatusShouldBe(int status) {
        Response response = ScenarioContext.get("lastResponse");
        Assertions.assertStatus(response, status);
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
}
