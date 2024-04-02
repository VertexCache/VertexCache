package com.vertexcache.service.command;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;
import com.vertexcache.service.Command;

import java.util.Map;

public class GetCommand implements Command<String> {

    public static final String COMMAND_KEY = "get";

    private Map<String, String> data;

    public GetCommand(Map<String, String> data) {
        this.data = data;
    }

    public String execute(String... args) {


        if (args.length != 1) {
            throw new IllegalArgumentException("GET command requires one argument: key-name");
        }
        String key = args[0];
        return data.getOrDefault(key, "Key not found");



    }
}
