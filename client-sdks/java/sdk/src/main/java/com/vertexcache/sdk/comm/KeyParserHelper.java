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

import com.vertexcache.sdk.model.VertexCacheSdkException;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * KeyParserHelper provides utility methods for parsing cryptographic keys used by the VertexCache SDK.
 *
 * This class supports two main operations:
 *
 * - configPublicKeyIfEnabled(String): Parses an RSA public key from a PEM-formatted string by removing
 *   headers and whitespace, then decoding it into a PublicKey instance. Used when asymmetric encryption is enabled.
 *
 * - configSharedKeyIfEnabled(String): Decodes a Base64-encoded symmetric key string into raw bytes.
 *   Used when symmetric encryption is enabled.
 *
 * Both methods throw VertexCacheSdkException if the input is malformed or the key parsing fails.
 *
 * These helpers are used during SDK initialization to configure secure transport options.
 */

public class KeyParserHelper {

    /**
     * Loads and parses an RSA public key from the provided PEM string if asymmetric encryption is enabled.
     * The PEM headers and any whitespace are stripped before base64 decoding and key generation.
     *
     * Only executed when the encryption mode is ASYMMETRIC; otherwise, the publicKey remains unset.
     *
     * @throws VertexCacheSdkException if the PEM string is invalid or key construction fails
     */
    public static PublicKey configPublicKeyIfEnabled(String publicKeyPem) throws VertexCacheSdkException {
        try {
            String cleaned = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(cleaned);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new VertexCacheSdkException("Invalid public key");
        }
    }

    /**
     * Loads and decodes the symmetric encryption key from a Base64 string into raw bytes.
     * This is only performed if the encryption mode is SYMMETRIC; otherwise, the key is set to null.
     *
     * @throws VertexCacheSdkException if the Base64 decoding fails or the key is malformed
     */
    public static byte[] configSharedKeyIfEnabled(String sharedEncryptionKey) throws VertexCacheSdkException {
        try {
            return Base64.getDecoder().decode(sharedEncryptionKey);
        } catch (Exception ex) {
            throw new VertexCacheSdkException("Invalid shared key");
        }
    }
}
