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


use PHPUnit\Framework\TestCase;
use VertexCache\Comm\GcmCryptoHelper;

final class GcmCryptoHelperTest extends TestCase
{
    private string $key;
    private string $message;

    protected function setUp(): void
    {
        $this->key = str_repeat("\0", 32); // 256-bit key
        $this->message = "VertexCache secure payload";
    }

    public function testEncryptDecryptRoundTrip(): void
    {
        $encrypted = GcmCryptoHelper::encrypt($this->message, $this->key);
        $decrypted = GcmCryptoHelper::decrypt($encrypted, $this->key);
        $this->assertEquals($this->message, $decrypted);
    }

    public function testDecryptFailsOnTamperedCiphertext(): void
    {
        $encrypted = GcmCryptoHelper::encrypt($this->message, $this->key);
        $tampered = substr($encrypted, 0, -1) . chr(ord($encrypted[-1]) ^ 0x01);

        $this->expectException(RuntimeException::class);
        GcmCryptoHelper::decrypt($tampered, $this->key);
    }

    public function testDecryptFailsIfTooShort(): void
    {
        $this->expectException(InvalidArgumentException::class);
        GcmCryptoHelper::decrypt("123", $this->key);
    }

    public function testBase64EncodeDecodeRoundTrip(): void
    {
        $encoded = GcmCryptoHelper::encodeBase64Key($this->key);
        $decoded = GcmCryptoHelper::decodeBase64Key($encoded);
        $this->assertEquals($this->key, $decoded);
    }

    public function testGenerateBase64Key(): void
    {
        $b64 = GcmCryptoHelper::generateBase64Key();
        $decoded = GcmCryptoHelper::decodeBase64Key($b64);
        $this->assertEquals(32, strlen($decoded));
    }

    public function testReconciliationWithFixedIv(): void
    {
        $key = str_repeat("\0", 16);
        $iv = str_repeat("\0", 12);
        $message = "VertexCacheGCMTest";

        $tag = '';
        $ciphertext = openssl_encrypt(
            $message,
            'aes-128-gcm',
            $key,
            OPENSSL_RAW_DATA,
            $iv,
            $tag,
            '',
            16
        );

        $combined = $iv . $ciphertext . $tag;

        $decrypted = openssl_decrypt(
            $ciphertext,
            'aes-128-gcm',
            $key,
            OPENSSL_RAW_DATA,
            $iv,
            $tag
        );

        $this->assertEquals($message, $decrypted);

        echo "[RECON] Plaintext: $message\n";
        echo "[RECON] Key (hex): " . bin2hex($key) . "\n";
        echo "[RECON] IV (hex): " . bin2hex($iv) . "\n";
        echo "[RECON] Encrypted (hex): " . bin2hex($combined) . "\n";
    }
}
