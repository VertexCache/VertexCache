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

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object GcmCryptoHelper {
    private const val AES = "AES"
    private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16 // bytes

    fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        val keySpec = SecretKeySpec(key, AES)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
        val ciphertext = cipher.doFinal(plaintext)

        return iv + ciphertext
    }

    fun decrypt(encrypted: ByteArray, key: ByteArray): ByteArray {
        if (encrypted.size < GCM_IV_LENGTH + GCM_TAG_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted data: too short")
        }

        val iv = encrypted.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = encrypted.copyOfRange(GCM_IV_LENGTH, encrypted.size)

        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        val keySpec = SecretKeySpec(key, AES)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        return cipher.doFinal(ciphertext)
    }

    fun encodeBase64Key(key: ByteArray): String =
        Base64.getEncoder().encodeToString(key)

    fun decodeBase64Key(encoded: String): ByteArray =
        Base64.getDecoder().decode(encoded.trim())

    fun generateBase64Key(): String {
        val keyGen = KeyGenerator.getInstance(AES)
        keyGen.init(256)
        val key = keyGen.generateKey()
        return encodeBase64Key(key.encoded)
    }
}
