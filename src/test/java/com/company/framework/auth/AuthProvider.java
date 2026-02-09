package com.company.framework.auth;

import io.restassured.specification.RequestSpecification;

public interface AuthProvider {
    RequestSpecification apply(RequestSpecification spec);
}
