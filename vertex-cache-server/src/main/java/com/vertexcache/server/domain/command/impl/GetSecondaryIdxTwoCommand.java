package com.vertexcache.server.domain.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.command.Command;
import com.vertexcache.server.domain.command.CommandResponse;
import com.vertexcache.server.domain.command.argument.ArgumentParser;

public class GetSecondaryIdxTwoCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(GetSecondaryIdxTwoCommand.class);

    public static final String COMMAND_KEY = "GETIDX2";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.getBySecondaryKeyIndexTwo(argumentParser.getPrimaryArgument().getArgs().getFirst());
                if(value != null) {
                    commandResponse.setResponse(value);
                } else {
                    commandResponse.setResponseNil();
                }
            } else {
                commandResponse.setResponseError(COMMAND_KEY + " command requires a single argument, which is the secondary key (IDX2) of the value you want to retrieve.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError(COMMAND_KEY + " command failed, fatal error, check logs.");
            logger.fatal(ex.getMessage());
        }
        return commandResponse;
    }
}
