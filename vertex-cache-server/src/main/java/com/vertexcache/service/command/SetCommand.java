package com.vertexcache.service.command;

import com.vertexcache.service.Command;

import java.util.Map;

public class SetCommand implements Command<String> {

    public static final String COMMAND_KEY = "set";

    private Map<String, String> data;

    public SetCommand(Map<String, String> data) {
        this.data = data;
    }

    public String execute(String... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("SET command requires two arguments: key-name and key-value");
        }
        String key = args[0];
        return data.getOrDefault(key, "Key not set");
    }
}
