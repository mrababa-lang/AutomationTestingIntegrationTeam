package com.company.framework.auth;

import com.company.framework.config.AuthConfig;
import io.restassured.specification.RequestSpecification;

public class BearerTokenProvider implements AuthProvider {
    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        String token = AuthConfig.bearerToken().orElse("");
        return spec.header("Authorization", "Bearer " + token);
    }
}
