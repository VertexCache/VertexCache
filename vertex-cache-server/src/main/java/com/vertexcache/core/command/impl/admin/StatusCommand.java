package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.status.SystemStatusReport;
import com.vertexcache.server.session.ClientSessionContext;

public class StatusCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "STATUS";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();
        response.setResponse(SystemStatusReport.getFullSystemReport());
        return response;
    }
}
