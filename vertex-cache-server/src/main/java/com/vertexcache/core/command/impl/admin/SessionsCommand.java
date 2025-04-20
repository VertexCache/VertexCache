package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.server.session.SessionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SessionsCommand extends AdminCommand<String> {

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

        boolean pretty = argumentParser.getPrimaryArgument().getArgs().size() == 1
                && argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY);

        if (pretty) {
            StringBuilder sb = new StringBuilder();
            sb.append("Active Sessions:").append(System.lineSeparator());
            sb.append("----------------").append(System.lineSeparator());

            for (Map.Entry<String, ClientSessionContext> entry : sessions.entrySet()) {
                ClientSessionContext ctx = entry.getValue();
                sb.append("Connection ID: ").append(entry.getKey()).append(System.lineSeparator());
                sb.append("  Client ID:   ").append(ctx.getClientId()).append(System.lineSeparator());
                sb.append("  Tenant ID:   ").append(ctx.getTenantId()).append(System.lineSeparator());
                sb.append("  Role:        ").append(ctx.getRole()).append(System.lineSeparator());
                sb.append(System.lineSeparator());
            }

            response.setResponse(sb.toString());
        } else {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, ClientSessionContext> entry : sessions.entrySet()) {
                ClientSessionContext ctx = entry.getValue();
                String connectionId = entry.getKey();

                lines.add("connection_id=" + connectionId);
                lines.add("client_id=" + ctx.getClientId());
                lines.add("tenant_id=" + ctx.getTenantId());
                lines.add("role=" + ctx.getRole());
            }

            response.setResponseFromArray(lines);
        }

        return response;
    }


}
