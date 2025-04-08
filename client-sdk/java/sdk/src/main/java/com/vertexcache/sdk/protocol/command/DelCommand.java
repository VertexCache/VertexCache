package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.protocol.CommandType;

import java.util.List;
import java.util.StringJoiner;

public class DelCommand extends BaseCommand {

    private final List<String> keys;

    public DelCommand(String key) {
        this(List.of(key));
    }

    public DelCommand(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("DEL command requires at least one key");
        }
        this.keys = keys;
    }

    @Override
    protected String buildCommand() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(CommandType.DEL.toString());
        for (String key : keys) {
            sj.add(key);
        }
        //sj.add("\n");
        return sj.toString();
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (!responseBody.equalsIgnoreCase("OK")) {
            this.setFailure("DEL failed: " + responseBody);
        }
    }

}
