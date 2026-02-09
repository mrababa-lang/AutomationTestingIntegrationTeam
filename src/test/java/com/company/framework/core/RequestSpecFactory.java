package com.company.framework.core;

import com.company.framework.auth.ApiKeyAuthProvider;
import com.company.framework.auth.AuthProvider;
import com.company.framework.auth.BasicAuthProvider;
import com.company.framework.auth.BearerTokenProvider;
import com.company.framework.auth.NoAuthProvider;
import com.company.framework.auth.OAuth2ClientCredentialsProvider;
import com.company.framework.config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;
import java.util.Map;

public final class RequestSpecFactory {
    private RequestSpecFactory() {
    }

    public static RequestSpecification build() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(TestConfig.getBaseUrl());
        builder.addHeader("Accept", "application/json");
        builder.addFilter(new Logging());

        RequestSpecification spec = builder.build();
        spec = applyHeaders(spec);
        spec = getAuthProvider().apply(spec);
        return spec;
    }

    @SuppressWarnings("unchecked")
    private static RequestSpecification applyHeaders(RequestSpecification spec) {
        Map<String, String> headers = ScenarioContext.get("headers");
        if (headers != null) {
            headers.forEach(spec::header);
        }
        return spec;
    }

    private static AuthProvider getAuthProvider() {
        String override = ScenarioContext.get("authTypeOverride");
        String authType = override != null ? override : TestConfig.getAuthType();
        switch (authType.toLowerCase(Locale.ROOT)) {
            case "basic":
                return new BasicAuthProvider();
            case "bearer":
                return new BearerTokenProvider();
            case "oauth2_client_credentials":
            case "oauth2":
                return new OAuth2ClientCredentialsProvider();
            case "api_key":
            case "apikey":
                return new ApiKeyAuthProvider();
            case "none":
            default:
                return new NoAuthProvider();
        }
    }
}
