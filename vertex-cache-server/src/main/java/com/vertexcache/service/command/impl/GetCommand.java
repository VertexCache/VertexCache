package com.vertexcache.service.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.domain.cache.Cache;
import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

import java.util.Map;

public class GetCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(GetCommand.class);

    public static final String COMMAND_KEY = "get";

    private Map<String, String> data;

    public GetCommand(Map<String, String> data) {
        this.data = data;
    }

    public CommandResponse execute(String... args) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (args.length == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.get(args[0]);
                if(value != null) {
                    commandResponse.setResponse(value);
                } else {
                    commandResponse.setResponseNil();
                }
            } else {
                commandResponse.setResponseError("GET command requires a single argument, which is the key of the value you want to retrieve.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError("GET command failed, fatal error, check logs.");
            logger.fatal(ex.getMessage());
        }
        return commandResponse;
    }
}
