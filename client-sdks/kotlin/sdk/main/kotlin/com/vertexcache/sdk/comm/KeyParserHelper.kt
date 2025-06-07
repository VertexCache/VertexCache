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

import vertexcache.sdk.model.VertexCacheSdkException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object KeyParserHelper {

    @Throws(VertexCacheSdkException::class)
    fun configPublicKeyIfEnabled(pem: String): PublicKey {
        return try {
            val cleaned = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\\s".toRegex(), "")
            val decoded = Base64.getDecoder().decode(cleaned)
            val keySpec = X509EncodedKeySpec(decoded)
            KeyFactory.getInstance("RSA").generatePublic(keySpec)
        } catch (e: Exception) {
            throw VertexCacheSdkException("Invalid public key")
        }
    }

    @Throws(VertexCacheSdkException::class)
    fun configSharedKeyIfEnabled(base64: String): ByteArray {
        return try {
            Base64.getDecoder().decode(base64)
        } catch (e: Exception) {
            throw VertexCacheSdkException("Invalid shared key")
        }
    }
}
