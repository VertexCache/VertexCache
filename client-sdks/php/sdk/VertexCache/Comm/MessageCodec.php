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

class MessageCodec
{
    public const MAX_MESSAGE_SIZE = 10485760; // 10MB
    public const PROTOCOL_VERSION = 0x00000101;

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

        $frame  = pack('N', $length);                // 4-byte big-endian length
        $frame .= pack('N', self::PROTOCOL_VERSION); // 4-byte big-endian version
        $frame .= $payload;                          // payload
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

        if ($version !== self::PROTOCOL_VERSION) {
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
}
