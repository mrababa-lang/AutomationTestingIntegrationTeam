package com.company.qic.core;

import io.github.resilience4j.retry.Retry;
import io.restassured.response.Response;

import java.util.function.Supplier;

public final class ApiClient {
  public Response executeWithRetry(Retry retry, Supplier<Response> call) {
    Supplier<Response> decorated = Retry.decorateSupplier(retry, call);
    return decorated.get();
  }

  public Response execute(Supplier<Response> call) {
    return call.get();
  }
}
