package com.company.qic.core;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import io.restassured.response.Response;

public final class SchemaValidator {
  public void validate(Response response, String schemaPath) {
    response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
  }
}
