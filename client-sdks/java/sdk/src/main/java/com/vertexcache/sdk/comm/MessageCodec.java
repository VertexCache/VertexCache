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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * MessageCodec handles framing and deframing of messages transmitted over TCP.
 *
 * This utility class provides methods to:
 * - `writeFramedMessage(OutputStream out, byte[] message)`: Prefixes the message with its 4-byte length and writes it to the stream.
 * - `readFramedMessage(InputStream in)`: Reads the 4-byte length header, then reads the full message of that length from the stream.
 *
 * This framing protocol ensures message boundaries are preserved across TCP transmissions,
 * which is essential since TCP is a stream-oriented protocol with no built-in message demarcation.
 */
public class MessageCodec {

    // 10 MB is probably enough, unless we really want to support images and videos...etc
    public static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024; // 10MB

    // Future Proof, for breaking versions
    public static final byte PROTOCOL_VERSION = 0x01;

    /**
     * Reads a framed message from the given input stream according to the VertexCache protocol.
     *
     * The framing format consists of a 4-byte integer indicating the message length,
     * followed by a 1-byte protocol version, and then the message payload.
     * This method validates the protocol version and ensures the message length
     * is within acceptable bounds before reading the payload.
     *
     * @param in the {@link InputStream} to read the message from
     * @return the message payload as a byte array, or {@code null} if the stream ends prematurely
     * @throws IOException if the version is unsupported, the length is invalid, or an I/O error occurs
     */
    public static byte[] readFramedMessage(InputStream in) throws IOException {
        byte[] header = in.readNBytes(5);
        if (header.length < 5) return null;

        int length = ByteBuffer.wrap(header, 0, 4).getInt();
        byte version = header[4];

        if (version != PROTOCOL_VERSION) {
            throw new IOException("Unsupported protocol version: " + version);
        }

        if (length <= 0 || length > MAX_MESSAGE_SIZE) {
            throw new IOException("Invalid message length: " + length);
        }

        return in.readNBytes(length);
    }

    /**
     * Writes a framed message to the given output stream using the VertexCache protocol.
     *
     * The message is framed as follows:
     * - A 4-byte integer representing the length of the payload
     * - A 1-byte protocol version
     * - The message payload itself
     *
     * This method validates that the message size does not exceed the allowed maximum
     * before writing the data to the stream in a single buffer.
     *
     * @param out the {@link OutputStream} to write the framed message to
     * @param data the message payload to send
     * @throws IOException if the message size exceeds the maximum or a write error occurs
     */
    public static void writeFramedMessage(OutputStream out, byte[] data) throws IOException {
        if (data.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Message too large: " + data.length);
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + data.length);
        buffer.putInt(data.length);
        buffer.put(PROTOCOL_VERSION);
        buffer.put(data);
        out.write(buffer.array());
    }
}
