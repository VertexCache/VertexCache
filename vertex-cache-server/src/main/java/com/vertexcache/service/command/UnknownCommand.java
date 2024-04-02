package com.vertexcache.service.command;

import com.vertexcache.service.Command;

public class UnknownCommand implements Command<String> {
    public String execute(String... args) {
        return "Unknown command";
    }
}
