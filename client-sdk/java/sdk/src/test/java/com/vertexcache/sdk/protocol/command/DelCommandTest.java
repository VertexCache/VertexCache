package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.result.VertexCacheSdkException;
import com.vertexcache.sdk.transport.TcpClientInterface;
import com.vertexcache.sdk.transport.TcpClientMock;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DelCommandTest {

    @Test
    void execute_shouldSucceedWhenResponseIsOK() {
        TcpClientInterface mock = new TcpClientMock("+OK");
        DelCommand cmd = (DelCommand) DelCommand.of("key123").execute(mock);

        assertTrue(cmd.isSuccess());
        assertEquals("OK", cmd.getResponse());
        assertNull(cmd.getError());
    }

    @Test
    void execute_shouldFailWhenResponseIsNotOK() {
        TcpClientInterface mock = new TcpClientMock("+FAIL");
        DelCommand cmd = (DelCommand) DelCommand.of("key123").execute(mock);

        assertFalse(cmd.isSuccess());
        assertEquals("DEL failed: FAIL", cmd.getError());
    }

    @Test
    void buildCommand_shouldFormatCorrectlyForSingleKey() {
        DelCommand cmd = DelCommand.of("mykey");
        assertEquals("DEL mykey", cmd.buildCommand());
    }

    @Test
    void buildCommand_shouldFormatCorrectlyForMultipleKeys() {
        DelCommand cmd = new DelCommand(Arrays.asList("key1", "key2", "key3"));
        assertEquals("DEL key1 key2 key3", cmd.buildCommand());
    }

    @Test
    void constructor_shouldThrowIfKeyListIsNullOrEmpty() {
        assertThrows(VertexCacheSdkException.class, () -> new DelCommand(null));
        assertThrows(VertexCacheSdkException.class, () -> new DelCommand(Collections.emptyList()));
    }
}
