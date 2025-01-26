package com.vertexcache.server.domain.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.command.Command;
import com.vertexcache.server.domain.command.CommandResponse;
import com.vertexcache.server.domain.command.argument.ArgumentParser;

public class GetSecondaryIdxOneCommand implements Command<String> {

    public static final String COMMAND_KEY = "GETIDX1";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.getBySecondaryKeyIndexOne(argumentParser.getPrimaryArgument().getArgs().getFirst());
                if(value != null) {
                    commandResponse.setResponse(value);
                } else {
                    commandResponse.setResponseNil();
                }
            } else {
                commandResponse.setResponseError(COMMAND_KEY + " command requires a single argument, which is the secondary key (IDX1) of the value you want to retrieve.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError(COMMAND_KEY + " command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }
}
