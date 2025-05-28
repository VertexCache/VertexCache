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

import com.vertexcache.sdk.transport.TcpClientInterface;
import com.vertexcache.sdk.transport.TcpClientMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseIsOK() {
        TcpClientInterface mock = new TcpClientMock("+PONG");
        PingCommand cmd = (PingCommand) new PingCommand().execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("PONG", cmd.getResponse());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldFailWhenResponseIsNotOK() {
        TcpClientInterface mock = new TcpClientMock("+WRONG");
        PingCommand cmd = (PingCommand) new PingCommand().execute(mock);

        assertFalse(cmd.isSuccess());
        assertEquals("PONG not received", cmd.getError());
    }

    @Test
    void buildCommand_shouldBePING() {
        PingCommand cmd = new PingCommand();
        assertEquals("PING", cmd.buildCommand());
    }
}
