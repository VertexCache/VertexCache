package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;

public class PingCommand extends BaseCommand<PingCommand> {

    @Override
    protected String buildCommand() {
        return "PING";
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank() || !responseBody.equalsIgnoreCase("PONG")) {
            setFailure("PONG not received");
        }
    }
}
