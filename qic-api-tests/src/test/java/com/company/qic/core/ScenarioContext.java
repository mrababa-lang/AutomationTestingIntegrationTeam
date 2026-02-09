package com.company.qic.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScenarioContext {
  private final Map<String, Object> data = new ConcurrentHashMap<>();
  private String requestBody;

  public void put(String key, Object value) {
    data.put(key, value);
  }

  public Object get(String key) {
    return data.get(key);
  }

  public Object getOrDefault(String key, Object defaultValue) {
    return data.getOrDefault(key, defaultValue);
  }

  public String getString(String key) {
    Object value = data.get(key);
    return value == null ? null : value.toString();
  }

  public void setRequestBody(String requestBody) {
    this.requestBody = requestBody;
  }

  public String getRequestBody() {
    return requestBody;
  }
}
