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

class MessageCodec
{
    public const MAX_MESSAGE_SIZE = 10485760; // 10MB
    public const PROTOCOL_VERSION = 0x01;

    /**
     * Writes a framed message to binary format: [length(4)][version(1)][payload]
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

        $frame = pack('N', $length);                 // 4-byte big-endian length
        $frame .= chr(self::PROTOCOL_VERSION);       // 1-byte version
        $frame .= $payload;                          // payload
        return $frame;
    }

    /**
     * Reads a framed message from the given binary buffer.
     *
     * @param string $buffer
     * @return array|null [payload, remaining] or null if too short
     * @throws \Exception
     */
    public static function readFramedMessage(string $buffer): ?array
    {
        if (strlen($buffer) < 5) {
            return null;
        }

        $unpacked = unpack('Nlength/Cversion', substr($buffer, 0, 5));
        $length = $unpacked['length'];
        $version = $unpacked['version'];

        if ($version !== self::PROTOCOL_VERSION) {
            throw new \Exception("Unsupported protocol version: $version");
        }

        if ($length <= 0 || $length > self::MAX_MESSAGE_SIZE) {
            throw new \Exception("Invalid message length: $length");
        }

        if (strlen($buffer) < 5 + $length) {
            return null;
        }

        $payload = substr($buffer, 5, $length);
        $remaining = substr($buffer, 5 + $length);
        return [$payload, $remaining];
    }
}
