package com.vertexcache.domain.command.impl;

import com.vertexcache.domain.command.argument.ArgumentParser;
import com.vertexcache.domain.command.Command;
import com.vertexcache.domain.command.CommandResponse;

public class UnknownCommand implements Command<String> {

    public CommandResponse execute() {
        return execute(null);
    }

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setResponseError("Unknown command");
        return commandResponse;
    }
}
