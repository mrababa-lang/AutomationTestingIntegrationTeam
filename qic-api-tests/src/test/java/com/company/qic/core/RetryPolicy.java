package com.company.qic.core;

import com.company.qic.config.TestConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class RetryPolicy {
  private static final Logger LOGGER = LoggerFactory.getLogger(RetryPolicy.class);

  private final RetryConfig retryConfig;
  private final Set<Integer> retryOn;

  public RetryPolicy(TestConfig config) {
    this.retryOn = Arrays.stream(config.retryOnStatus().split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .map(Integer::parseInt)
        .collect(Collectors.toSet());

    this.retryConfig = RetryConfig.custom()
        .maxAttempts(config.retryMaxAttempts())
        .intervalFunction(IntervalFunction.of(Duration.ofMillis(config.retryWaitMs())))
        .retryOnException(throwable -> true)
        .retryOnResult(result -> {
          if (result instanceof io.restassured.response.Response response) {
            return retryOn.contains(response.statusCode());
          }
          return false;
        })
        .build();
  }

  public Retry create(String name) {
    Retry retry = Retry.of(name, retryConfig);
    retry.getEventPublisher()
        .onRetry(event -> LOGGER.warn("Retry attempt {} for {} due to {}", event.getNumberOfRetryAttempts(), name,
            event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : event.getResult()))
        .onError(event -> LOGGER.warn("Retry failed for {} after {} attempts", name, event.getNumberOfRetryAttempts()));
    return retry;
  }

  public boolean isRetryableStatus(int statusCode) {
    return retryOn.contains(statusCode);
  }
}
