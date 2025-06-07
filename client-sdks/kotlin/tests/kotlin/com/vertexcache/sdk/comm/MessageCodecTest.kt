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

package tests.comm

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import sdk.comm.MessageCodec

class MessageCodecTest {

    @Test
    fun testWriteThenReadFramedMessage() {
        val original = "Hello VertexCache"
        val payload = original.toByteArray()

        val out = ByteArrayOutputStream()
        MessageCodec.writeFramedMessage(out, payload)

        val input = ByteArrayInputStream(out.toByteArray())
        val result = MessageCodec.readFramedMessage(input)

        assertNotNull(result)
        assertEquals(String(payload), String(result))
    }

    @Test
    fun testInvalidVersionByte() {
        val badFrame = ByteBuffer.allocate(8)
            .putInt(3)
            .put(0x02)
            .put("abc".toByteArray())
            .array()

        val input = ByteArrayInputStream(badFrame)
        assertThrows<IOException> {
            MessageCodec.readFramedMessage(input)
        }
    }

    @Test
    fun testTooShortHeaderReturnsNull() {
        val short = byteArrayOf(0x01, 0x02)
        val input = ByteArrayInputStream(short)
        val result = MessageCodec.readFramedMessage(input)
        assertNull(result)
    }

    @Test
    fun testTooLargePayloadRejected() {
        val big = ByteArray(MessageCodec.MAX_MESSAGE_SIZE + 1)
        val out = ByteArrayOutputStream()

        assertThrows<IOException> {
            MessageCodec.writeFramedMessage(out, big)
        }
    }

    @Test
    fun testWriteEmptyPayloadThenReadShouldFail() {
        val out = ByteArrayOutputStream()
        MessageCodec.writeFramedMessage(out, ByteArray(0))
        val input = ByteArrayInputStream(out.toByteArray())

        assertThrows<IOException> {
            MessageCodec.readFramedMessage(input)
        }
    }

    @Test
    fun testUtf8MultibytePayload() {
        val original = "你好, VertexCache 🚀"
        val payload = original.toByteArray(Charsets.UTF_8)

        val out = ByteArrayOutputStream()
        MessageCodec.writeFramedMessage(out, payload)

        val input = ByteArrayInputStream(out.toByteArray())
        val result = MessageCodec.readFramedMessage(input)

        assertNotNull(result)
        assertEquals(original, String(result, Charsets.UTF_8))
    }

    @Test
    fun testHexDumpForInterSdkComparison() {
        val out = ByteArrayOutputStream()
        MessageCodec.writeFramedMessage(out, "ping".toByteArray())
        val hex = out.toByteArray().joinToString("") { "%02X".format(it) }
        println("Framed hex: $hex")
    }
}
