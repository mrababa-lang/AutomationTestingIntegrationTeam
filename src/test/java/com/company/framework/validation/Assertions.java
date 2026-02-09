package com.company.framework.validation;

import io.restassured.response.Response;
import org.testng.Assert;

public final class Assertions {
    private Assertions() {
    }

    public static void assertStatus(Response response, int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Unexpected status code");
    }

    public static void assertJsonPathEquals(Response response, String jsonPath, String expected) {
        String actual = response.jsonPath().getString(jsonPath);
        Assert.assertEquals(actual, expected, "Unexpected value at json path: " + jsonPath);
    }
}
