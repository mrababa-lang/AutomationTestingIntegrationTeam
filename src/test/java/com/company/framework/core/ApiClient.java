package com.company.framework.core;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    public Response sendGet(String path) {
        RequestSpecification spec = RequestSpecFactory.build();
        return RestAssured.given(spec)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    public Response sendPost(String path, String body) {
        RequestSpecification spec = RequestSpecFactory.build();
        return RestAssured.given(spec)
                .contentType("application/json")
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }
}
