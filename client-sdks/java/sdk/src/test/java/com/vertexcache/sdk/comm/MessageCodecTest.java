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
package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.comm.MessageCodec;
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
    public void testInvalidProtocolVersionShouldNotThrowButCapture() throws IOException {
        int invalidVersion = 0xDEADBEEF;
        byte[] payload = "abc".getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(8 + payload.length);
        buffer.putInt(payload.length);        // Length = 3
        buffer.putInt(invalidVersion);        // Invalid version
        buffer.put(payload);

        ByteArrayInputStream in = new ByteArrayInputStream(buffer.array());

        // This does not throw by default — protocolVersion is just updated
        byte[] result = MessageCodec.readFramedMessage(in);
        assertNotNull(result);
        assertEquals("abc", new String(result));
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

    @Test
    public void testWriteEmptyPayloadThenReadShouldFail() throws IOException {
        byte[] payload = new byte[0];

        // Writing should still be allowed
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(out, payload);
        byte[] framed = out.toByteArray();

        // Header is now 8 bytes: 4 (length) + 4 (version)
        assertEquals(8, framed.length);

        // Reading should fail because length == 0
        ByteArrayInputStream in = new ByteArrayInputStream(framed);
        assertThrows(IOException.class, () -> MessageCodec.readFramedMessage(in));
    }

    @Test
    public void testUtf8MultibytePayload() throws IOException {
        String original = "你好, VertexCache 🚀";
        byte[] payload = original.getBytes("UTF-8");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(out, payload);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        byte[] result = MessageCodec.readFramedMessage(in);

        assertNotNull(result);
        assertEquals(original, new String(result, "UTF-8"));
    }

    @Test
    public void testHexDumpForInterSdkComparison() throws IOException {
        byte[] payload = "ping".getBytes(); // Keep simple
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(out, payload);

        byte[] framed = out.toByteArray();
        System.out.println("Framed hex: " + bytesToHex(framed));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
