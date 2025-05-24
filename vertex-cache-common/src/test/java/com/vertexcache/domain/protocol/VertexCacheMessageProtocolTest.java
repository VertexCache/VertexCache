package com.vertexcache.domain.protocol;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertArrayEquals;

public class VertexCacheMessageProtocolTest {

    @Test
    public void testEncodeString() {
        byte[] expected = "+SUCCESS\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] actual = VertexCacheMessageProtocol.encodeString("SUCCESS");
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEncodeArray() {
        byte[] expected = "[2\r\n#Hello\r\n#World\r\n]\r\n".getBytes(StandardCharsets.UTF_8);

        byte[][] values = {
                "Hello".getBytes(StandardCharsets.UTF_8),
                "World".getBytes(StandardCharsets.UTF_8)
        };

        byte[] actual = VertexCacheMessageProtocol.encodeArray(values);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testError() {
        byte[] expected = "-Error Message\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] actual = VertexCacheMessageProtocol.encodeError("Error Message");
        assertArrayEquals(expected, actual);
    }

}
