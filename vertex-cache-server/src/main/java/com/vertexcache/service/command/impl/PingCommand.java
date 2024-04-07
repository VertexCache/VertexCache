package com.vertexcache.service.command.impl;

import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

public class PingCommand implements Command<String> {

    public static final String COMMAND_KEY = "ping";

    public CommandResponse execute(String... args) {
        CommandResponse commandResponse = new CommandResponse();
        if (args.length != 0) {
            throw new IllegalArgumentException("PING command does not require any parameters");
        }
        commandResponse.setResponse("PONG");
        return commandResponse;
    }
}
