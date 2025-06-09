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

use VertexCache\Model\VertexCacheSdkException;

/**
 * SSLHelper provides SSL/TLS stream context creation utilities for VertexCache.
 */
class SSLHelper
{
    /**
     * Creates a stream context that validates the server certificate against a trusted PEM string.
     *
     * @param string $pemCert
     * @return resource
     * @throws VertexCacheSdkException
     */
    /**
     * Creates a PHP stream context that enforces TLS server certificate validation
     * against a specific PEM-encoded certificate.
     *
     * @param string $pemCert PEM-encoded X.509 certificate content
     * @return resource Stream context for use with stream_socket_client
     * @throws VertexCacheSdkException
     */
    public static function createVerifiedSocketContext(string $pemCert)
    {
        // Save to a temporary cert file
        $tempCertFile = tempnam(sys_get_temp_dir(), 'vc_cert_');
        if ($tempCertFile === false || @file_put_contents($tempCertFile, $pemCert) === false) {
            throw new VertexCacheSdkException("Failed to write PEM certificate to temp file");
        }

        if (openssl_x509_read($pemCert) === false) {
            throw new VertexCacheSdkException("Invalid PEM certificate");
        }

        $contextOptions = [
            'ssl' => [
                'verify_peer'      => true,
                'verify_peer_name' => true,
                'allow_self_signed' => false,
                'cafile'           => $tempCertFile,
            ],
        ];

        $context = stream_context_create($contextOptions);
        if (!$context) {
            throw new VertexCacheSdkException("Failed to create secure stream context");
        }

        return $context;
    }

    /**
     * Creates a PHP stream context that disables all TLS verification.
     * This is insecure and should only be used in development or test environments.
     *
     * @return resource Stream context for use with stream_socket_client
     * @throws VertexCacheSdkException
     */
    public static function createInsecureSocketContext()
    {
        $contextOptions = [
            'ssl' => [
                'verify_peer'       => false,
                'verify_peer_name'  => false,
                'allow_self_signed' => true,
            ],
        ];

        $context = stream_context_create($contextOptions);
        if (!$context) {
            throw new VertexCacheSdkException("Failed to create non secure stream context");
        }

        return $context;
    }
}
