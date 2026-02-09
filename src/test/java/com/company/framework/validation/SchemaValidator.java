package com.company.framework.validation;

import com.company.framework.config.TestConfig;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public final class SchemaValidator {
    private SchemaValidator() {
    }

    public static void validate(Response response, String schemaPath) {
        if (!TestConfig.isSchemaValidationEnabled()) {
            return;
        }
        InputStream stream = SchemaValidator.class.getClassLoader().getResourceAsStream(schemaPath);
        if (stream == null) {
            throw new IllegalStateException("Schema not found: " + schemaPath);
        }
        assertThat(response.getBody().asString(), JsonSchemaValidator.matchesJsonSchema(stream));
    }
}
