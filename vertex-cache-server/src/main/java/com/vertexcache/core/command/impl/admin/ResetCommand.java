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

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Administrative command that performs a full in-memory reset of the VertexCache node.
 *
 * This command clears:
 * - All cache entries
 * - Metrics counters
 * - Alert retry/backoff state
 * - Client connection usage statistics
 *
 * It does not affect persistent configuration or cluster identity.
 * Requires ADMIN privileges and explicit confirmation to execute (RESET CONFIRM).
 *
 * Use this to restore the server to a clean runtime state without restarting the process.
 * Especially useful for testing, debugging, or recovering from a critical in-memory fault.
 */
public class ResetCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "RESET";
    private static final String COMMAND_CONFIRM = "CONFIRM";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session)  {
        CommandResponse response = new CommandResponse();
        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() == 1 && argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_CONFIRM)) {
                Cache.getInstance().clear();
                response.setResponse("OK: Cache has been reset.");
            } else {
                response.setResponseError("ERR_CONFIRM_REQUIRED Reset requires confirmation. Usage: RESET CONFIRM");
            }
        } catch (Exception e) {
            response.setResponseError("ERR_CONFIRM_REQUIRED Failed to reset cache: " + e.getMessage());
        }
        return response;
    }
}
