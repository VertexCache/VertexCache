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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

class GcmCryptoHelperTest {

    private val key = ByteArray(32) // 256-bit zero key
    private val message = "VertexCache secure payload".toByteArray()

    @Test
    fun `encrypt and decrypt round-trip`() {
        val encrypted = GcmCryptoHelper.encrypt(message, key)
        val decrypted = GcmCryptoHelper.decrypt(encrypted, key)
        assertArrayEquals(message, decrypted)
    }

    @Test
    fun `decrypt should fail on tampered ciphertext`() {
        val encrypted = GcmCryptoHelper.encrypt(message, key)
        encrypted[encrypted.lastIndex] = encrypted.last().xor(0x01)

        assertThrows(Exception::class.java) {
            GcmCryptoHelper.decrypt(encrypted, key)
        }
    }

    @Test
    fun `decrypt should fail if too short`() {
        assertThrows(IllegalArgumentException::class.java) {
            GcmCryptoHelper.decrypt(ByteArray(5), key)
        }
    }

    @Test
    fun `base64 key encode and decode round-trip`() {
        val encoded = GcmCryptoHelper.encodeBase64Key(key)
        val decoded = GcmCryptoHelper.decodeBase64Key(encoded)
        assertArrayEquals(key, decoded)
    }

    @Test
    fun `generate base64 key should be 256-bit`() {
        val generated = GcmCryptoHelper.generateBase64Key()
        val decoded = GcmCryptoHelper.decodeBase64Key(generated)
        assertEquals(32, decoded.size)
    }

    @Test
    fun `reconciliation test with fixed iv`() {
        val key = ByteArray(16)
        val iv = ByteArray(12)
        val message = "VertexCacheGCMTest".toByteArray()

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(16 * 8, iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val ciphertext = cipher.doFinal(message)
        val combined = iv + ciphertext

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        val decrypted = cipher.doFinal(ciphertext)
        assertArrayEquals(message, decrypted)

        println("[RECON] Plaintext: ${String(message)}")
        println("[RECON] Key (hex): ${key.joinToString("") { "%02x".format(it) }}")
        println("[RECON] IV (hex): ${iv.joinToString("") { "%02x".format(it) }}")
        println("[RECON] Encrypted (hex): ${combined.joinToString("") { "%02x".format(it) }}")
    }
}
