package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class ResetCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "RESET";
    private static final String COMMAND_CONFIRM = "CONFIRM";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session)  {
        CommandResponse response = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1 && argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_CONFIRM)) {
                Cache.getInstance().clear();
                response.setResponse("OK: Cache has been reset.");
            } else {
                response.setResponseError("ERR_CONFIRM_REQUIRED Reset requires confirmation. Usage: RESET CONFIRM");
            }
        } catch (Exception e) {
            response.setResponseError("ERR_CONFIRM_REQUIRED Failed to reset cache: " + e.getMessage());
        }
        return response;
    }
}
