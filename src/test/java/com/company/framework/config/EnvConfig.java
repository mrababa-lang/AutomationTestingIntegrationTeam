package com.company.framework.config;

public final class EnvConfig {
    private EnvConfig() {
    }

    public static String baseUrl() {
        return TestConfig.getBaseUrl();
    }

    public static String env() {
        return TestConfig.getEnvironment();
    }
}
