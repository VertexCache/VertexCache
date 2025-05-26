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
package com.vertexcache.domain.security;

import com.vertexcache.common.security.KeyPairHelper;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;


public class KeyPairTest {

    @Test
    public void testGenerate() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            assertNotNull(keyPair);
            assertNotNull(keyPair.getPublic());
            assertNotNull(keyPair.getPrivate());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEncodeAndDecodePublicKey() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyBase64 = KeyPairHelper.encodeKey(publicKey);
            assertNotNull(publicKeyBase64);

            PublicKey decodedPublicKey = KeyPairHelper.decodePublicKey(publicKeyBase64);
            assertNotNull(decodedPublicKey);
            assertEquals(publicKey, decodedPublicKey);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEncodeAndDecodePrivateKey() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PrivateKey privateKey = keyPair.getPrivate();
            String privateKeyBase64 = KeyPairHelper.encodeKey(privateKey);
            assertNotNull(privateKeyBase64);

            PrivateKey decodedPrivateKey = KeyPairHelper.decodePrivateKey(privateKeyBase64);
            assertNotNull(decodedPrivateKey);
            assertEquals(privateKey, decodedPrivateKey);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testPublicKeyToString() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = KeyPairHelper.publicKeyToString(publicKey);
            assertNotNull(publicKeyString);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
