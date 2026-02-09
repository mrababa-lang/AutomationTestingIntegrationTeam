package com.company.qic.core;

import com.company.qic.config.TestConfig;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

public final class ResponseSpecFactory {
  private final TestConfig config;

  public ResponseSpecFactory(TestConfig config) {
    this.config = config;
  }

  public void validate(Response response) {
    response.then().time(Matchers.lessThan(config.maxResponseTimeMs()));
    String body = response.getBody() == null ? null : response.getBody().asString();
    if (body != null && !body.isBlank()) {
      response.then().contentType(Matchers.containsString("application/json"));
    }
  }
}
