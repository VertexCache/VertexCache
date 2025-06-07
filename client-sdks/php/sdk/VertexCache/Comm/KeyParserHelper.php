<?php
// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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
namespace VertexCache\Comm;

use VertexCache\Model\VertexCacheSdkException;

class KeyParserHelper
{
    /**
     * Parses a PEM-formatted RSA public key string into a usable key resource.
     *
     * @param string $pemString
     * @return string
     * @throws VertexCacheSdkException
     */
    public static function configPublicKeyIfEnabled(string $pemString): string
    {
        try {
            $cleaned = str_replace(
                ["-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----", "\n", "\r", " "],
                "",
                $pemString
            );
            $decoded = base64_decode($cleaned, true);

            if ($decoded === false) {
                throw new VertexCacheSdkException("Invalid public key");
            }

            $key = openssl_pkey_get_public("-----BEGIN PUBLIC KEY-----\n" . chunk_split(base64_encode($decoded), 64, "\n") . "-----END PUBLIC KEY-----");

            if ($key === false) {
                throw new VertexCacheSdkException("Invalid public key");
            }

            return $decoded;
        } catch (\Throwable $e) {
            throw new VertexCacheSdkException("Invalid public key");
        }
    }

    /**
     * Decodes a Base64-encoded symmetric key string into raw bytes.
     *
     * @param string $base64
     * @return string
     * @throws VertexCacheSdkException
     */
    public static function configSharedKeyIfEnabled(string $base64): string
    {
        $decoded = base64_decode($base64, true);
        if ($decoded === false || base64_encode($decoded) !== preg_replace('/\s+/', '', $base64)) {
            throw new VertexCacheSdkException("Invalid shared key");
        }
        return $decoded;
    }
}
