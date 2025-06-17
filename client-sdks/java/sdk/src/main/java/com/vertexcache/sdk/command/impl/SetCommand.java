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

import com.vertexcache.sdk.command.CommandBase;
import com.vertexcache.sdk.command.CommandType;
import com.vertexcache.sdk.model.VertexCacheSdkException;

/**
 * Handles the SET command in VertexCache.
 *
 * Stores a value in the cache under the specified key, optionally assigning
 * secondary (idx1) and tertiary (idx2) indexes for lookup. Existing keys will
 * be overwritten. Supports expiration and format validation if configured.
 *
 * Requires the client to have WRITE or ADMIN access.
 *
 * Validation:
 * - Key and value are required arguments.
 * - Optional arguments may include index fields and TTL metadata.
 */
public class SetCommand extends CommandBase<SetCommand> {

    private final String primaryKey;
    private final String value;
    private final String secondaryKey;
    private final String tertiaryKey;

    public SetCommand(String primaryKey, String value) throws VertexCacheSdkException {
        this(primaryKey, value, null, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey) throws VertexCacheSdkException {
        this(primaryKey, value, secondaryKey, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey, String tertiaryKey) throws VertexCacheSdkException {

        if(primaryKey == null || primaryKey.isBlank()) {
            throw new VertexCacheSdkException("Missing Primary Key");
        }

        if(value == null || value.isBlank()) {
            throw new VertexCacheSdkException("Missing Value");
        }

        if(secondaryKey != null && secondaryKey.isBlank()) {
            throw new VertexCacheSdkException("Secondary key can't be empty when used");
        }

        if(secondaryKey != null && !secondaryKey.isBlank() && tertiaryKey != null && tertiaryKey.isBlank()) {
            throw new VertexCacheSdkException("Tertiary key can't be empty when used");
        }

        this.primaryKey = primaryKey;
        this.value = value;
        this.secondaryKey = secondaryKey;
        this.tertiaryKey = tertiaryKey;
    }

    @Override
    protected String buildCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append(CommandType.SET).append(CommandBase.COMMAND_SPACER)
                .append(primaryKey).append(CommandBase.COMMAND_SPACER)
                .append(value);

        if (secondaryKey != null && !secondaryKey.isBlank()) {
            sb.append(" ").append(CommandType.IDX1).append(" ").append(secondaryKey);
        }

        if (tertiaryKey != null && !tertiaryKey.isBlank()) {
            sb.append(" ").append(CommandType.IDX2).append(" ").append(tertiaryKey);
        }

        return sb.toString();
    }

    protected void parseResponse(String responseBody) {
        if(!responseBody.equalsIgnoreCase("OK")) {
            this.setFailure("OK Not received");
        } else {
            this.setSuccess();
        }
    }
}
