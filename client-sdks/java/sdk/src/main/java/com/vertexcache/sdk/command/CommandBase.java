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
package com.vertexcache.sdk.command;

import com.vertexcache.sdk.comm.ClientConnectorInterface;
import com.vertexcache.sdk.model.VertexCacheSdkException;

/**
 * BaseCommand defines the foundational structure for all client-issued commands in the VertexCache SDK.
 *
 * It encapsulates common metadata and behaviors shared by all command types, including:
 * - Command type identification (e.g., GET, SET, DEL)
 * - Internal tracking for retries and timestamps
 * - Role-based authorization levels
 *
 * Subclasses should extend this class to implement specific command logic and payload formatting.
 *
 * This abstraction allows the SDK to handle commands in a consistent, extensible, and testable manner.
 */
public abstract class CommandBase<T extends CommandBase<T>> implements CommandInterface {

    private static String RESPONSE_OK = "OK";
    protected static final String COMMAND_SPACER = " ";

    private boolean success;
    private String response;
    private String error;

    @Override
    public CommandInterface execute(ClientConnectorInterface client) {
        try {
            String raw = client.send(buildCommand()).trim();

            if (raw.startsWith("+")) {
                response = raw.substring(1);
                parseResponse(response);
                if (error == null) {
                    success = true;
                }
            } else if (raw.startsWith("-")) {
                success = false;
                error = raw.substring(1); // remove '-'
            } else {
                success = false;
                error = "Unexpected response: " + raw;
            }

        } catch (VertexCacheSdkException e) {
            success = false;
            error = e.getMessage();
        }
        return this;
    }

    protected abstract String buildCommand();

    protected void parseResponse(String responseBody) {
        // Default: do nothing — override if needed
    }

    public void setFailure(String response) {
        this.success = false;
        this.error = response;
    }

    public void setSuccess() {
        this.success = true;
        this.response = CommandBase.RESPONSE_OK;
        this.error = null;
    }

    public void setSuccess(String response) {
        this.success = true;
        this.response = response;
        this.error = null;
    }

    @Override
    public String getStatusMessage() {
        return isSuccess() ? getResponse() : getError();
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public String getError() {
        return error;
    }
}
