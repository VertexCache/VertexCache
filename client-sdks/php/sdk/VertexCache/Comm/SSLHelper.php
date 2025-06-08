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
    public static function createVerifiedSocketContext(string $pemCert)
    {
        try {
            if (empty($pemCert) || strpos($pemCert, 'BEGIN CERTIFICATE') === false) {
                throw new \InvalidArgumentException("Invalid certificate format");
            }

            $pemFile = self::createTempPemFile($pemCert);
            $options = [
                'ssl' => [
                    'verify_peer' => true,
                    'cafile' => $pemFile,
                    'verify_peer_name' => true,
                ]
            ];

            $context = stream_context_create($options);
            if (!is_resource($context)) {
                throw new \RuntimeException("stream_context_create() failed");
            }

            return $context;
        } catch (\Throwable $e) {
            throw new VertexCacheSdkException("Failed to create secure socket connection");
        }
    }

    /**
     * Creates an insecure stream context that bypasses certificate validation.
     *
     * @return resource
     * @throws VertexCacheSdkException
     */
    public static function createInsecureSocketContext()
    {
        try {
            $contextOptions = [
                'ssl' => [
                    'verify_peer' => false,
                    'verify_peer_name' => false
                ]
            ];
            $context = stream_context_create($contextOptions);
            if (!is_resource($context)) {
                throw new \RuntimeException("stream_context_create() failed");
            }

            return $context;
        } catch (\Throwable $e) {
            throw new VertexCacheSdkException("Failed to create non secure socket connection");
        }
    }

    /**
     * Writes the PEM cert to a temporary file and returns the path.
     *
     * @param string $pemCert
     * @return string
     * @throws \RuntimeException
     */
    private static function createTempPemFile(string $pemCert): string
    {
        $tempFile = tempnam(sys_get_temp_dir(), 'cert_');
        if (!$tempFile) {
            throw new \RuntimeException("Failed to create temporary file for cert");
        }

        $bytesWritten = file_put_contents($tempFile, $pemCert);
        if ($bytesWritten === false || $bytesWritten === 0) {
            throw new \RuntimeException("Failed to write PEM certificate to temporary file");
        }

        return $tempFile;
    }
}
