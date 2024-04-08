package com.vertexcache.domain.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.domain.command.argument.ArgumentParser;
import com.vertexcache.domain.command.Command;
import com.vertexcache.domain.command.CommandResponse;

public class SetCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(SetCommand.class);

    public static final String COMMAND_KEY = "set";

    public CommandResponse execute(ArgumentParser argumentParser) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setResponseError("SET command failed, fatal error, check logs.");
        return commandResponse;
        /*
        CommandResponse commandResponse = new CommandResponse();

        try {

            int numArgs = args.length;

            if (numArgs >= 2 && numArgs <= 4) {
                Cache<Object, Object> cache = Cache.getInstance();
                switch(numArgs) {

                    case 2:

                        System.out.println(args[0] + " " + args[1]);

                        cache.put(args[0],args[1]);
                        commandResponse.setResponseOK();
                        break;

                    case 3:
                        cache.put(args[0],args[1],args[2]);
                        commandResponse.setResponseOK();
                        break;

                    case 4:
                        cache.put(args[0],args[1],args[2],args[3]);
                        commandResponse.setResponseOK();
                        break;

                    default:
                        // Should NOT happen, already checked
                        commandResponse.setResponseError("SET command requires two arguments: key-name and key-value <optional-secondary-index-1> <optional-secondary-index-2>");
                        break;
                }
            } else {
                commandResponse.setResponseError("SET command requires two arguments: key-name and key-value <optional-secondary-index-1> <optional-secondary-index-2>");
            }
        } catch (Exception ex) {
            commandResponse.setResponseError("SET command failed, fatal error, check logs.");
            logger.fatal(ex.getMessage());
        }
        return commandResponse;

         */
    }
}
