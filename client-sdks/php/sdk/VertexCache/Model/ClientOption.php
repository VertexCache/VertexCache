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

namespace VertexCache\Model;

use VertexCache\Model\EncryptionMode;

/**
 * Configuration container for initializing the VertexCache SDK client.
 *
 * This class holds all user-specified options required to establish a connection
 * to a VertexCache server, including host, port, TLS settings, authentication tokens,
 * encryption modes (asymmetric or symmetric), and related keys or certificates.
 *
 * It provides a flexible way to customize client behavior, including security preferences.
 */
class ClientOption
{
    public const DEFAULT_CLIENT_ID = 'sdk-client';
    public const DEFAULT_HOST = '127.0.0.1';
    public const DEFAULT_PORT = 50505;
    public const DEFAULT_READ_TIMEOUT = 3000;
    public const DEFAULT_CONNECT_TIMEOUT = 3000;

    private ?string $clientId = self::DEFAULT_CLIENT_ID;
    private ?string $clientToken = null;

    private string $serverHost = self::DEFAULT_HOST;
    private int $serverPort = self::DEFAULT_PORT;

    private bool $enableTlsEncryption = false;
    private ?string $tlsCertificate = null;
    private bool $verifyCertificate = false;

    private string $encryptionMode = EncryptionMode::NONE;
    private bool $encryptWithPublicKey = false;
    private bool $encryptWithSharedKey = false;

    private ?string $publicKey = null;
    private ?string $sharedEncryptionKey = null;

    private int $readTimeout = self::DEFAULT_READ_TIMEOUT;
    private int $connectTimeout = self::DEFAULT_CONNECT_TIMEOUT;

    public function getClientId(): string
    {
        return $this->clientId ?? '';
    }

    public function getClientToken(): string
    {
        return $this->clientToken ?? '';
    }

    public function buildIdentCommand(): string
    {
        return sprintf(
            'IDENT {"client_id":"%s", "token":"%s"}',
            $this->getClientId(),
            $this->getClientToken()
        );
    }

    public function getOpenSslPublicKey()
    {
        $key = $this->getPublicKey();
        $res = openssl_pkey_get_public($key);
        if ($res === false) {
            throw new VertexCacheSdkException("Invalid public key format");
        }
        return $res;
    }

    public function getSharedEncryptionKeyAsBase64(): string
    {
        if (empty($this->sharedEncryptionKey)) {
            throw new VertexCacheSdkException("Shared encryption key is not set");
        }

        $decoded = base64_decode($this->sharedEncryptionKey, true);
        if ($decoded === false) {
            throw new VertexCacheSdkException("Failed to decode shared encryption key from Base64");
        }

        return $decoded;
    }

    // Getters/setters follow the same order as fields

    public function setClientId(?string $clientId): void { $this->clientId = $clientId; }
    public function setClientToken(?string $clientToken): void { $this->clientToken = $clientToken; }

    public function getServerHost(): string { return $this->serverHost; }
    public function setServerHost(string $host): void { $this->serverHost = $host; }

    public function getServerPort(): int { return $this->serverPort; }
    public function setServerPort(int $port): void { $this->serverPort = $port; }

    public function isEnableTlsEncryption(): bool { return $this->enableTlsEncryption; }
    public function setEnableTlsEncryption(bool $enabled): void { $this->enableTlsEncryption = $enabled; }

    public function getTlsCertificate(): ?string { return $this->tlsCertificate; }
    public function setTlsCertificate(?string $cert): void { $this->tlsCertificate = $cert; }

    public function isVerifyCertificate(): bool { return $this->verifyCertificate; }
    public function setVerifyCertificate(bool $verify): void { $this->verifyCertificate = $verify; }

    public function getEncryptionMode(): string { return $this->encryptionMode; }
    public function setEncryptionMode(string $mode): void { $this->encryptionMode = $mode; }

    public function isEncryptWithPublicKey(): bool { return $this->encryptWithPublicKey; }
    public function setEncryptWithPublicKey(bool $value): void { $this->encryptWithPublicKey = $value; }

    public function isEncryptWithSharedKey(): bool { return $this->encryptWithSharedKey; }
    public function setEncryptWithSharedKey(bool $value): void { $this->encryptWithSharedKey = $value; }

    public function getPublicKey(): ?string { return $this->publicKey; }
    public function setPublicKey(?string $key): void { $this->publicKey = $key; }

    public function getSharedEncryptionKey(): ?string { return $this->sharedEncryptionKey; }
    public function setSharedEncryptionKey(?string $key): void { $this->sharedEncryptionKey = $key; }

    public function getReadTimeout(): int { return $this->readTimeout; }
    public function setReadTimeout(int $timeout): void { $this->readTimeout = $timeout; }

    public function getConnectTimeout(): int { return $this->connectTimeout; }
    public function setConnectTimeout(int $timeout): void { $this->connectTimeout = $timeout; }
}
