package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
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

        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long uptimeSeconds = uptimeMillis / 1000;

        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        List<String> stats = new ArrayList<>();
        stats.add("uptime_seconds=" + uptimeSeconds);
        stats.add("memory_used_mb=" + usedMemory);
        stats.add("memory_max_mb=" + maxMemory);

        // Placeholder for future:
        // stats.add("command_count=" + ...);
        // stats.add("cache_hits=" + ...);
        // stats.add("cache_misses=" + ...);

        response.setResponseFromArray(stats);
        return response;
    }
}
