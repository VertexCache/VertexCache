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
package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;

/**
 * Handles the PING command in VertexCache.
 *
 * This command is used to check server availability and latency.
 * It returns a basic "PONG" response and can be used by clients to verify liveness.
 *
 * PING is always allowed regardless of authentication state or client role.
 * It does not require access validation or key arguments.
 */
public class PingCommand extends BaseCommand<PingCommand> {

    @Override
    protected String buildCommand() {
        return "PING";
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank() || !responseBody.equalsIgnoreCase("PONG")) {
            setFailure("PONG not received");
        }
    }
}
