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

/**
 * Command represents a generic interface for all command types that can be executed by the VertexCache SDK.
 *
 * Implementations of this interface must define how a command is converted into its raw byte representation
 * for transmission and identify its associated CommandType.
 *
 * This abstraction allows polymorphic handling of different commands (e.g., GET, SET, DEL) in a unified way,
 * enabling streamlined processing and execution logic within the SDK's transport layer.
 */
public interface CommandInterface {
    CommandInterface execute(ClientConnectorInterface client);
    boolean isSuccess();
    String getResponse();
    String getError();
    String getStatusMessage();
}

