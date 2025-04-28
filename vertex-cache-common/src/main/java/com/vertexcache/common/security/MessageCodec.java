package com.vertexcache.common.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MessageCodec {

    // 10 MB is probably enough, unless we really want to support images and videos...etc
    public static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024; // 10MB

    // Future Proof, for breaking versions
    public static final byte PROTOCOL_VERSION = 0x01;

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
