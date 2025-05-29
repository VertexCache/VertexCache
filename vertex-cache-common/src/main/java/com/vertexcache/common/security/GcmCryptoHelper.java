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
package com.vertexcache.common.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for performing AES-GCM encryption and decryption using a shared key.
 *
 * This class provides static helper methods:
 * - `encrypt(byte[] data, byte[] key)`: Encrypts the given plaintext using AES-GCM with a 12-byte IV.
 * - `decrypt(byte[] encryptedData, byte[] key)`: Decrypts AES-GCM-encrypted data back to plaintext.
 *
 * AES-GCM ensures both confidentiality and integrity of the data.
 * The encrypted payload format includes the IV and authentication tag.
 *
 * Note: The caller is responsible for supplying a valid 128/192/256-bit AES key.
 */
public class GcmCryptoHelper {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits (recommended)
    private static final int GCM_TAG_LENGTH = 128; // bits

    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] encrypt(byte[] plaintext, byte[] keyBytes) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        SecretKeySpec key = new SecretKeySpec(keyBytes, AES);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ciphertext = cipher.doFinal(plaintext);

        // Concatenate IV + ciphertext
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return combined;
    }

    public static byte[] decrypt(byte[] encrypted, byte[] keyBytes) throws Exception {
        if (encrypted.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data: too short");
        }

        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] ciphertext = new byte[encrypted.length - GCM_IV_LENGTH];

        System.arraycopy(encrypted, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(encrypted, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        SecretKeySpec key = new SecretKeySpec(keyBytes, AES);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] decodeBase64Key(String base64) {
        return Base64.getDecoder().decode(base64.trim());
    }

    public static String encodeBase64Key(byte[] raw) {
        return Base64.getEncoder().encodeToString(raw);
    }

    public static String generateBase64Key() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(256); // AES-256
        SecretKey key = keyGen.generateKey();
        return encodeBase64Key(key.getEncoded());
    }
}
