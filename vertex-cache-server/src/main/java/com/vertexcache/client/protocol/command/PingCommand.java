package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;

public class PingCommand extends BaseCommand<PingCommand> {

    public static final String COMMAND_KEY = "PING";

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

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
