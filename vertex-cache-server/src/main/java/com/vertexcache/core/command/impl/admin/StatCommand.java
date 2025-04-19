
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

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
        StringBuilder sb = new StringBuilder();

        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long uptimeSeconds = uptimeMillis / 1000;
        long uptimeMinutes = uptimeSeconds / 60;
        long uptimeHours = uptimeMinutes / 60;

        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        sb.append("STATS\n");
        sb.append("Uptime: ").append(uptimeHours).append("h ")
                .append(uptimeMinutes % 60).append("m ")
                .append(uptimeSeconds % 60).append("s\n");
        sb.append("Memory Usage: ").append(usedMemory).append(" MB used / ")
                .append(maxMemory).append(" MB max\n");

        // Add placeholder for future stats like command count, hit/miss, etc.
        //sb.append("Command Count: (placeholder)\n");
        //sb.append("Cache Hit/Miss: (placeholder)\n");

        response.setResponse(sb.toString().trim());
        return response;
    }
}
