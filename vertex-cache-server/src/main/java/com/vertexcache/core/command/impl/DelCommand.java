package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.service.CacheAccessService;
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

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() != 1) {
                response.setResponseError("DEL command requires a single argument: the key to remove.");
                return response;
            }

            String key = argumentParser.getPrimaryArgument().getArgs().getFirst();
            CacheAccessService service = new CacheAccessService();
            service.remove(session, key);
            response.setResponseOK();

        } catch (Exception ex) {
            response.setResponseError("DEL command failed. Check logs.");
            LogHelper.getInstance().logFatal("[DelCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
