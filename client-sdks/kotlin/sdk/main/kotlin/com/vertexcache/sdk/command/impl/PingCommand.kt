// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// ------------------------------------------------------------------------------
package com.vertexcache.sdk.command.impl

import com.vertexcache.sdk.command.CommandBase

/**
 * Handles the PING command in VertexCache.
 *
 * This command is used to check server availability and latency.
 * It returns a basic "PONG" response and can be used by clients to verify liveness.
 *
 * PING is always allowed regardless of authentication state or client role.
 * It does not require access validation or key arguments.
 */
class PingCommand : CommandBase<PingCommand>() {

    override fun buildCommand(): String {
        return "PING"
    }

    override fun parseResponse(responseBody: String) {
        if (responseBody.isBlank() || !responseBody.equals("PONG", ignoreCase = true)) {
            setFailure("PONG not received")
        }
    }
}
