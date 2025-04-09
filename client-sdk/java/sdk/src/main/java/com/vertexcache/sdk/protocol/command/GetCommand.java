package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.result.VertexCacheSdkException;

public class GetCommand extends BaseCommand<GetCommand> {

    private final String key;
    private String value;

    public GetCommand(String key) {
        if (key == null || key.isBlank()) {
            throw new VertexCacheSdkException("GET command requires a non-empty key");
        }
        this.key = key;
    }

    @Override
    protected String buildCommand() {
        return "GET " + key;
    }

    @Override
    protected void parseResponse(String responseBody) {
        if ("(nil)".equalsIgnoreCase(responseBody)) {
            this.setSuccess("No matching key found, +(nil)");
            return;
        }

        if (responseBody.startsWith("ERR")) {
            setFailure("GET failed: " + responseBody);
        } else {
            this.value = responseBody;
        }
    }


    public String getValue() {
        return value;
    }
}
