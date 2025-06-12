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
use VertexCache\Comm\MessageCodec;

class MessageCodecTest extends TestCase
{
    public function testWriteThenReadFramedMessage_shouldSucceed(): void
    {
        $original = "hello VCMP";
        $framed = MessageCodec::writeFramedMessage($original);

        $stream = fopen('php://memory', 'r+');
        fwrite($stream, $framed);
        rewind($stream);

        $result = MessageCodec::readFramedMessage($stream);
        $this->assertEquals($original, $result);

        fclose($stream);
    }

    public function testInvalidVersionByte_shouldThrow(): void
    {
        $stream = fopen('php://memory', 'r+');
        // Write valid length, but invalid version (e.g., 0xFF)
        fwrite($stream, pack('N', 3) . chr(0xFF) . "abc");
        rewind($stream);

        $this->expectException(\Exception::class);
        $this->expectExceptionMessage("Invalid protocol version");
        MessageCodec::readFramedMessage($stream);

        fclose($stream);
    }

    public function testTooLargePayload_shouldThrow(): void
    {
        $this->expectException(\Exception::class);
        $this->expectExceptionMessage("Message too large");

        $payload = str_repeat("A", MessageCodec::MAX_MESSAGE_SIZE + 1);
        MessageCodec::writeFramedMessage($payload);
    }

    public function testWriteEmptyPayloadThenReadShouldFail(): void
    {
        $stream = fopen('php://memory', 'r+');
        fwrite($stream, '');
        rewind($stream);

        $result = MessageCodec::readFramedMessage($stream);
        $this->assertNull($result);

        fclose($stream);
    }

    public function testPartialPayload_shouldReturnNull(): void
    {
        $stream = fopen('php://memory', 'r+');
        // Write valid header for 10 bytes, but only provide 5 bytes of payload
        fwrite($stream, pack('N', 10) . chr(MessageCodec::PROTOCOL_VERSION) . "abcde");
        rewind($stream);

        $result = MessageCodec::readFramedMessage($stream);
        $this->assertNull($result);

        fclose($stream);
    }
}
