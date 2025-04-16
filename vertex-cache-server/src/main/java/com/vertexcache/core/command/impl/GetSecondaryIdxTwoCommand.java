package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class GetSecondaryIdxTwoCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "GETIDX2";

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1) {
                Cache<Object, Object> cache = Cache.getInstance();
                String idxKey = KeyPrefixer.prefixKey(argumentParser.getPrimaryArgument().getArgs().getFirst(), session);
                String value = (String) cache.getBySecondaryKeyIndexTwo(idxKey);
                if (value != null) {
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

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
