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
// ------------------------------------------------------------------------------

namespace VertexCache\Comm;

class MessageCodec
{
    public const MAX_MESSAGE_SIZE = 10485760; // 10MB

    // RSA with PKCS#1 v1.5 encryption
    public const PROTOCOL_VERSION_RSA_PKCS1 = 0x00000101;

    // AES-GCM symmetric encryption
    public const PROTOCOL_VERSION_AES_GCM = 0x00000181;

    // Default protocol version (static)
    public static int $protocolVersion = self::PROTOCOL_VERSION_RSA_PKCS1;

    /**
     * Writes a framed message to binary format: [length(4)][version(4)][payload]
     *
     * @param string $payload
     * @return string
     * @throws \Exception
     */
    public static function writeFramedMessage(string $payload): string
    {
        $length = strlen($payload);
        if ($length > self::MAX_MESSAGE_SIZE) {
            throw new \Exception("Message too large: $length");
        }

        $frame  = pack('N', $length);                       // 4-byte big-endian length
        $frame .= pack('N', self::$protocolVersion);        // 4-byte big-endian version
        $frame .= $payload;                                 // payload
        return $frame;
    }

    /**
     * Reads a framed message from the given binary stream.
     *
     * @param resource $stream
     * @return string|null
     * @throws \Exception
     */
    public static function readFramedMessage($stream): ?string
    {
        $header = fread($stream, 8); // 4 bytes length + 4 bytes version
        if ($header === false || strlen($header) < 8) {
            return null;
        }

        $parts = unpack('Nlength/Nversion', $header);
        $length = $parts['length'];
        $version = $parts['version'];

        if ($version !== self::$protocolVersion) {
            throw new \Exception("Invalid protocol version: 0x" . dechex($version));
        }

        if ($length <= 0 || $length > self::MAX_MESSAGE_SIZE) {
            throw new \Exception("Invalid framed message length: $length bytes");
        }

        $payload = '';
        while (strlen($payload) < $length) {
            $chunk = fread($stream, $length - strlen($payload));
            if ($chunk === false || $chunk === '') {
                return null;
            }
            $payload .= $chunk;
        }

        return $payload;
    }

    /**
     * Switch to AES-GCM protocol version (symmetric)
     */
    public static function switchToSymmetric(): void
    {
        self::$protocolVersion = self::PROTOCOL_VERSION_AES_GCM;
    }

    /**
     * Switch to RSA PKCS#1 protocol version (asymmetric)
     */
    public static function switchToAsymmetric(): void
    {
        self::$protocolVersion = self::PROTOCOL_VERSION_RSA_PKCS1;
    }
}
