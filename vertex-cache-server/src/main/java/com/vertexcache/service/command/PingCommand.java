package com.vertexcache.service.command;

import com.vertexcache.service.Command;

public class PingCommand implements Command<String> {

    public static final String COMMAND_KEY = "ping";

    public String execute(String... args) {
        if (args.length != 0) {
            throw new IllegalArgumentException("PING command does not require any parameters");
        }
        return "PONG";
    }
}
