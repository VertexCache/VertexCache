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
package com.vertexcache.domain.security;

import com.vertexcache.common.security.MessageCodec;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MessageCodecTest {

    @Test
    public void testWriteAndReadSymmetry() throws IOException {
        byte[] message = "Hello, VertexCache!".getBytes();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(output, message);

        byte[] framed = output.toByteArray();
        ByteArrayInputStream input = new ByteArrayInputStream(framed);

        byte[] result = MessageCodec.readFramedMessage(input);
        assertNotNull(result);
        assertArrayEquals(message, result);
    }

    @Test
    public void testShortHeaderShouldReturnNull() throws IOException {
        byte[] partialHeader = new byte[] {0, 0, 0, 5}; // 4-byte length only, missing version
        ByteArrayInputStream input = new ByteArrayInputStream(partialHeader);
        byte[] result = MessageCodec.readFramedMessage(input);
        assertNull(result);
    }

    @Test
    public void testInvalidLengthShouldThrow() {
        int invalidLength = MessageCodec.MAX_MESSAGE_SIZE + 1;
        byte[] header = new byte[8];
        header[0] = (byte) ((invalidLength >> 24) & 0xFF);
        header[1] = (byte) ((invalidLength >> 16) & 0xFF);
        header[2] = (byte) ((invalidLength >> 8) & 0xFF);
        header[3] = (byte) (invalidLength & 0xFF);

        // version is fine
        header[4] = 0;
        header[5] = 0;
        header[6] = 1;
        header[7] = 1;

        ByteArrayInputStream input = new ByteArrayInputStream(header);
        assertThrows(IOException.class, () -> MessageCodec.readFramedMessage(input));
    }

    @Test
    public void testTruncatedPayloadShouldReturnShort() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] actualPayload = "Short".getBytes();
        ByteArrayOutputStream frame = new ByteArrayOutputStream();
        MessageCodec.writeFramedMessage(frame, actualPayload);
        byte[] full = frame.toByteArray();

        // Truncate to simulate broken stream
        byte[] corrupted = new byte[full.length - 2];
        System.arraycopy(full, 0, corrupted, 0, corrupted.length);

        ByteArrayInputStream input = new ByteArrayInputStream(corrupted);
        byte[] result = MessageCodec.readFramedMessage(input);
        assertNotNull(result);
        assertTrue(result.length < actualPayload.length); // due to truncation
    }

    @Test
    public void testZeroLengthPayloadShouldThrow() {
        byte[] message = new byte[0];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThrows(IOException.class, () -> {
            MessageCodec.writeFramedMessage(output, message);

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            MessageCodec.readFramedMessage(input);
        });
    }

    @Test
    public void testProtocolFieldExtraction() {
        int flags = 0x11011278;

        MessageCodecTestHelper.setProtocolVersionForTest(flags);

        assertTrue(MessageCodec.isCompressed());
        assertTrue(MessageCodec.isMultipartMessage());
        assertFalse(MessageCodec.isSignedMessage());
        assertTrue(MessageCodec.isAckRequested());
        assertTrue(MessageCodec.isTracingEnabled());
        assertEquals(2, MessageCodec.extractProtocolFormat());
        assertEquals(7, MessageCodec.extractEncryptionHint());
        assertEquals(8, MessageCodec.extractProtocolVersion());
    }
}
