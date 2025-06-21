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
// ------------------------------------------------------------------------------

use PHPUnit\Framework\TestCase;
use VertexCache\VertexCacheSDK;
use VertexCache\Model\ClientOption;
use VertexCache\Model\EncryptionMode;
use VertexCache\Model\VertexCacheSdkException;

final class VertexCacheSDKLiveTest extends TestCase
{
    private const CLIENT_ID = 'sdk-client-php';
    private const CLIENT_TOKEN = 'cb7d114f-dfab-4acb-b3ab-634638abb3f6';
    private const HOST = 'localhost';
    private const PORT = 50505;
    private const TLS_CERT = <<<CERT
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

    private const PUBLIC_KEY = <<<KEY
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
EwIDAQAB
-----END PUBLIC KEY-----
KEY;

    private VertexCacheSDK $sdk;

    protected function setUp(): void
    {
        if (getenv('VC_LIVE_TLS_ASYMMETRIC_TEST') !== 'true') {
            $this->markTestSkipped('VC_LIVE_TLS_ASYMMETRIC_TEST not set, skipping TLS asymmetric tests.');
        }

        $option = new ClientOption();
        $option->setClientId(self::CLIENT_ID);
        $option->setClientToken(self::CLIENT_TOKEN);
        $option->setServerHost(self::HOST);
        $option->setServerPort(self::PORT);
        $option->setEnableTlsEncryption(true);
        $option->setTlsCertificate(self::TLS_CERT);
        $option->setEncryptionMode(EncryptionMode::ASYMMETRIC);
        $option->setPublicKey(self::PUBLIC_KEY);

        $this->sdk = new VertexCacheSDK($option);
        $this->sdk->openConnection();
    }

    protected function tearDown(): void
    {
        if (isset($this->sdk)) {
            $this->sdk->close();
        }
    }

    public function testPingShouldSucceed(): void
    {
        $result = $this->sdk->ping();
        $this->assertTrue($result->isSuccess());
        $this->assertStringStartsWith('PONG', $result->getMessage());
    }

    public function testSetAndGet(): void
    {
        $key = 'php-test-key';
        $value = 'test-value';

        $set = $this->sdk->set($key, $value);
        $this->assertTrue($set->isSuccess());
        $this->assertEquals('OK', $set->getMessage());

        $get = $this->sdk->get($key);
        $this->assertTrue($get->isSuccess());
        $this->assertEquals($value, $get->getValue());
    }

    public function testDelAndGetNull(): void
    {
        $this->sdk->set('to-delete', 'dead');

        $del = $this->sdk->del('to-delete');
        $this->assertTrue($del->isSuccess());

        $result = $this->sdk->get('to-delete');
        $this->assertTrue($result->isSuccess());
        $this->assertNull($result->getValue());
    }

    public function testSetWithIndices(): void
    {
        $this->assertTrue(
            $this->sdk->set('key1', 'val', 'idx1-key')->isSuccess()
        );

        $this->assertTrue(
            $this->sdk->set('key2', 'valx', 'idx1-key', 'idx2-key')->isSuccess()
        );

        $get1 = $this->sdk->getBySecondaryIndex('idx1-key');

        $this->assertTrue($get1->isSuccess());
        $this->assertEquals('valx', $get1->getValue());

        $get2 = $this->sdk->getByTertiaryIndex('idx2-key');
        $this->assertTrue($get2->isSuccess());
        $this->assertEquals('valx', $get2->getValue());
    }

    public function testMultibyteKeyAndValue(): void
    {
        $key = 'キー🔑';
        $val = 'データ💾';

        $this->assertTrue($this->sdk->set($key, $val)->isSuccess());
        $result = $this->sdk->get($key);

        $this->assertTrue($result->isSuccess());
        $this->assertEquals($val, $result->getValue());
    }

    public function testSetMissingKeyShouldFail(): void
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->sdk->set('', 'some-value');
    }

    public function testSetMissingValueShouldFail(): void
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->sdk->set('some-key', '');
    }

    public function testSetEmptySecondaryIndex(): void
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->sdk->set('key', 'val', '');
    }

    public function testSetEmptyTertiaryIndex(): void
    {
        $this->expectException(VertexCacheSdkException::class);
        $this->sdk->set('key', 'val', 'idx1', '');
    }
}
