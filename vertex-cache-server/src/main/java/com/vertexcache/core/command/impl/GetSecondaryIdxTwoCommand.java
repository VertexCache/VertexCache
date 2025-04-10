package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.command.Command;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;

public class GetSecondaryIdxTwoCommand implements Command<String> {

    public static final String COMMAND_KEY = "GETIDX2";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String value = (String) cache.getBySecondaryKeyIndexTwo(argumentParser.getPrimaryArgument().getArgs().getFirst());
                if(value != null) {
                    commandResponse.setResponse(StringUtil.esacpeQuote(value));
                } else {
                    commandResponse.setResponseNil();
                }
            } else {
                commandResponse.setResponseError(COMMAND_KEY + " command requires a single argument, which is the secondary key (IDX2) of the value you want to retrieve.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError(COMMAND_KEY + " command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }
}
