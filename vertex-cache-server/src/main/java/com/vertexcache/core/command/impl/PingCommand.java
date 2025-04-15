package com.vertexcache.core.command.impl;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.Command;
import com.vertexcache.core.command.CommandResponse;

public class PingCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "PING";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        if (argumentParser.getPrimaryArgument().isArgsExist()) {
            throw new IllegalArgumentException("PING command does not require any parameters");
        }
        commandResponse.setResponse("PONG");
        return commandResponse;
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
