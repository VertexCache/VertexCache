package com.vertexcache.server.domain.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.server.domain.cache.Cache;

import com.vertexcache.server.domain.command.argument.ArgumentParser;
import com.vertexcache.server.domain.command.Command;
import com.vertexcache.server.domain.command.CommandResponse;

import java.util.ArrayList;

public class SetCommand implements Command<String> {

    private static final String SUB_ARG_SECONDARY_INDEX_ONE = "idx1";
    private static final String SUB_ARG_SECONDARY_INDEX_TWO = "idx2";

    public static final String COMMAND_KEY = "set";
    private ArrayList<String> subArguments;

    public SetCommand() {
        this.subArguments = new ArrayList<>();
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_ONE);
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_TWO);
    }

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        try {
            argumentParser.setSubArguments(this.subArguments);

            boolean isPrimaryOK = false;
            if(argumentParser.getPrimaryArgument().isArgsExist() && argumentParser.getPrimaryArgument().getArgs().size() == 2) {
                isPrimaryOK = true;
            } else {
                System.out.println("failed ====> " + argumentParser.getPrimaryArgument().getArgs().size());
            }

            if(isPrimaryOK) {
                Cache<Object, Object> cache = Cache.getInstance();

                if (isPrimaryOK &&
                        argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1 &&
                        argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().size() == 1
                ) {
                    // 2 Secondary Indexes
                    cache.put(
                            argumentParser.getPrimaryArgument().getArgs().get(0),
                            argumentParser.getPrimaryArgument().getArgs().get(1),
                            argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst(),
                            argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().getFirst()
                    );
                    commandResponse.setResponseOK();
                } else if (isPrimaryOK &&
                        argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE) &&
                        argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1 &&
                        !argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO)) {
                    // 1 Secondary Index
                    cache.put(
                            argumentParser.getPrimaryArgument().getArgs().get(0),
                            argumentParser.getPrimaryArgument().getArgs().get(1),
                            argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst()
                    );
                    commandResponse.setResponseOK();
                } else if (isPrimaryOK) {
                    // No Secondary Indexes
                    cache.put(
                            argumentParser.getPrimaryArgument().getArgs().get(0),
                            argumentParser.getPrimaryArgument().getArgs().get(1)
                    );
                    commandResponse.setResponseOK();
                } else {
                    commandResponse.setResponseError("SET command requires two arguments: key-name and key-value [IDX1] <optional-secondary-index-1> [IDX2] <optional-secondary-index-2>");
                }

            } else {
                commandResponse.setResponseError("SET command requires two arguments: key-name and key-value [IDX1] <optional-secondary-index-1> [IDX2] <optional-secondary-index-2>");
            }

        } catch (Exception ex) {
            commandResponse.setResponseError("GET command failed, fatal error, check logs.");
            LogHelper.getInstance().logFatal(ex.getMessage());
        }
        return commandResponse;
    }
}
