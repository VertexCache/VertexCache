package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;

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
