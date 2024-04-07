package com.vertexcache.service.command.impl;

import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

public class UnknownCommand implements Command<String> {
    public CommandResponse execute(String... args) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setResponseError("Unknown command");
        return commandResponse;
    }
}
