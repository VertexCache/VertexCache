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
