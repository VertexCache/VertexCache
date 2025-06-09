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

class DelCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseIsOK() {
        ClientConnectorInterface mock = new ClientConnectorMock("+OK");
        DelCommand cmd = (DelCommand) new DelCommand("key123").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("OK", cmd.getResponse());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldFailWhenResponseIsNotOK() {
        ClientConnectorInterface mock = new ClientConnectorMock("+FAIL");
        DelCommand cmd = (DelCommand) new DelCommand("key123").execute(mock);

        assertFalse(cmd.isSuccess());
        assertEquals("DEL failed: FAIL", cmd.getError());
    }

    @Test
    void buildCommand_shouldFormatCorrectlyForSingleKey() {
        DelCommand cmd = new DelCommand("mykey");
        assertEquals("DEL mykey", cmd.buildCommand());
    }

    @Test
    void constructor_shouldThrowIfKeyListIsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new DelCommand(null));
    }
}
