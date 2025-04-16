package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class GetCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "GET";

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String key = KeyPrefixer.prefixKey(argumentParser.getPrimaryArgument().getArgs().getFirst(), session);
                String value = (String) cache.get(key);
                if (value != null) {
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

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
