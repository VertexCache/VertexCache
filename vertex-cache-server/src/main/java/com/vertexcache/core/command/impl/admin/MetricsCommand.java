package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;
import java.util.List;

public class MetricsCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "METRICS";
    private static final long START_TIME = System.currentTimeMillis();

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        boolean pretty = argumentParser.getPrimaryArgument().getArgs().size() == 1 &&
                argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY);

        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long uptimeSeconds = uptimeMillis / 1000;
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        if (pretty) {
            String output = String.join(System.lineSeparator(),
                    "Runtime Stats:",
                    "--------------",
                    "Uptime:       " + uptimeSeconds + " seconds",
                    "Memory Used:  " + usedMemory + " MB",
                    "Memory Max:   " + maxMemory + " MB"
            );
            response.setResponse(output);
        } else {
            List<String> stats = new ArrayList<>();
            stats.add("uptime_seconds=" + uptimeSeconds);
            stats.add("memory_used_mb=" + usedMemory);
            stats.add("memory_max_mb=" + maxMemory);
            response.setResponseFromArray(stats);
        }

        return response;
    }
}
