/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.server.session.SessionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Administrative command that returns information about active client sessions.
 *
 * Provides visibility into currently connected clients, including:
 * - Connection identifiers
 * - Assigned roles (e.g., READ, READ_WRITE)
 * - Session durations and activity state
 *
 * Supports optional "pretty" mode for human-readable formatted output,
 * useful for CLI inspection or manual debugging.
 *
 * Requires ADMIN privileges to execute.
 */
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
