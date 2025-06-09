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
use VertexCache\Model\VertexCacheSdkException;
use VertexCache\Comm\SSLHelper;

class SSLHelperTest extends TestCase
{
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

    public function testCreateVerifiedContext_validCert_shouldSucceed()
    {
        $context = SSLHelper::createVerifiedSocketContext(self::VALID_PEM_CERT);
        $this->assertIsResource($context);
        $options = stream_context_get_options($context);
        $this->assertTrue($options['ssl']['verify_peer']);
        $this->assertTrue($options['ssl']['verify_peer_name']);
        $this->assertFileExists($options['ssl']['cafile']);
    }

    public function testCreateVerifiedContext_invalidCert_shouldThrow()
    {
        $this->expectException(VertexCacheSdkException::class);
        SSLHelper::createVerifiedSocketContext("invalid pem string");
    }

    public function testCreateInsecureContext_shouldSucceed()
    {
        $context = SSLHelper::createInsecureSocketContext();
        $this->assertIsResource($context);
        $options = stream_context_get_options($context);
        $this->assertFalse($options['ssl']['verify_peer']);
        $this->assertFalse($options['ssl']['verify_peer_name']);
        $this->assertTrue($options['ssl']['allow_self_signed']);
    }

    /**
     * @group live
     */
    public function testInsecureTLSConnectionToLocalhost_ifEnabled()
    {
        $enableLive = false;
        if (!$enableLive) {
            $this->markTestSkipped('Live TLS test skipped; enableLive = false');
        }

        $context = SSLHelper::createInsecureSocketContext();
        $fp = @stream_socket_client("tls://localhost:50505", $errno, $errstr, 1, STREAM_CLIENT_CONNECT, $context);
        if ($fp === false) {
            $this->markTestSkipped("TLS connection failed: $errstr ($errno)");
        }

        $this->assertIsResource($fp);
        fclose($fp);
    }
}
