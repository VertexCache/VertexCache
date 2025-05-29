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
package com.vertexcache.sdk.transport.protocol;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.ByteBuffer;

public class MessageCodecTest {

    @Test
    public void testWriteThenReadFramedMessage() throws IOException {
        String original = "Hello VertexCache";
        byte[] payload = original.getBytes();

        // Write
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(out, payload);

        // Read
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] result = MessageCodec.readFramedMessage(in);

        assertNotNull(result);
        assertArrayEquals(payload, result);
    }

    @Test
    public void testInvalidVersionByte() {
        byte[] badFrame = ByteBuffer.allocate(4 + 1 + 3)
                .putInt(3)         // length
                .put((byte) 0x02)  // invalid version
                .put("abc".getBytes())
                .array();

        ByteArrayInputStream in = new ByteArrayInputStream(badFrame);
        assertThrows(IOException.class, () -> MessageCodec.readFramedMessage(in));
    }

    @Test
    public void testTooShortHeaderReturnsNull() throws IOException {
        byte[] bad = new byte[] {0x01, 0x02}; // < 5 bytes
        ByteArrayInputStream in = new ByteArrayInputStream(bad);

        assertNull(MessageCodec.readFramedMessage(in));
    }

    @Test
    public void testTooLargePayloadRejected() {
        byte[] bigPayload = new byte[MessageCodec.MAX_MESSAGE_SIZE + 1];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThrows(IOException.class, () -> MessageCodec.writeFramedMessage(out, bigPayload));
    }
}
