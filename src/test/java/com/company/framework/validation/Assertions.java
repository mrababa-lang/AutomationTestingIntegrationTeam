package com.company.framework.validation;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Arrays;

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

    public static void assertStatusIn(Response response, int... statusCodes) {
        boolean matched = Arrays.stream(statusCodes)
                .anyMatch(code -> code == response.getStatusCode());
        Assert.assertTrue(matched, "Unexpected status code: " + response.getStatusCode());
    }
}
