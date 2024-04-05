package com.vertexcache.service.command.impl;

import com.vertexcache.domain.cache.CacheService;
import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

import java.util.Map;

public class GetCommand implements Command<String> {

    public static final String COMMAND_KEY = "get";

    private Map<String, String> data;

    public GetCommand(Map<String, String> data) {
        this.data = data;
    }

    public CommandResponse execute(String... args) {


        if (args.length != 1) {
            throw new IllegalArgumentException("GET command requires one argument: key-name");
        }
        String key = args[0];
       // return data.getOrDefault(key, "Key not found");



        return new CommandResponse(true,"Key not found");


    }
}
