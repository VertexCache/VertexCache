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

import com.vertexcache.sdk.model.VertexCacheSdkException;
import com.vertexcache.sdk.comm.ClientConnectorMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetCommandTest {

    @Test
    void buildCommand_shouldFormatCorrectly() {
        SetCommand cmd = new SetCommand("user:123", "{\"name\":\"Alice\"}");
        String expected = "SET user:123 {\"name\":\"Alice\"}";
        assertEquals(expected, cmd.buildCommand());
    }

    @Test
    void execute_shouldFailWhenResponseIsNotOK() {
        ClientConnectorMock mock = new ClientConnectorMock("+NOT_OK");
        SetCommand cmd = (SetCommand) new SetCommand("key", "value").execute(mock);

        assertFalse(cmd.isSuccess());
        assertEquals("OK Not received", cmd.getError());
    }

    @Test
    void parseResponse_shouldMarkFailureIfResponseIsNotOK() {
        SetCommand cmd = new SetCommand("key", "value");
        cmd.parseResponse("OK Not received");
        assertFalse(cmd.isSuccess());
        assertEquals("OK Not received", cmd.getError());
    }

    @Test
    void constructor_shouldThrowIfKeyOrValueIsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new SetCommand(null, "value"));
        assertThrows(VertexCacheSdkException.class, () -> new SetCommand("key", null));
        assertThrows(VertexCacheSdkException.class, () -> new SetCommand("", "value"));
    }
}
