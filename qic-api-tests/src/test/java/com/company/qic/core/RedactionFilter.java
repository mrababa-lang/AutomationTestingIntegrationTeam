package com.company.qic.core;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class RedactionFilter {
  private static final Set<String> PII_FIELDS = Set.of(
      "emiratesidno",
      "mobile",
      "mobileno",
      "email",
      "passportno",
      "authorization"
  );

  public Map<String, String> redactHeaders(Map<String, String> headers) {
    Map<String, String> redacted = new LinkedHashMap<>();
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (key != null && key.toLowerCase(Locale.ROOT).contains("authorization")) {
        redacted.put(key, "***redacted***");
      } else {
        redacted.put(key, value);
      }
    }
    return redacted;
  }

  public String redactBody(String body) {
    if (body == null) {
      return null;
    }
    String redacted = body;
    for (String field : PII_FIELDS) {
      redacted = redacted.replaceAll("(?i)\"" + field + "\"\\s*:\\s*\"[^\"]*\"", "\"" + field + "\":\"***redacted***\"");
      redacted = redacted.replaceAll("(?i)\"" + field + "\"\\s*:\\s*\\d+", "\"" + field + "\":***redacted***");
    }
    return redacted;
  }
}
