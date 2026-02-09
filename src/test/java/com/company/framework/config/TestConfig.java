package com.company.framework.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class TestConfig {
    private static final String DEFAULT_BASE_URL = "https://httpbin.org";
    private static final String DEFAULT_AUTH_TYPE = "none";
    private static final boolean DEFAULT_SCHEMA_VALIDATION_ENABLED = true;

    private TestConfig() {
    }

    public static String getBaseUrl() {
        return getValue("baseUrl", "BASE_URL").orElse(DEFAULT_BASE_URL);
    }

    public static String getEnvironment() {
        return getValue("env", "ENV").orElse("dev");
    }

    public static String getAuthType() {
        return getValue("authType", "AUTH_TYPE").orElse(DEFAULT_AUTH_TYPE).toLowerCase(Locale.ROOT);
    }

    public static boolean isSchemaValidationEnabled() {
        return getValue("schemaValidationEnabled", "SCHEMA_VALIDATION_ENABLED")
                .map(Boolean::parseBoolean)
                .orElse(DEFAULT_SCHEMA_VALIDATION_ENABLED);
    }

    public static List<String> getRedactedFields() {
        String raw = getValue("redactedFields", "REDACTED_FIELDS").orElse("password,emiratesId,mobile");
        if (raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(raw.split(","));
    }

    private static Optional<String> getValue(String systemKey, String envKey) {
        String sysValue = System.getProperty(systemKey);
        if (sysValue != null && !sysValue.isBlank()) {
            return Optional.of(sysValue.trim());
        }
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return Optional.of(envValue.trim());
        }
        return Optional.empty();
    }
}
