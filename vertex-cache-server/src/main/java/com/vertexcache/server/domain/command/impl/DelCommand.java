package com.vertexcache.server.domain.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.command.Command;
import com.vertexcache.server.domain.command.CommandResponse;
import com.vertexcache.server.domain.command.argument.ArgumentParser;

public class DelCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(DelCommand.class);

    public static final String COMMAND_KEY = "del";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                // Will remove if exists, if doesn't it simply ignores and the response is still ok
                try {
                    cache.remove(argumentParser.getPrimaryArgument().getArgs().getFirst());
                    commandResponse.setResponseOK();
                } catch (Exception exDel) {
                    // More than likely won't happen
                    commandResponse.setResponseError("DEL command failed, try again.  Possible fatal error with server, check logs.");
                }
            } else {
                commandResponse.setResponseError("DEL command requires a single argument, which is the key of the value you want to remove.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError("DEL command failed, fatal error, check logs.");
            logger.fatal(ex.getMessage());
        }
        return commandResponse;
    }
}
