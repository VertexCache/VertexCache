
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.server.session.SessionRegistry;

import java.util.Map;

public class SessionCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "SESSIONS";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        Map<String, ClientSessionContext> sessions = SessionRegistry.listAll();

        if (sessions.isEmpty()) {
            response.setResponse("No active sessions.");
            return response;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Active Sessions (").append(sessions.size()).append("):\n");

        sessions.forEach((connectionId, ctx) -> {
            sb.append("  Connection ID: ").append(connectionId).append("\n");
            sb.append("    Client ID: ").append(ctx.getClientId()).append("\n");
            sb.append("    Tenant ID: ").append(ctx.getTenantId()).append("\n");
            sb.append("    Role: ").append(ctx.getRole()).append("\n\n");
        });

        response.setResponse(sb.toString().trim());
        return response;
    }
}
