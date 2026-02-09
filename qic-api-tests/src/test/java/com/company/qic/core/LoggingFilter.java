package com.company.qic.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LoggingFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
  private final RedactionFilter redactionFilter = new RedactionFilter();

  @Override
  public Response filter(FilterableRequestSpecification requestSpec,
                         FilterableResponseSpecification responseSpec,
                         FilterContext ctx) {
    Response response = ctx.next(requestSpec, responseSpec);
    if (response.statusCode() >= 400) {
      logRequest(requestSpec);
      logResponse(response);
    }
    return response;
  }

  private void logRequest(FilterableRequestSpecification requestSpec) {
    Map<String, String> headers = new LinkedHashMap<>();
    Headers reqHeaders = requestSpec.getHeaders();
    if (reqHeaders != null) {
      reqHeaders.forEach(header -> headers.put(header.getName(), header.getValue()));
    }
    Map<String, String> redactedHeaders = redactionFilter.redactHeaders(headers);
    String body = requestSpec.getBody() == null ? null : requestSpec.getBody().toString();
    String redactedBody = redactionFilter.redactBody(body);
    LOGGER.warn("Request {} {} Headers={} Body={}", requestSpec.getMethod(), requestSpec.getURI(), redactedHeaders, redactedBody);
  }

  private void logResponse(Response response) {
    String body = response.getBody() == null ? null : response.getBody().asString();
    String redactedBody = redactionFilter.redactBody(body);
    LOGGER.warn("Response Status={} Headers={} Body={}", response.statusCode(), response.getHeaders(), redactedBody);
  }
}
