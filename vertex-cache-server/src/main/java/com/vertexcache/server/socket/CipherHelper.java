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
package com.vertexcache.server.socket;

import com.vertexcache.core.cache.exception.VertexCacheException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.PrivateKey;
import java.util.Map;

/**
 * RSA Cipher Mapper Helper
 */
public class CipherHelper {

    private static final Map<Integer, String> ASYMMETRIC_CIPHER_MAP = Map.of(
            1, "RSA/ECB/PKCS1Padding",
            2, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
            3, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"
    );

    // Symmetric Ciphers (AES)
    private static final Map<Integer, String> SYMMETRIC_CIPHER_MAP = Map.of(
            8, "AES/GCM/NoPadding",
            9, "AES/CBC/PKCS5Padding"
    );

    public static Cipher getCipherFromId(int cipherId, PrivateKey privateKey) throws VertexCacheException {
        String transformation = ASYMMETRIC_CIPHER_MAP.get(cipherId);
        if (transformation == null) {
            throw new VertexCacheException("Unsupported RSA cipher ID in Message Codec: " + cipherId);
        }

        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher;
        } catch (Exception e) {
            throw new VertexCacheException("Failed to initialize RSA cipher: " + e.getMessage(), e);
        }
    }

    public static String getSymmetricTransformation(int cipherId) {
        String transformation = SYMMETRIC_CIPHER_MAP.get(cipherId);
        if (transformation == null) {
            throw new IllegalArgumentException("Unsupported AES cipher ID: " + cipherId);
        }
        return transformation;
    }

    /* Future - migrate to this?
    public static Cipher getSymmetricCipherFromId(int id, SecretKey key, byte[] iv, int mode) {
        String transformation = SYMMETRIC_CIPHER_MAP.get(id);
        if (transformation == null)
            throw new IllegalArgumentException("Unsupported AES cipher ID: " + id);

        try {
            Cipher cipher = Cipher.getInstance(transformation);
            if (transformation.contains("GCM")) {
                cipher.init(mode, key, new GCMParameterSpec(128, iv));
            } else {
                cipher.init(mode, key, new IvParameterSpec(iv));
            }
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("Failed to init AES cipher: " + e.getMessage(), e);
        }
    }
     */

}
