package com.company.qic.config;

public final class TestConfig {
  private static final String DEFAULT_BASE_URL = "https://www.devapi.anoudapps.com";
  private static final String DEFAULT_COMPANY = "002";

  private final Env env;

  public TestConfig(Env env) {
    this.env = env;
  }

  public String baseUrl() {
    return valueOrDefault("baseUrl", DEFAULT_BASE_URL);
  }

  public String username() {
    return env.get("username");
  }

  public String password() {
    return env.get("password");
  }

  public String companyHeader() {
    return valueOrDefault("companyHeader", DEFAULT_COMPANY);
  }

  public boolean openApiEnabled() {
    return env.getBoolean("openApiEnabled", true);
  }

  public String openApiSpecPath() {
    return valueOrDefault("openApiSpecPath", "openapi/qic-aggregator.yaml");
  }

  public boolean schemaValidationEnabled() {
    return env.getBoolean("schemaValidationEnabled", true);
  }

  public boolean retryEnabled() {
    return env.getBoolean("retry.enabled", true);
  }

  public int retryMaxAttempts() {
    return env.getInt("retry.maxAttempts", 3);
  }

  public long retryWaitMs() {
    return env.getLong("retry.waitMs", 500L);
  }

  public String retryOnStatus() {
    return valueOrDefault("retry.retryOnStatus", "429,500,502,503,504");
  }

  public boolean pollEnabled() {
    return env.getBoolean("poll.enabled", false);
  }

  public int pollTimeoutSeconds() {
    return env.getInt("poll.timeoutSeconds", 30);
  }

  public long pollIntervalMs() {
    return env.getLong("poll.intervalMs", 1000L);
  }

  public long maxResponseTimeMs() {
    return env.getLong("response.maxTimeMs", 5000L);
  }

  private String valueOrDefault(String key, String defaultValue) {
    String value = env.get(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
