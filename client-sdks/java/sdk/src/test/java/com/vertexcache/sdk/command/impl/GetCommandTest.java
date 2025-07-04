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

import com.vertexcache.sdk.comm.ClientConnectorInterface;
import com.vertexcache.sdk.comm.ClientConnectorMock;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseStartsWithValue() {
        ClientConnectorInterface mock = new ClientConnectorMock("+{\"name\":\"Alice\"}");
        GetCommand cmd = (GetCommand) new GetCommand("user:1").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("{\"name\":\"Alice\"}", cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldReturnNullValueWhenKeyIsNil() {
        ClientConnectorInterface mock = new ClientConnectorMock("+(nil)");
        GetCommand cmd = (GetCommand) new GetCommand("missing:key").execute(mock);

        assertEquals("No matching key found, +(nil)",cmd.getResponse());
        assertTrue(cmd.isSuccess());
        assertNull(cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void buildCommand_shouldBeValid() {
        GetCommand cmd = new GetCommand("foo");
        assertEquals("GET foo", cmd.buildCommand());
    }

    @Test
    void constructor_shouldThrowIfKeyIsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new GetCommand(null));
        assertThrows(VertexCacheSdkException.class, () -> new GetCommand(""));
    }
}
