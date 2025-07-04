package com.vertexcache.sdk.command.impl;

import com.vertexcache.sdk.comm.ClientConnectorInterface;
import com.vertexcache.sdk.comm.ClientConnectorMock;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetSecondaryIdxOneCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseStartsWithValue() {
        ClientConnectorInterface mock = new ClientConnectorMock("+{\"role\":\"editor\"}");
        GetSecondaryIdxOneCommand cmd = (GetSecondaryIdxOneCommand)
                new GetSecondaryIdxOneCommand("team:1").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("{\"role\":\"editor\"}", cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldReturnNullValueWhenIdxIsNil() {
        ClientConnectorInterface mock = new ClientConnectorMock("+(nil)");
        GetSecondaryIdxOneCommand cmd = (GetSecondaryIdxOneCommand)
                new GetSecondaryIdxOneCommand("missing:idx").execute(mock);

        assertEquals("No matching key found, +(nil)", cmd.getResponse());
        assertTrue(cmd.isSuccess());
        assertNull(cmd.getValue());
        assertNull(cmd.getError());
    }

    @Test
    void buildCommand_shouldBeValid() {
        GetSecondaryIdxOneCommand cmd = new GetSecondaryIdxOneCommand("foo:bar");
        assertEquals("GETIDX1 foo:bar", cmd.buildCommand());
    }

    @Test
    void constructor_shouldThrowIfIdx1IsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new GetSecondaryIdxOneCommand(null));
        assertThrows(VertexCacheSdkException.class, () -> new GetSecondaryIdxOneCommand(""));
    }
}
