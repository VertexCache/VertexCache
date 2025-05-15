package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class GetSecondaryIdxTwoCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "GETIDX2";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() != 1) {
                response.setResponseError("GETIDX2 command requires a single argument: the secondary index (IDX2) key.");
                return response;
            }

            String idxKey = argumentParser.getPrimaryArgument().getArgs().getFirst();
            CacheAccessService service = new CacheAccessService();
            String value = service.getBySecondaryIdx2(session, idxKey);

            if (value != null) {
                response.setResponse(StringUtil.esacpeQuote(value));
            } else {
                response.setResponseNil();
            }

        } catch (Exception ex) {
            response.setResponseError("GETIDX2 command failed. Check logs.");
            LogHelper.getInstance().logFatal("[GetSecondaryIdxTwoCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
