package com.company.framework.auth;

import com.company.framework.config.AuthConfig;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BasicAuthProvider implements AuthProvider {
    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        String username = AuthConfig.basicUsername().orElse("");
        String password = AuthConfig.basicPassword().orElse("");
        return spec.auth().preemptive().basic(username, password);
    }
}
