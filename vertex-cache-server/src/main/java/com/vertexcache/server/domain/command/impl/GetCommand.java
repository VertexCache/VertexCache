package com.vertexcache.server.domain.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.command.argument.ArgumentParser;
import com.vertexcache.server.domain.command.Command;
import com.vertexcache.server.domain.command.CommandResponse;

public class GetCommand implements Command<String> {

    public static final String COMMAND_KEY = "get";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.get(argumentParser.getPrimaryArgument().getArgs().getFirst());
                if(value != null) {
                    commandResponse.setResponse(StringUtil.esacpeQuote(value));
                } else {
                    commandResponse.setResponseNil();
                }
            } else {
                commandResponse.setResponseError("GET command requires a single argument, which is the key of the value you want to retrieve.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError("GET command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }
}
