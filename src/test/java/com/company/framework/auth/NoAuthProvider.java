package com.company.framework.auth;

import io.restassured.specification.RequestSpecification;

public class NoAuthProvider implements AuthProvider {
    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec;
    }
}
