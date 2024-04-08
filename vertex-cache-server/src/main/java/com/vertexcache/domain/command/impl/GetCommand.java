package com.vertexcache.domain.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.domain.cache.Cache;
import com.vertexcache.domain.command.argument.ArgumentParser;
import com.vertexcache.domain.command.Command;
import com.vertexcache.domain.command.CommandResponse;

public class GetCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(GetCommand.class);

    public static final String COMMAND_KEY = "get";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.get(argumentParser.getPrimaryArgument().getArgs().getFirst());
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

    /*
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

     */
}
