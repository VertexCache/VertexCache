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
use VertexCache\Model\VertexCacheSdkException;

class SocketHelper
{
    /**
     * Creates a TLS-secured stream socket connection using the given client options.
     *
     * Depending on the verifyCertificate flag, a secure (verified) or insecure TLS context is created.
     *
     * @param ClientOption $options
     * @return resource Connected stream socket
     * @throws VertexCacheSdkException
     */
    public static function createSecureSocket(ClientOption $options)
    {
        try {
            $host = $options->getServerHost();
            $port = $options->getServerPort();
            $timeout = $options->getConnectTimeout();

            $context = $options->isVerifyCertificate()
                ? SSLHelper::createVerifiedSocketContext($options->getTlsCertificate())
                : SSLHelper::createInsecureSocketContext();

            $socket = @stream_socket_client(
                "tls://{$host}:{$port}",
                $errno,
                $errstr,
                $timeout,
                STREAM_CLIENT_CONNECT,
                $context
            );

            if ($socket === false) {
                throw new VertexCacheSdkException("Failed to create Secure Socket: $errstr ($errno)");
            }

            stream_set_timeout($socket, $options->getReadTimeout());
            return $socket;
        } catch (\Throwable $e) {
            throw new VertexCacheSdkException("Failed to create Secure Socket");
        }
    }

    /**
     * Creates a plain TCP socket connection using the given client options.
     *
     * No encryption or TLS handshake is performed.
     *
     * @param ClientOption $options
     * @return resource Connected stream socket
     * @throws VertexCacheSdkException
     */
    public static function createSocketNonTLS(ClientOption $options)
    {
        $host = $options->getServerHost();
        $port = $options->getServerPort();
        $timeout = $options->getConnectTimeout();

        $errstr = '';
        $errno = 0;

        $socket = @stream_socket_client(
            "tcp://{$host}:{$port}",
            $errno,
            $errstr,
            $timeout,
            STREAM_CLIENT_CONNECT
        );

        if ($socket === false || !is_resource($socket)) {
            throw new VertexCacheSdkException("Failed to create Non Secure Socket: $errstr ($errno)");
        }

        stream_set_timeout($socket, $options->getReadTimeout());
        return $socket;
    }
}
