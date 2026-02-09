package com.company.qic.core;

import com.company.qic.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

public final class RequestSpecFactory {
  private final TestConfig config;
  private final LoggingFilter loggingFilter = new LoggingFilter();

  public RequestSpecFactory(TestConfig config) {
    this.config = config;
  }

  public RequestSpecification create(Endpoint endpoint, boolean useAuth, boolean useCompanyHeader, Filter contractFilter) {
    RequestSpecification spec = RestAssured.given()
        .baseUri(config.baseUrl())
        .contentType("application/json")
        .accept("application/json");

    if (useAuth) {
      spec.auth().preemptive().basic(config.username(), config.password());
    }

    if (useCompanyHeader) {
      spec.header("company", config.companyHeader());
    }

    List<Filter> filters = new ArrayList<>();
    filters.add(loggingFilter);
    if (contractFilter != null) {
      filters.add(contractFilter);
    }
    spec.filters(filters);

    return spec;
  }
}
