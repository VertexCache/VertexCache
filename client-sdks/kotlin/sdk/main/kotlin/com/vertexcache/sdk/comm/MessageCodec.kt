// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

package com.vertexcache.sdk.comm

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * MessageCodec handles framing and deframing of messages transmitted over TCP.
 *
 * Framing format:
 * - 4-byte big-endian payload length
 * - 4-byte big-endian protocol version
 * - N-byte payload
 */
object MessageCodec {
    const val MAX_MESSAGE_SIZE = 10 * 1024 * 1024
    const val PROTOCOL_VERSION = 0x00000101

    /**
     * Writes a framed message to the output stream.
     *
     * @param out The OutputStream to write to.
     * @param data The message payload.
     * @throws IOException If the message is too large or a write fails.
     */
    @Throws(IOException::class)
    fun writeFramedMessage(out: OutputStream, data: ByteArray) {
        if (data.size > MAX_MESSAGE_SIZE) {
            throw IOException("Message too large: ${data.size}")
        }

        val buffer = ByteBuffer.allocate(4 + 4 + data.size)
        buffer.putInt(data.size)
        buffer.putInt(PROTOCOL_VERSION)
        buffer.put(data)
        out.write(buffer.array())
    }

    /**
     * Reads a framed message from the input stream.
     *
     * @param input The InputStream to read from.
     * @return The payload byte array, or null if the stream is too short.
     * @throws IOException If the version is invalid, the length is out of bounds, or reading fails.
     */
    @Throws(IOException::class)
    fun readFramedMessage(input: InputStream): ByteArray? {
        val header = input.readNBytes(8)
        if (header.size < 8) return null

        val buffer = ByteBuffer.wrap(header)
        val length = buffer.int
        val version = buffer.int

        if (version != PROTOCOL_VERSION) {
            throw IOException("Unsupported protocol version: 0x${version.toUInt().toString(16)}")
        }

        if (length <= 0 || length > MAX_MESSAGE_SIZE) {
            throw IOException("Invalid message length: $length")
        }

        return input.readNBytes(length)
    }
}
