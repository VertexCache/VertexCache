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
import com.vertexcache.sdk.command.CommandType
import com.vertexcache.sdk.model.VertexCacheSdkException

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
class SetCommand(
    private val primaryKey: String,
    private val value: String,
    private val secondaryKey: String? = null,
    private val tertiaryKey: String? = null
) : CommandBase<SetCommand>() {

    init {
        if (primaryKey.isBlank()) {
            throw VertexCacheSdkException("Missing Primary Key")
        }
        if (value.isBlank()) {
            throw VertexCacheSdkException("Missing Value")
        }
        if (secondaryKey != null && secondaryKey.isBlank()) {
            throw VertexCacheSdkException("Secondary key can't be empty when used")
        }
        if (!secondaryKey.isNullOrBlank() && tertiaryKey != null && tertiaryKey.isBlank()) {
            throw VertexCacheSdkException("Tertiary key can't be empty when used")
        }
    }

    override fun buildCommand(): String {
        val sb = StringBuilder()
        sb.append(CommandType.SET.keyword)
            .append(COMMAND_SPACER).append(primaryKey)
            .append(COMMAND_SPACER).append(value)

        if (!secondaryKey.isNullOrBlank()) {
            sb.append(" ").append(CommandType.IDX1.keyword).append(" ").append(secondaryKey)
        }

        if (!tertiaryKey.isNullOrBlank()) {
            sb.append(" ").append(CommandType.IDX2.keyword).append(" ").append(tertiaryKey)
        }

        return sb.toString()
    }

    override fun parseResponse(responseBody: String) {
        if (!responseBody.equals("OK", ignoreCase = true)) {
            setFailure("OK Not received")
        } else {
            setSuccess()
        }
    }
}
