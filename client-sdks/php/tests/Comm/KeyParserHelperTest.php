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
use VertexCache\Comm\KeyParserHelper;
use VertexCache\Model\VertexCacheSdkException;

class KeyParserHelperTest extends TestCase
{
    private string $validPem = <<<PEM
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----
PEM;

    private string $invalidPem = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----";
    private string $validSharedKey = "YWJjZGVmZ2hpamtsbW5vcA=="; // "abcdefghijklmnop"
    private string $invalidSharedKey = "%%%INVALID%%%";

    public function testConfigPublicKeyIfEnabledValid()
    {
        $result = KeyParserHelper::configPublicKeyIfEnabled($this->validPem);
        $this->assertIsString($result);
        $this->assertGreaterThan(0, strlen($result));
    }

    public function testConfigPublicKeyIfEnabledInvalid()
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->expectExceptionMessage("Invalid public key");
        KeyParserHelper::configPublicKeyIfEnabled($this->invalidPem);
    }

    public function testConfigSharedKeyIfEnabledValid()
    {
        $result = KeyParserHelper::configSharedKeyIfEnabled($this->validSharedKey);
        $this->assertEquals("abcdefghijklmnop", $result);
        $this->assertEquals(16, strlen($result));
    }

    public function testConfigSharedKeyIfEnabledInvalid()
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->expectExceptionMessage("Invalid shared key");
        KeyParserHelper::configSharedKeyIfEnabled($this->invalidSharedKey);
    }
}
