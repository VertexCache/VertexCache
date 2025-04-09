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
