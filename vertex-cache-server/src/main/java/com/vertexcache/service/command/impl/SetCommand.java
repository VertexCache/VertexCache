package com.vertexcache.service.command.impl;

import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

import java.util.Map;

public class SetCommand implements Command<String> {

    public static final String COMMAND_KEY = "set";

    private Map<String, String> data;

    public SetCommand(Map<String, String> data) {
        this.data = data;
    }

    public CommandResponse execute(String... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("SET command requires two arguments: key-name and key-value");
        }
        String key = args[0];
        //return data.getOrDefault(key, "Key not set");
        return new CommandResponse(true,"Key not set");
    }
}
