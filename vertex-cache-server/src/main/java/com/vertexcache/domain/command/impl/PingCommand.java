package com.vertexcache.domain.command.impl;

import com.vertexcache.domain.command.argument.ArgumentParser;
import com.vertexcache.domain.command.Command;
import com.vertexcache.domain.command.CommandResponse;

public class PingCommand implements Command<String> {

    public static final String COMMAND_KEY = "ping";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        if (argumentParser.getPrimaryArgument().isArgsExist()) {
            throw new IllegalArgumentException("PING command does not require any parameters");
        }
        commandResponse.setResponse("PONG");
        return commandResponse;
    }


}
