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
use VertexCache\Comm\SocketHelper;
use VertexCache\Model\ClientOption;
use VertexCache\Model\VertexCacheSdkException;

class SocketHelperTest extends TestCase
{
    private const ENABLE_LIVE_TLS_TESTS = false;
    private const MOCK_PORT = 18888;
    private const UNUSED_PORT = 65534;
    private const BLACKHOLE_IP = '10.255.255.1';

    private $serverSocket;

    private const VALID_PEM_CERT = <<<CERT
-----BEGIN CERTIFICATE-----
MIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV
BAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x
EDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv
Y2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj
Ia3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ
J+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf
lIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM
6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz
aaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ
DXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo
MA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH
JaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8
JguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx
iAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul
v/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4
qwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP
-----END CERTIFICATE-----
CERT;

    protected function setUp(): void
    {
        $this->serverSocket = @stream_socket_server("tcp://127.0.0.1:" . self::MOCK_PORT, $errno, $errstr);
        if (!$this->serverSocket) {
            $this->markTestSkipped("Unable to bind to 127.0.0.1:" . self::MOCK_PORT . " - $errstr ($errno)");
        }
    }

    protected function tearDown(): void
    {
        if (is_resource($this->serverSocket)) {
            fclose($this->serverSocket);
        }
    }

    public function testCreateNonSecureSocket_shouldSucceed()
    {
        $option = new ClientOption("127.0.0.1", self::MOCK_PORT);
        $option->setConnectTimeout(1);
        $option->setReadTimeout(1);

        $socket = SocketHelper::createSocketNonTLS($option);
        $this->assertIsResource($socket);
        fclose($socket);
    }

    public function testCreateSecureSocket_shouldFailDueToMissingTLSContext()
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->expectExceptionMessageMatches('/^Failed to create Secure Socket.*/');

        $option = new ClientOption("127.0.0.1", self::MOCK_PORT);
        $option->setVerifyCertificate(true);
        $option->setTlsCertificate(null);

        SocketHelper::createSecureSocket($option);
    }

    public function testCreateSecureSocket_shouldFailWithBadCertificate()
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->expectExceptionMessageMatches('/^Failed to create Secure Socket.*/');

        $option = new ClientOption("127.0.0.1", self::MOCK_PORT);
        $option->setVerifyCertificate(true);
        $option->setTlsCertificate("not a cert");

        SocketHelper::createSecureSocket($option);
    }

    /**
     * @group live
     */
    public function testCreateSecureSocket_shouldSucceedWithLiveServer()
    {
        if (!self::ENABLE_LIVE_TLS_TESTS) {
            $this->markTestSkipped("Live TLS test skipped");
        }

        $option = new ClientOption("127.0.0.1", 50505); // must match TLS server port
        $option->setVerifyCertificate(true);
        $option->setTlsCertificate(self::VALID_PEM_CERT);
        $option->setConnectTimeout(1);
        $option->setReadTimeout(1);

        try {
            $socket = SocketHelper::createSecureSocket($option);
            $this->assertIsResource($socket);
            fclose($socket);
        } catch (VertexCacheSdkException $e) {
            $this->markTestSkipped("Live TLS test failed: " . $e->getMessage());
        }
    }

}
