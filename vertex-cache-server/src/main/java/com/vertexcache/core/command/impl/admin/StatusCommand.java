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
import com.vertexcache.core.status.SystemStatusReport;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Administrative command that returns the current status of the VertexCache server.
 *
 * Provides a snapshot of runtime state, including:
 * - Server uptime
 * - Current configuration summary
 * - Role (e.g. PRIMARY, STANDBY)
 * - Cluster status (if clustering is enabled)
 *
 * Supports optional "pretty" mode for human-readable formatted output,
 * useful during manual inspection or command-line usage.
 *
 * Requires ADMIN privileges to execute.
 */
public class StatusCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "STATUS";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        boolean pretty = argumentParser.getPrimaryArgument().getArgs().size() == 1 &&
                argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY);

        if (pretty) {
            response.setResponse(SystemStatusReport.getStatusSummaryAsPretty());
        } else {
            response.setResponseFromArray(SystemStatusReport.getFullSystemReportAsFlat());
        }

        return response;
    }
}
