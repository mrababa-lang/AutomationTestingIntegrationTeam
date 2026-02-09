package com.company.framework.core;

import java.util.Map;

public class ResponseContext {
    private final int statusCode;
    private final Map<String, String> responseHeaders;
    private final String responseBody;
    private final String method;
    private final String url;

    public ResponseContext(int statusCode, Map<String, String> responseHeaders, String responseBody, String method, String url) {
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.method = method;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
