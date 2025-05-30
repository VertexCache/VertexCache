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

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Administrative command that triggers a graceful shutdown of the VertexCache server.
 *
 * Initiates an orderly shutdown process, including:
 * - Closing all client connections
 * - Flushing any pending operations (if applicable)
 * - Releasing internal resources
 *
 * This command is intended for controlled maintenance workflows or scripted shutdowns.
 * It does not restart the process—external process supervision is required to bring the server back up.
 *
 * Requires ADMIN privileges and explicit confirmation (e.g., SHUTDOWN CONFIRM) to execute.
 */
public class ShutdownCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "SHUTDOWN";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();
        response.setResponse("OK: Shutdown initiated");
        String who = session != null ? session.getClientId() : "unknown";
        LogHelper.getInstance().logInfo("[ADMIN SHUTDOWN] Shutdown triggered by client: " + who);

        // Delay to allow the response to be sent before exit
        new Thread(() -> {
            try {
                Thread.sleep(100); // slight delay
            } catch (InterruptedException ignored) {}
            System.exit(0);
        }).start();

        return response;
    }
}
