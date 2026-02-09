package com.company.framework.core;

import com.company.framework.config.AuthConfig;
import com.company.framework.config.TestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class RedactionFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RedactionFilter() {
    }

    public static Map<String, String> redactHeaders(Map<String, String> headers) {
        Map<String, String> redacted = new HashMap<>();
        String apiKeyHeader = AuthConfig.apiKeyHeaderName().orElse("x-api-key");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String name = entry.getKey();
            if (name.equalsIgnoreCase("authorization") || name.equalsIgnoreCase(apiKeyHeader)) {
                redacted.put(name, "***");
            } else {
                redacted.put(name, entry.getValue());
            }
        }
        return redacted;
    }

    public static String redactBody(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }
        try {
            JsonNode root = MAPPER.readTree(body);
            List<String> fields = new ArrayList<>();
            for (String field : TestConfig.getRedactedFields()) {
                fields.add(field.trim());
            }
            if (fields.isEmpty()) {
                return body;
            }
            redactNode(root, fields);
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception ignored) {
            return body;
        }
    }

    private static void redactNode(JsonNode node, List<String> fields) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode child = objectNode.get(fieldName);
                if (fields.contains(fieldName)) {
                    objectNode.put(fieldName, "***");
                } else {
                    redactNode(child, fields);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                redactNode(child, fields);
            }
        }
    }
}
