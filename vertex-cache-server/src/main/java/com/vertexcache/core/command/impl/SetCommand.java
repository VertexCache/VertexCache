package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;

public class SetCommand extends BaseCommand<String> {

    private static final String SUB_ARG_SECONDARY_INDEX_ONE = "IDX1";
    private static final String SUB_ARG_SECONDARY_INDEX_TWO = "IDX2";

    public static final String COMMAND_KEY = "SET";
    private ArrayList<String> subArguments;

    public SetCommand() {
        this.subArguments = new ArrayList<>();
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_ONE);
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_TWO);
    }

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            argumentParser.setSubArguments(this.subArguments);

            boolean isPrimaryOK = false;
            if(argumentParser.getPrimaryArgument().isArgsExist() && argumentParser.getPrimaryArgument().getArgs().size() == 2) {
                isPrimaryOK = true;
            }

            if(isPrimaryOK) {
                Cache<Object, Object> cache = Cache.getInstance();

                String primaryKey = KeyPrefixer.prefixKey(argumentParser.getPrimaryArgument().getArgs().get(0), session);
                String value = argumentParser.getPrimaryArgument().getArgs().get(1).replace("\"", "\\\"");

                if (argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1 &&
                        argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().size() == 1) {

                    String idx1 = KeyPrefixer.prefixKey(argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst(), session);
                    String idx2 = KeyPrefixer.prefixKey(argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().getFirst(), session);
                    cache.put(primaryKey, value, idx1, idx2);
                    commandResponse.setResponseOK();

                } else if (argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1 &&
                        !argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO)) {

                    String idx1 = KeyPrefixer.prefixKey(argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst(), session);
                    cache.put(primaryKey, value, idx1);
                    commandResponse.setResponseOK();
                } else {
                    cache.put(primaryKey, value);
                    commandResponse.setResponseOK();
                }

            } else {
                commandResponse.setResponseError("SET command requires two arguments: key-name and key-value [IDX1] <optional-secondary-index-1> [IDX2] <optional-secondary-index-2>");
            }

        } catch (Exception ex) {
            commandResponse.setResponseError("SET command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
