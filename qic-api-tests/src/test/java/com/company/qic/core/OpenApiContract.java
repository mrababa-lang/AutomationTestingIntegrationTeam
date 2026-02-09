package com.company.qic.core;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.Filter;

public final class OpenApiContract {
  private final OpenApiValidationFilter filter;

  public OpenApiContract(String specResourcePath) {
    this.filter = new OpenApiValidationFilter(specResourcePath);
  }

  public Filter filter() {
    return filter;
  }
}
