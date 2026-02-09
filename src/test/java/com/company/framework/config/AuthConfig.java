package com.company.framework.config;

import java.util.Optional;

public final class AuthConfig {
    private AuthConfig() {
    }

    public static String authType() {
        return TestConfig.getAuthType();
    }

    public static Optional<String> basicUsername() {
        return getValue("basicUsername", "BASIC_USERNAME");
    }

    public static Optional<String> basicPassword() {
        return getValue("basicPassword", "BASIC_PASSWORD");
    }

    public static Optional<String> bearerToken() {
        return getValue("bearerToken", "BEARER_TOKEN");
    }

    public static Optional<String> oauthTokenUrl() {
        return getValue("oauthTokenUrl", "OAUTH_TOKEN_URL");
    }

    public static Optional<String> oauthClientId() {
        return getValue("oauthClientId", "OAUTH_CLIENT_ID");
    }

    public static Optional<String> oauthClientSecret() {
        return getValue("oauthClientSecret", "OAUTH_CLIENT_SECRET");
    }

    public static Optional<String> oauthScope() {
        return getValue("oauthScope", "OAUTH_SCOPE");
    }

    public static Optional<String> oauthAudience() {
        return getValue("oauthAudience", "OAUTH_AUDIENCE");
    }

    public static Optional<String> apiKeyHeaderName() {
        return getValue("apiKeyHeaderName", "API_KEY_HEADER_NAME");
    }

    public static Optional<String> apiKeyValue() {
        return getValue("apiKeyValue", "API_KEY_VALUE");
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
