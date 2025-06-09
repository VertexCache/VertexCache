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

import com.vertexcache.sdk.model.VertexCacheSdkException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class KeyParserHelperTest {

    private val validPEM = """
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
        bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
        UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
        GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
        NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
        6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
        EwIDAQAB
        -----END PUBLIC KEY-----
    """.trimIndent()

    private val invalidPEM = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----"
    private val validSharedKey = "YWJjZGVmZ2hpamtsbW5vcA=="
    private val invalidSharedKey = "%%%INVALID%%%"

    @Test
    fun `configPublicKeyIfEnabled should succeed with valid PEM`() {
        val key = KeyParserHelper.configPublicKeyIfEnabled(validPEM)
        assertNotNull(key)
        assertEquals("RSA", key.algorithm)
    }

    @Test
    fun `configPublicKeyIfEnabled should fail with invalid PEM`() {
        val exception = assertThrows(VertexCacheSdkException::class.java) {
            KeyParserHelper.configPublicKeyIfEnabled(invalidPEM)
        }
        assertEquals("Invalid public key", exception.message)
    }

    @Test
    fun `configSharedKeyIfEnabled should succeed with valid base64`() {
        val result = KeyParserHelper.configSharedKeyIfEnabled(validSharedKey)
        assertNotNull(result)
        assertEquals(16, result.size)
    }

    @Test
    fun `configSharedKeyIfEnabled should return correct byte content`() {
        val expected = "abcdefghijklmnop".toByteArray(StandardCharsets.UTF_8)
        val result = KeyParserHelper.configSharedKeyIfEnabled(validSharedKey)
        assertArrayEquals(expected, result)
    }

    @Test
    fun `configSharedKeyIfEnabled should fail with invalid base64`() {
        val exception = assertThrows(VertexCacheSdkException::class.java) {
            KeyParserHelper.configSharedKeyIfEnabled(invalidSharedKey)
        }
        assertEquals("Invalid shared key", exception.message)
    }
}
