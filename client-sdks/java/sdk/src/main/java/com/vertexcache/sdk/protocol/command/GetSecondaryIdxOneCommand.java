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
import com.vertexcache.sdk.exception.VertexCacheSdkException;

/**
 * Handles the GET Secondary Idx (idx1) command in VertexCache.
 *
 * Retrieves the value for a given key from the cache.
 * Returns an error if the key is missing or expired.
 *
 * Requires the client to have READ, READ_WRITE, or ADMIN access.
 * This command supports primary key lookups only.
 */
public class GetSecondaryIdxOneCommand extends BaseCommand<GetSecondaryIdxOneCommand> {

    private final String key;
    private String value;

    public GetSecondaryIdxOneCommand(String key) {
        if (key == null || key.isBlank()) {
            throw new VertexCacheSdkException("GET By Secondary Index (idx1) command requires a non-empty key");
        }
        this.key = key;
    }

    @Override
    protected String buildCommand() {
        return "GETIDX1 " + key;
    }

    @Override
    protected void parseResponse(String responseBody) {
        if ("(nil)".equalsIgnoreCase(responseBody)) {
            this.setSuccess("No matching key found, +(nil)");
            return;
        }

        if (responseBody.startsWith("ERR")) {
            setFailure("GETIDX1 failed: " + responseBody);
        } else {
            this.value = responseBody;
        }
    }

    public String getValue() {
        return value;
    }
}
