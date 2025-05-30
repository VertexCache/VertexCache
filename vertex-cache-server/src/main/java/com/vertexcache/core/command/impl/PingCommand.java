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
package com.vertexcache.core.command.impl;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.Command;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Lightweight command used to verify that the VertexCache server is reachable and responsive.
 *
 * This is typically used by clients or monitoring systems to check basic liveness
 * without performing any cache operations.
 *
 * Responds with a simple acknowledgment (e.g., "PONG") if the server is healthy.
 * Requires no authentication or elevated privileges.
 *
 * Common use cases include:
 * - Health checks
 * - Connection readiness probes
 * - Network connectivity validation
 */
public class PingCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "PING";

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        if (argumentParser.getPrimaryArgument().isArgsExist()) {
            throw new IllegalArgumentException("PING command does not require any parameters");
        }
        commandResponse.setResponse("PONG");
        return commandResponse;
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
