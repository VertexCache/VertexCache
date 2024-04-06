package com.vertexcache.service.command.impl;

import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

public class UnknownCommand implements Command<String> {
    public CommandResponse execute(String... args) {
        return (new CommandResponse(false,"Unknown command"));
    }
}
