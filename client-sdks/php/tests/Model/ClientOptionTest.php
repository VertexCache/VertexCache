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

namespace Tests\Model;

use PHPUnit\Framework\TestCase;
use VertexCache\Model\ClientOption;
use VertexCache\Model\EncryptionMode;

class ClientOptionTest extends TestCase
{
    public function testDefaults(): void
    {
        $option = new ClientOption();

        $this->assertEquals('sdk-client', $option->getClientId());
        $this->assertEquals('', $option->getClientToken());
        $this->assertEquals('127.0.0.1', $option->getServerHost());
        $this->assertEquals(50505, $option->getServerPort());
        $this->assertFalse($option->isEnableTlsEncryption());
        $this->assertFalse($option->isVerifyCertificate());
        $this->assertEquals(3000, $option->getReadTimeout());
        $this->assertEquals(3000, $option->getConnectTimeout());
        $this->assertEquals(EncryptionMode::NONE, $option->getEncryptionMode());
        $this->assertStringContainsString('IDENT', $option->buildIdentCommand());
    }

    public function testSetValues(): void
    {
        $option = new ClientOption();
        $option->setClientId('test-client');
        $option->setClientToken('token123');
        $option->setServerHost('192.168.1.100');
        $option->setServerPort(9999);
        $option->setEnableTlsEncryption(true);
        $option->setVerifyCertificate(true);
        $option->setTlsCertificate('cert');
        $option->setConnectTimeout(1234);
        $option->setReadTimeout(5678);
        $option->setEncryptionMode(EncryptionMode::SYMMETRIC);

        $this->assertEquals('test-client', $option->getClientId());
        $this->assertEquals('token123', $option->getClientToken());
        $this->assertEquals('192.168.1.100', $option->getServerHost());
        $this->assertEquals(9999, $option->getServerPort());
        $this->assertTrue($option->isEnableTlsEncryption());
        $this->assertTrue($option->isVerifyCertificate());
        $this->assertEquals('cert', $option->getTlsCertificate());
        $this->assertEquals(1234, $option->getConnectTimeout());
        $this->assertEquals(5678, $option->getReadTimeout());
        $this->assertEquals(EncryptionMode::SYMMETRIC, $option->getEncryptionMode());
    }

    public function testIdentCommandGeneration(): void
    {
        $option = new ClientOption();
        $option->setClientId('my-id');
        $option->setClientToken('my-token');
        $expected = 'IDENT {"client_id":"my-id", "token":"my-token"}';
        $this->assertEquals($expected, $option->buildIdentCommand());
    }

    public function testNullTokenAndIdFallback(): void
    {
        $option = new ClientOption();
        $option->setClientId(null);
        $option->setClientToken(null);
        $ident = $option->buildIdentCommand();
        $this->assertStringContainsString('"client_id":""', $ident);
        $this->assertStringContainsString('"token":""', $ident);
    }
}
