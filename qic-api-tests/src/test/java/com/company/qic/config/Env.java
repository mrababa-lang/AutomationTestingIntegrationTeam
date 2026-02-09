package com.company.qic.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Env {
  private final Properties properties = new Properties();

  public Env(String resourcePath) {
    if (resourcePath == null) {
      return;
    }
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (input != null) {
        properties.load(input);
      }
    } catch (IOException ignored) {
      // Best-effort loading; missing file is allowed.
    }
  }

  public String get(String key) {
    String sys = System.getProperty(key);
    if (sys != null && !sys.isBlank()) {
      return sys;
    }
    String env = System.getenv(key);
    if (env != null && !env.isBlank()) {
      return env;
    }
    return properties.getProperty(key);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    String value = get(key);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value.trim());
  }

  public int getInt(String key, int defaultValue) {
    String value = get(key);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    return Integer.parseInt(value.trim());
  }

  public long getLong(String key, long defaultValue) {
    String value = get(key);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    return Long.parseLong(value.trim());
  }
}
