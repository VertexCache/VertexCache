package com.vertexcache.sdk.protocol.command;
import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.protocol.CommandType;
import com.vertexcache.sdk.result.VertexCacheSdkException;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class DelCommand extends BaseCommand<DelCommand> {

    private final List<String> keys;

    public DelCommand(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new VertexCacheSdkException("DEL command requires at least one key");
        }
        this.keys = keys;
    }

    public static DelCommand of(String key) {
        return new DelCommand(Collections.singletonList(key));
    }

    @Override
    protected String buildCommand() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(CommandType.DEL.toString());
        for (String key : keys) {
            sj.add(key);
        }
        return sj.toString();
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (!responseBody.equalsIgnoreCase("OK")) {
            this.setFailure("DEL failed: " + responseBody);
        }
    }
}

