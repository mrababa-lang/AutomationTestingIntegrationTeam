package com.company.framework.core;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Logging implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        Map<String, String> requestHeaders = toMap(requestSpec.getHeaders());
        String requestBody = requestSpec.getBody() == null ? null : requestSpec.getBody().toString();
        String method = requestSpec.getMethod();
        String url = requestSpec.getURI();

        Map<String, String> responseHeaders = toMap(response.getHeaders());
        String responseBody = response.getBody() == null ? null : response.getBody().asString();

        Map<String, String> redactedRequestHeaders = RedactionFilter.redactHeaders(requestHeaders);
        Map<String, String> redactedResponseHeaders = RedactionFilter.redactHeaders(responseHeaders);
        String redactedRequestBody = RedactionFilter.redactBody(requestBody);
        String redactedResponseBody = RedactionFilter.redactBody(responseBody);

        String requestAttachment = String.format("METHOD: %s\nURL: %s\nHEADERS: %s\nBODY: %s", method, url, redactedRequestHeaders, redactedRequestBody);
        String responseAttachment = String.format("STATUS: %s\nHEADERS: %s\nBODY: %s", response.getStatusCode(), redactedResponseHeaders, redactedResponseBody);

        ExtentCucumberAdapter.addTestStepLog("Request\n" + requestAttachment);
        ExtentCucumberAdapter.addTestStepLog("Response\n" + responseAttachment);

        ResponseContext context = new ResponseContext(
                response.getStatusCode(),
                responseHeaders,
                responseBody,
                method,
                url
        );
        ScenarioContext.set("response", context);

        return response;
    }

    private Map<String, String> toMap(Headers headers) {
        Map<String, String> map = new LinkedHashMap<>();
        headers.asList().forEach(header -> map.put(header.getName(), header.getValue()));
        return map;
    }
}
