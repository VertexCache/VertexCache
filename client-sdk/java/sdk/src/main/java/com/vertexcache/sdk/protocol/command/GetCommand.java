package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;

public class GetCommand extends BaseCommand {

    private final String key;
    private String value;

    public GetCommand(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("GET command requires a non-empty key");
        }
        this.key = key;
    }

    @Override
    protected String buildCommand() {
        return "GET " + key;
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (responseBody.equalsIgnoreCase("nil") || responseBody.isEmpty()) {
            this.setFailure("Key not found or empty");
        } else {
            this.value = responseBody;
        }
    }

    public String getValue() {
        return value;
    }
}
