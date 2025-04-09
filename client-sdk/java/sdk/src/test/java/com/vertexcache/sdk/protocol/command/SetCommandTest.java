package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.result.VertexCacheSdkException;
import com.vertexcache.sdk.transport.TcpClientMock;
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
        TcpClientMock mock = new TcpClientMock("+NOT_OK");
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
