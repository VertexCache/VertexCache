package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.result.VertexCacheSdkException;
import com.vertexcache.sdk.transport.TcpClientInterface;
import com.vertexcache.sdk.transport.TcpClientMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseStartsWithValue() {
        TcpClientInterface mock = new TcpClientMock("+{\"name\":\"Alice\"}");
        GetCommand cmd = (GetCommand) new GetCommand("user:1").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("{\"name\":\"Alice\"}", cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldReturnNullValueWhenKeyIsNil() {
        TcpClientInterface mock = new TcpClientMock("+(nil)");
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
