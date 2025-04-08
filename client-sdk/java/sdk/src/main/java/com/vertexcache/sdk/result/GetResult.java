package com.vertexcache.sdk.result;

public class GetResult extends CommandResult {
    private final String value;

    public GetResult(boolean success, String message, String value) {
        super(success, message);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
