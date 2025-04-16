package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class DelCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "DEL";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String key = KeyPrefixer.prefixKey(argumentParser.getPrimaryArgument().getArgs().getFirst(), session);
                cache.remove(key);
                commandResponse.setResponseOK();
            } else {
                commandResponse.setResponseError("DEL command requires a single argument, which is the key of the value you want to remove.");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError("DEL command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }
}
