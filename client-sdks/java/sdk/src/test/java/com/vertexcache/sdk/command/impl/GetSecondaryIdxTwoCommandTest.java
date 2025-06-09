package com.vertexcache.sdk.command.impl;

import com.vertexcache.sdk.model.VertexCacheSdkException;
import com.vertexcache.sdk.comm.ClientConnectorInterface;
import com.vertexcache.sdk.comm.ClientConnectorMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetSecondaryIdxTwoCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseStartsWithValue() {
        ClientConnectorInterface mock = new ClientConnectorMock("+{\"role\":\"editor\"}");
        GetSecondaryIdxTwoCommand cmd = (GetSecondaryIdxTwoCommand)
                new GetSecondaryIdxTwoCommand("team:1").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("{\"role\":\"editor\"}", cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldReturnNullValueWhenIdxIsNil() {
        ClientConnectorInterface mock = new ClientConnectorMock("+(nil)");
        GetSecondaryIdxTwoCommand cmd = (GetSecondaryIdxTwoCommand)
                new GetSecondaryIdxTwoCommand("missing:idx").execute(mock);

        assertEquals("No matching key found, +(nil)", cmd.getResponse());
        assertTrue(cmd.isSuccess());
        assertNull(cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void buildCommand_shouldBeValid() {
        GetSecondaryIdxTwoCommand cmd = new GetSecondaryIdxTwoCommand("foo:bar");
        assertEquals("GETIDX2 foo:bar", cmd.buildCommand());
    }

    @Test
    void constructor_shouldThrowIfIdx1IsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new GetSecondaryIdxOneCommand(null));
        assertThrows(VertexCacheSdkException.class, () -> new GetSecondaryIdxOneCommand(""));
    }
}
