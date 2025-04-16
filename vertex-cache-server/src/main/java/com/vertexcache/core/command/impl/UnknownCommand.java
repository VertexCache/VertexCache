package com.vertexcache.core.command.impl;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.Command;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.server.session.ClientSessionContext;

public class UnknownCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "UNKNOWN";

    public CommandResponse execute() {
        return execute(null,null);
    }

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setResponseError("Unknown command");
        return commandResponse;
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
