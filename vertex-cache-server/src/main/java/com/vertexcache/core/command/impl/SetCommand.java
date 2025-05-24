package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;

public class SetCommand extends BaseCommand<String> {

    private static final String SUB_ARG_SECONDARY_INDEX_ONE = "IDX1";
    private static final String SUB_ARG_SECONDARY_INDEX_TWO = "IDX2";

    public static final String COMMAND_KEY = "SET";
    private final ArrayList<String> subArguments;

    public SetCommand() {
        this.subArguments = new ArrayList<>();
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_ONE);
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_TWO);
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            argumentParser.setSubArguments(this.subArguments);

            var args = argumentParser.getPrimaryArgument().getArgs();
            if (args.size() != 2) {
                response.setResponseError("SET requires two arguments: key-name and key-value [IDX1] <optional-index-1> [IDX2] <optional-index-2>");
                return response;
            }

            String key = args.get(0);
            String value = args.get(1).replace("\"", "\\\"");

            CacheAccessService service = new CacheAccessService();

            boolean hasIdx1 = argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE)
                    && argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1;
            boolean hasIdx2 = argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO)
                    && argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().size() == 1;

            if (hasIdx1 && hasIdx2) {
                String idx1 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst();
                String idx2 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().getFirst();
                service.put(session, key, value, idx1, idx2);
            } else if (hasIdx1) {
                String idx1 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst();
                service.put(session, key, value, idx1);
            } else {
                service.put(session, key, value);
            }

            response.setResponseOK();

        } catch (Exception ex) {
            response.setResponseError("SET command failed. Check logs.");
            LogHelper.getInstance().logFatal("[SetCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
