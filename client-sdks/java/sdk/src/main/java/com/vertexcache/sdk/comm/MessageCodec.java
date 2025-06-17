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

    public static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024; // 10MB

    public static final int PROTOCOL_VERSION_RSA_PKCS1     = 0x00000101;
    public static final int PROTOCOL_VERSION_AES_GCM            = 0x00000181;

    private static int protocolVersion = PROTOCOL_VERSION_RSA_PKCS1;

    public static void switchToSymmetric() {
        protocolVersion = PROTOCOL_VERSION_AES_GCM;
    }

    public static void switchToAsymmetric() {
        protocolVersion = PROTOCOL_VERSION_RSA_PKCS1;
    }

    public static byte[] readFramedMessage(InputStream in) throws IOException {
        byte[] header = in.readNBytes(8); // 4 bytes length + 4 bytes version
        if (header.length < 8) return null;

        int length = ByteBuffer.wrap(header, 0, 4).getInt();
        protocolVersion = ByteBuffer.wrap(header, 4, 4).getInt(); // 4-byte version field

        if (length <= 0 || length > MAX_MESSAGE_SIZE) {
            throw new IOException("Invalid message length: " + length);
        }

        return in.readNBytes(length);
    }

    public static void writeFramedMessage(OutputStream out, byte[] data) throws IOException {
        if (data.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Message too large: " + data.length);
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + data.length); // 4 bytes for length + 4 bytes for version
        buffer.putInt(data.length);
        buffer.putInt(protocolVersion); // 4-byte version
        buffer.put(data);
        out.write(buffer.array());
    }

}
