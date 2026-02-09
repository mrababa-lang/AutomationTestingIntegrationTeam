package com.company.framework.auth;

import com.company.framework.config.AuthConfig;
import io.restassured.specification.RequestSpecification;

public class ApiKeyAuthProvider implements AuthProvider {
    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        String headerName = AuthConfig.apiKeyHeaderName().orElse("x-api-key");
        String value = AuthConfig.apiKeyValue().orElse("");
        return spec.header(headerName, value);
    }
}
