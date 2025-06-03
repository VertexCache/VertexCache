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

import com.vertexcache.sdk.comm.GcmCryptoHelper;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class GcmCryptoHelperTest {

    private final byte[] key = new byte[32]; // 256-bit AES key (zeroed for test)
    private final byte[] message = "VertexCache secure payload".getBytes();

    @Test
    public void testEncryptDecryptRoundTrip() throws Exception {
        byte[] encrypted = GcmCryptoHelper.encrypt(message, key);
        assertNotNull(encrypted);
        assertTrue(encrypted.length > message.length);

        byte[] decrypted = GcmCryptoHelper.decrypt(encrypted, key);
        assertArrayEquals(message, decrypted);
    }

    @Test
    public void testDecryptFailsOnModifiedCiphertext() throws Exception {
        byte[] encrypted = GcmCryptoHelper.encrypt(message, key);
        encrypted[encrypted.length - 1] ^= 0x01; // flip last byte

        assertThrows(Exception.class, () -> GcmCryptoHelper.decrypt(encrypted, key));
    }

    @Test
    public void testDecryptFailsIfTooShort() {
        byte[] tooShort = new byte[5];
        assertThrows(IllegalArgumentException.class, () -> GcmCryptoHelper.decrypt(tooShort, key));
    }

    @Test
    public void testBase64KeyEncodeDecodeRoundTrip() {
        String base64 = GcmCryptoHelper.encodeBase64Key(key);
        byte[] decoded = GcmCryptoHelper.decodeBase64Key(base64);

        assertArrayEquals(key, decoded);
    }

    @Test
    public void testGenerateBase64Key() throws Exception {
        String base64Key = GcmCryptoHelper.generateBase64Key();
        assertNotNull(base64Key);

        byte[] decoded = GcmCryptoHelper.decodeBase64Key(base64Key);
        assertEquals(32, decoded.length); // 256-bit
    }

    @Test
    public void testEncryptGeneratesUniqueIVs() throws Exception {
        byte[] key = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);

        byte[] c1 = GcmCryptoHelper.encrypt(message, key);
        byte[] c2 = GcmCryptoHelper.encrypt(message, key);

        assertNotEquals(Base64.getEncoder().encodeToString(c1), Base64.getEncoder().encodeToString(c2));
    }
}
