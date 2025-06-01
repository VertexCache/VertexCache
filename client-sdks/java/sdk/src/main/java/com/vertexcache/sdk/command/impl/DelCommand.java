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
package com.vertexcache.sdk.command.impl;

import com.vertexcache.sdk.command.BaseCommand;
import com.vertexcache.sdk.command.CommandType;
import com.vertexcache.sdk.exception.VertexCacheSdkException;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Handles the DEL command in VertexCache.
 *
 * Deletes a key and its associated value from the cache.
 * If the system is configured to allow idempotent deletes,
 * then attempting to delete a non-existent key will still
 * return a success response ("OK DEL (noop)").
 *
 * Requires the client to have WRITE or ADMIN access.
 *
 * Configuration:
 * - del_command_idempotent: when true, deletion of missing keys does not result in an error.
 */
public class DelCommand extends BaseCommand<DelCommand> {

    private final String key;

    public DelCommand(String key) {
        if (key == null || key.isBlank()) {
            throw new VertexCacheSdkException("GET command requires a non-empty key");
        }
        this.key = key;
    }

    @Override
    protected String buildCommand() { return "DEL " + key; }


    @Override
    protected void parseResponse(String responseBody) {
        if (!responseBody.equalsIgnoreCase("OK")) {
            this.setFailure("DEL failed: " + responseBody);
        }
    }
}

