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

use VertexCache\Model\ClientOption;
use VertexCache\Model\EncryptionMode;
use VertexCache\Model\VertexCacheSdkException;
use VertexCache\Comm\GcmCryptoHelper;
use VertexCache\Comm\SocketHelper;
use VertexCache\Comm\MessageCodec;

class ClientConnector
{
    private $socket;
    private $writer;
    private $reader;
    private $options;
    private $connected = false;

    public function __construct(ClientOption $options)
    {
        $this->options = $options;
    }

    public function connect(): void
    {
        try {
            $this->socket = $this->options->isEnableTlsEncryption()
                ? SocketHelper::createSecureSocket($this->options)
                : SocketHelper::createSocketNonTLS($this->options);

            stream_set_timeout($this->socket, $this->options->getReadTimeout());
            $this->writer = $this->socket;
            $this->reader = $this->socket;

            $identPayload = $this->options->buildIdentCommand();
            $encrypted = $this->encryptIfEnabled($identPayload);
            $framed = MessageCodec::writeFramedMessage($encrypted);
            fwrite($this->writer, $framed);

            $response = MessageCodec::readFramedMessage($this->reader);
            $identStr = $response === null ? '' : trim($response);
            if (strpos($identStr, '+OK') !== 0) {
                throw new VertexCacheSdkException("Authorization failed: " . $identStr);
            }

            $this->connected = true;
        } catch (\Exception $e) {
            throw new VertexCacheSdkException($e->getMessage());
        }
    }

    public function send(string $message): string
    {
        if (!$this->isConnected()) {
            throw new VertexCacheSdkException("Not connected");
        }

        try {
            $payload = $this->encryptIfEnabled($message);
            $framed = MessageCodec::writeFramedMessage($payload);
            fwrite($this->writer, $framed);

            $response = MessageCodec::readFramedMessage($this->reader);
            if ($response === null) {
                throw new VertexCacheSdkException("Connection closed by server");
            }

            return $response;
        } catch (\Exception $ex) {
            throw new VertexCacheSdkException("Unexpected failure during send");
        }
    }

    private function encryptIfEnabled(string $plainText): string
    {
        try {
            switch ($this->options->getEncryptionMode()) {
                case EncryptionMode::ASYMMETRIC:
                    MessageCodec::switchToAsymmetric();
                    $pubKey = $this->options->getOpenSslPublicKey();
                    if (!openssl_public_encrypt($plainText, $encrypted, $pubKey, OPENSSL_PKCS1_PADDING)) {
                        throw new VertexCacheSdkException("RSA encryption failed");
                    }
                    return $encrypted;

                case EncryptionMode::SYMMETRIC:
                    MessageCodec::switchToSymmetric();
                    return GcmCryptoHelper::encrypt($plainText, $this->options->getSharedEncryptionKeyAsBase64());

                case EncryptionMode::NONE:
                default:
                    return $plainText;
            }
        } catch (\Exception $e) {
            throw new VertexCacheSdkException("Encryption failed for, text redacted *****");
        }
    }

    public function isConnected(): bool
    {
        return $this->connected && is_resource($this->socket);
    }

    public function close(): void
    {
        if (is_resource($this->socket)) {
            @fclose($this->socket);
        }
        $this->connected = false;
    }
}
