<?php
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
namespace VertexCache\Comm;

class GcmCryptoHelper
{
    private const IV_LENGTH = 12;  // bytes
    private const TAG_LENGTH = 16; // bytes
    private const CIPHER = 'aes-256-gcm';

    public static function encrypt(string $plaintext, string $key): string
    {
        $iv = random_bytes(self::IV_LENGTH);
        $tag = '';

        $ciphertext = openssl_encrypt(
            $plaintext,
            self::CIPHER,
            $key,
            OPENSSL_RAW_DATA,
            $iv,
            $tag,
            '',
            self::TAG_LENGTH
        );

        return $iv . $ciphertext . $tag;
    }

    public static function decrypt(string $encrypted, string $key): string
    {
        $len = strlen($encrypted);
        if ($len < self::IV_LENGTH + self::TAG_LENGTH) {
            throw new \InvalidArgumentException("Encrypted input too short");
        }

        $iv = substr($encrypted, 0, self::IV_LENGTH);
        $tag = substr($encrypted, -self::TAG_LENGTH);
        $ciphertext = substr($encrypted, self::IV_LENGTH, $len - self::IV_LENGTH - self::TAG_LENGTH);

        $plaintext = openssl_decrypt(
            $ciphertext,
            self::CIPHER,
            $key,
            OPENSSL_RAW_DATA,
            $iv,
            $tag
        );

        if ($plaintext === false) {
            throw new \RuntimeException("Decryption failed or tag mismatch");
        }

        return $plaintext;
    }

    public static function encodeBase64Key(string $key): string
    {
        return base64_encode($key);
    }

    public static function decodeBase64Key(string $encoded): string
    {
        return base64_decode(trim($encoded), true);
    }

    public static function generateBase64Key(): string
    {
        return base64_encode(random_bytes(32)); // AES-256
    }
}
