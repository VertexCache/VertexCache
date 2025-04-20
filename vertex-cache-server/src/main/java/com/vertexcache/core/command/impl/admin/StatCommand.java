package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.status.SystemStatusReport;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;
import java.util.List;

public class StatCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "STATS";
    private static final long START_TIME = System.currentTimeMillis();

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        if (argumentParser.getPrimaryArgument().getArgs().size() == 1 && argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY)) {
            response.setResponse(SystemStatusReport.getStatusSummaryAsPretty());
        } else {
            response.setResponseFromArray(SystemStatusReport.getFullSystemReportAsFlat());
        }

        return response;
    }

}
