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
package com.vertexcache.sdk.transport;

/**
 * TcpClientInterface defines the contract for low-level TCP communication used by the VertexCache SDK.
 *
 * Implementations of this interface are responsible for:
 * - Sending framed and encrypted commands to the VertexCache server
 * - Receiving and decoding responses
 * - Managing connection lifecycle including reconnection on failure
 *
 * This abstraction allows the SDK to decouple command logic from underlying transport details,
 * making it easier to mock or substitute transport mechanisms during testing or extension.
 */
public interface TcpClientInterface {
    String send(String message);
    boolean isConnected();
    void close();
}
