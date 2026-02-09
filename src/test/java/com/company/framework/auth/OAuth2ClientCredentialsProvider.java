package com.company.framework.auth;

import com.company.framework.config.AuthConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OAuth2ClientCredentialsProvider implements AuthProvider {
    private static String accessToken;
    private static Instant expiryTime;

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        String token = getValidToken();
        return spec.header("Authorization", "Bearer " + token);
    }

    private synchronized String getValidToken() {
        if (accessToken != null && expiryTime != null) {
            Instant refreshPoint = expiryTime.minusSeconds(60);
            if (Instant.now().isBefore(refreshPoint)) {
                return accessToken;
            }
        }
        fetchToken();
        return accessToken;
    }

    private void fetchToken() {
        String tokenUrl = AuthConfig.oauthTokenUrl().orElseThrow(() -> new IllegalStateException("OAUTH_TOKEN_URL is required"));
        String clientId = AuthConfig.oauthClientId().orElseThrow(() -> new IllegalStateException("OAUTH_CLIENT_ID is required"));
        String clientSecret = AuthConfig.oauthClientSecret().orElseThrow(() -> new IllegalStateException("OAUTH_CLIENT_SECRET is required"));

        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "client_credentials");
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);
        AuthConfig.oauthScope().ifPresent(scope -> formParams.put("scope", scope));
        AuthConfig.oauthAudience().ifPresent(audience -> formParams.put("audience", audience));

        Response response = RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .post(tokenUrl)
                .then()
                .extract()
                .response();

        if (response.getStatusCode() >= 300) {
            throw new IllegalStateException("OAuth token fetch failed with status " + response.getStatusCode());
        }

        String token = response.jsonPath().getString("access_token");
        Integer expiresIn = response.jsonPath().getInt("expires_in");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("OAuth token response missing access_token");
        }
        accessToken = token;
        long expiresSeconds = expiresIn == null ? 300 : expiresIn;
        expiryTime = Instant.now().plusSeconds(expiresSeconds);
    }
}
