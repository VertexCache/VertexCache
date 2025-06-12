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
use VertexCache\Comm\ClientConnector;
use VertexCache\Model\ClientOption;
use VertexCache\Model\EncryptionMode;

class ClientConnectorTest extends TestCase
{
    public function testClientConnector_initialState(): void
    {
        $option = new ClientOption("127.0.0.1", 50505);
        $client = new ClientConnector($option);

        $this->assertFalse($client->isConnected());
    }

    public function testClientConnector_closeDoesNotError(): void
    {
        $option = new ClientOption("127.0.0.1", 50505);
        $client = new ClientConnector($option);

        // Should be safe to close even before connect
        $client->close();

        $this->assertFalse($client->isConnected());
    }

    public function testClientConnector_encryptionModeNoneIsPlaintext(): void
    {
        $option = new ClientOption("127.0.0.1", 50505);
        $option->setEncryptionMode(EncryptionMode::NONE);

        $client = new ClientConnector($option);
        $reflection = new \ReflectionClass($client);
        $method = $reflection->getMethod('encryptIfEnabled');
        $method->setAccessible(true);

        $result = $method->invoke($client, "hello world");
        $this->assertEquals("hello world", $result);
    }
}
