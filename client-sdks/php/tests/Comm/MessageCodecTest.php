<?php
// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
// Licensed under the Apache License, Version 2.0
// ------------------------------------------------------------------------------

use PHPUnit\Framework\TestCase;
use VertexCache\Comm\MessageCodec;

class MessageCodecTest extends TestCase
{
    public function testWriteThenReadFramedMessage()
    {
        $payload = "Hello VertexCache";
        $frame = MessageCodec::writeFramedMessage($payload);
        [$decoded, $remaining] = MessageCodec::readFramedMessage($frame);

        $this->assertEquals($payload, $decoded);
        $this->assertEquals("", $remaining);
    }

    public function testInvalidVersionByte()
    {
        $frame = pack('N', 3) . chr(0x02) . "abc";
        $this->expectException(\Exception::class);
        $this->expectExceptionMessage("Unsupported protocol version");
        MessageCodec::readFramedMessage($frame);
    }

    public function testTooShortHeaderReturnsNull()
    {
        $this->assertNull(MessageCodec::readFramedMessage("\x01\x02"));
    }

    public function testTooLargePayloadRejected()
    {
        $this->expectException(\Exception::class);
        $this->expectExceptionMessage("Message too large");
        $big = str_repeat("A", MessageCodec::MAX_MESSAGE_SIZE + 1);
        MessageCodec::writeFramedMessage($big);
    }

    public function testWriteEmptyPayloadThenReadShouldFail()
    {
        $frame = MessageCodec::writeFramedMessage("");
        $this->expectException(\Exception::class);
        $this->expectExceptionMessage("Invalid message length");
        MessageCodec::readFramedMessage($frame);
    }

    public function testUtf8MultibytePayload()
    {
        $original = "你好, VertexCache 🚀";
        $frame = MessageCodec::writeFramedMessage($original);
        [$decoded, $remaining] = MessageCodec::readFramedMessage($frame);

        $this->assertEquals($original, $decoded);
        $this->assertEquals("", $remaining);
    }

    public function testHexDumpForInterSdkComparison()
    {
        $frame = MessageCodec::writeFramedMessage("ping");
        echo "Framed hex: " . strtoupper(bin2hex($frame)) . "\n";

        // Prevent risky test flag
        $this->assertNotEmpty($frame);
    }

}
