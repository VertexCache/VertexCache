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


namespace Comm;

use PHPUnit\Framework\TestCase;
use VertexCache\Model\ClientOption;
use VertexCache\Model\EncryptionMode;
use VertexCache\Comm\ClientConnector;

class ClientConnectorLiveTestWithSymmetric extends TestCase
{
    private const HOST = '127.0.0.1';
    private const PORT = 50505;
    private const CLIENT_ID = 'sdk-client-php';
    private const CLIENT_TOKEN = 'cb7d114f-dfab-4acb-b3ab-634638abb3f6';
    private const TEST_SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
    private const TLS_CERT = <<<CERT
-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
... (truncated for brevity; use your full cert)
-----END CERTIFICATE-----
CERT;

    protected function setUp(): void
    {
        if (getenv('VC_LIVE_TLS_SYMMETRIC_TEST') !== 'true') {
             $this->markTestSkipped('VC_LIVE_TEST not enabled');
        }
    }

    public function testLiveConnectAndPing_shouldSucceed(): void
    {
        $option = new ClientOption(self::HOST, self::PORT);
        $option->setClientId(self::CLIENT_ID);
        $option->setClientToken(self::CLIENT_TOKEN);
        $option->setEnableTlsEncryption(true);
        $option->setVerifyCertificate(false); // set true if cert is verifiable
        $option->setTlsCertificate(self::TLS_CERT);
        $option->setEncryptionMode(EncryptionMode::SYMMETRIC);
        $option->setSharedEncryptionKey(self::TEST_SHARED_KEY);

        $client = new ClientConnector($option);
        $client->connect();
        $this->assertTrue($client->isConnected());

        $reply = $client->send("PING");
        $this->assertNotNull($reply);
        $this->assertStringStartsWith("+PONG", $reply);

        $client->close();
        $this->assertFalse($client->isConnected());
    }
}
