/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.sdk;

import com.vertexcache.sdk.model.GetResult;
import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.CommandResult;
import com.vertexcache.sdk.model.EncryptionMode;
import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Live integration tests for the VertexCache SDK.
 *
 * These tests require a local VertexCache server instance to be running and accessible.
 * The tests are configured to connect using a predefined test public key and a self-signed TLS certificate.
 *
 * Out of the box, the corresponding/respective certificate and public/private key are preconfigured at the server level
 * to match the values used in this test suite.
 *
 * WARNING: These credentials and certificates are intended for testing only.
 * Do NOT reuse them in production environments.
 *
 * These tests validate real network interactions including authentication, encryption,
 * and core command behaviors (e.g. GET, SET, DEL) over an actual TCP connection.
 *
 * This suite is designed to allow you to easily get a quick understanding of how the SDK works
 * and see VertexCache in action with minimal friction.
 */
@EnabledIfEnvironmentVariable(named = "VC_LIVE_TLS_ASYMMETRIC_TEST", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VertexCacheSDKLiveTest {

    private final static String CLIENT_ID = "sdk-client-java";
    private final static String CLIENT_TOKEN = "ea143c4a-1426-4d43-b5be-f0ecffe4a6c7";

    private final static String VERTEXCACHE_SERVER_HOST = "localhost";
    private final static int VERTEXCACHE_SERVER_PORT = 50505;

    private final static boolean ENABLE_TLS = true;
    private final static String TEST_TLS_CERT = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\\nBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x\\nEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv\\nY2FsaG9zdDAeFw0yNTA1MTgwMzU2NDdaFw0zNTA1MTYwMzU2NDdaMG4xEDAOBgNV\\nBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24x\\nEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEjAQBgNVBAMTCWxv\\nY2FsaG9zdDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMHVT5HdQkUj\\nIa3sYmLQUeOja7tKtAXi1cuhCLlrvgS2DKJa9cpkgi1dsKOjJmsTqo580e+jrpdQ\\nJ+mTybdKoG6CZWEqfMizut48aTQoBteiLFSZ9J2/6nCXhxugA+aQ94lhkj3lJIHf\\nlIZeIYHaPNXH9/K4oCODJ8P6MfeQjY1ZWbrcQ9PxHQhWV/60AfTuJRJ4T/HQmOqM\\n6IcYz2t7iviIYvQq37A+wr1ClgxlfuT6JScEA8J34GivskB2p/MEn8E8y/durORz\\naaF5RBpnsc+fzVwQuvkth993rnDemdrcvTF1bdF5t88Zt5FiPD4qDF+pKloHNMRQ\\nDXBYb9Wf/t8CAwEAAaMhMB8wHQYDVR0OBBYEFOYQaTvkoqgLjRhCYBMrwLqrVfJo\\nMA0GCSqGSIb3DQEBDAUAA4IBAQCVBHT1uqtm72g085JuWdjBoBDa6bJD3Wj3L+GH\\nJaKOF26wQmXtLV0KraH3t3SUxWOM865OcbOkIiSUjMIgqmmFh1quoF4NMBa0wye8\\nJguLk6Qpffd+YXfzddxi33jdCUWgyqcTKq7bfB5DbMP4U5yVxnlXwKB0dxkaEFSx\\niAUrhcZ1+iYjelrERk8MPj9FQIzQ8FwwF4oB8ShNDhDNWCOVbSdLXwMOLH84u/ul\\nv/I4U/5/mqGGTtwNyyzFS0GYgrYua4H7Aqer2g4wv8PUYwkaAfQ49CWm9kFQxgD4\\nqwwA44GZv7zAa89WHNpbIMAA8keexZkPzJBIQNSKy2d9dhcP\\n-----END CERTIFICATE-----";

    private final static EncryptionMode ENABLE_PUBLIC_PRIVATE_KEY_USE = EncryptionMode.ASYMMETRIC;
    private final static String TEST_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----";

    private  VertexCacheSDK sdk;

    @BeforeEach
    public void setUp() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);

        sdk = new VertexCacheSDK(clientOption);
        sdk.openConnection();
    }

    @AfterEach
    public void tearDown() {
        if (sdk != null) {
            sdk.close();
        }
    }

    @Test
    @Order(1)
    public void testPingShouldSucceed() {
        CommandResult result = sdk.ping();
        assertTrue(result.isSuccess(), "Ping should succeed");
        assertTrue(result.getMessage().startsWith("PONG"), "Expected PONG response");
    }

    @Test
    @Order(2)
    public void testSetShouldSucceed() {
        CommandResult result = sdk.set("test-key", "value-123");
        assertTrue(result.isSuccess(), "SET command should succeed");
        assertEquals("OK", result.getMessage(), "Expected OK response");
    }

    @Test
    @Order(3)
    public void testGetShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123");

        GetResult result = sdk.get("test-key");
        assertTrue(result.isSuccess(), "GET should succeed");
        assertEquals("value-123", result.getValue(), "Value should match what was set");
    }

    @Test
    @Order(4)
    public void testDelShouldSucceedAndRemoveKey() {
        sdk.set("delete-key", "to-be-deleted");

        CommandResult delResult = sdk.del("delete-key");
        assertTrue(delResult.isSuccess(), "DEL command is  should succeed");

        GetResult getResult = sdk.get("delete-key");
        assertTrue(getResult.isSuccess(), "GET should be true after deletion again, it's idempotent");
        assertNull(getResult.getValue(), "Deleted key should return null");
    }

    @Test
    @Order(5)
    public void testGetOnMissingKeyShouldFail() {
        GetResult result = sdk.get("nonexistent-key");
        assertTrue(result.isSuccess(), "GET should true for nonexistent key");
        assertNull(result.getValue(), "Value should be null for nonexistent key");
    }

    @Test
    @Order(6)
    public void testSetSecodaryIndexShouldSucceed() {
        CommandResult result = sdk.set("test-key", "value-123","test-secondary-index");
        assertTrue(result.isSuccess(), "SET command should succeed");
        assertEquals("OK", result.getMessage(), "Expected OK response");
    }

    @Test
    @Order(7)
    public void testSetSecodaryIndexAndTertiaryIndexShouldSucceed() {
        CommandResult result = sdk.set("test-key", "value-123","test-secondary-index","test-tertiary-index");
        assertTrue(result.isSuccess(), "SET command should succeed");
        assertEquals("OK", result.getMessage(), "Expected OK response");
    }

    @Test
    @Order(8)
    public void testGetBySecondaryIndexShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123","test-secondary-index");

        GetResult result = sdk.getBySecondaryIndex("test-secondary-index");
        assertTrue(result.isSuccess(), "GETIDX1 should succeed");
        assertEquals("value-123", result.getValue(), "Value should match what was set");
    }

    @Test
    @Order(9)
    public void testGetByTertiaryIndexShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123","test-secondary-index","test-tertiary-index");

        GetResult result = sdk.getByTertiaryIndex("test-tertiary-index");
        assertTrue(result.isSuccess(), "GETIDX2 should succeed");
        assertEquals("value-123", result.getValue(), "Value should match what was set");
    }

    @Test
    @Order(10)
    public void testMultibyteKeyAndValueShouldSucceed() {
        String multibyteKey = "键🔑値🌟";
        String multibyteValue = "测试🧪データ💾";

        CommandResult setResult = sdk.set(multibyteKey, multibyteValue);
        assertTrue(setResult.isSuccess(), "SET command with multibyte key/value should succeed");
        assertEquals("OK", setResult.getMessage());

        GetResult getResult = sdk.get(multibyteKey);
        assertTrue(getResult.isSuccess(), "GET command with multibyte key should succeed");
        assertEquals(multibyteValue, getResult.getValue(), "Multibyte value should be preserved");
    }

    @Test
    @Order(11)
    public void testFailedHost() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost("bad-host"); // Bad Host
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);
        assertThrows(VertexCacheSdkException.class, () -> {
            (new VertexCacheSDK(clientOption)).openConnection();
        });
    }

    @Test
    @Order(12)
    public void testFailedPort() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(0); // Bad Port
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);
        assertThrows(VertexCacheSdkException.class, () -> {
            (new VertexCacheSDK(clientOption)).openConnection();
        });
    }

    @Test
    @Order(13)
    public void testFailedSecureTLS() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setVerifyCertificate(true);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            (new VertexCacheSDK(clientOption)).openConnection();
        });
        assertTrue(
                ex.getMessage().contains("Failed to create Secure Socket"),
                "Expected TLS failure message"
        );
    }

    @Test
    @Order(14)
    public void testNonSecureTLS() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setVerifyCertificate(false);
        clientOption.setTlsCertificate(null); // Intentionally bad and it doesn't care, there is no validation
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);
    }

    @Test
    @Order(15)
    public void testInvalidPublicKey() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setVerifyCertificate(false);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> clientOption.setPublicKey(TEST_PUBLIC_KEY + "_BAD")
        );
        assertTrue(
                ex.getMessage().contains("Invalid public key"),
                "Expected public key failure message"
        );
    }

    @Test
    @Order(16)
    public void testInvalidSharedKey() {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setVerifyCertificate(false);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(EncryptionMode.SYMMETRIC);
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> clientOption.setSharedEncryptionKey("_BAD_SHARED_KEY")
        );
        assertTrue(
                ex.getMessage().contains("Invalid shared key"),
                "Expected shared key failure message"
        );
    }

    @Test
    @Order(17)
    public void testSetWithEmptyKeyShouldFail() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set("", "value-123")
        );
        assertTrue(ex.getMessage().contains("Missing Primary Key"));
    }

    @Test
    @Order(18)
    public void testSetWithEmptyValueShouldFail() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set("empty-value-key", "")
        );
        assertTrue(ex.getMessage().contains("Missing Value"));
    }

    @Test
    @Order(19)
    public void testSetWithNullKeyShouldThrow() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set(null, "value-123")
        );
        assertTrue(ex.getMessage().contains("Missing Primary Key"));
    }

    @Test
    @Order(20)
    public void testSetWithNullValueShouldThrow() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set("empty-value-key", null)
        );
        assertTrue(ex.getMessage().contains("Missing Value"));
    }

    @Test
    @Order(21)
    public void testSetWithEmptySecondaryIndexShouldThrow() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set("key", "value", "")
        );
        assertTrue(ex.getMessage().contains("Secondary key can't be empty when used"));
    }

    @Test
    @Order(22)
    public void testSetWithEmptyTertiaryIndexShouldThrow() {
        VertexCacheSdkException ex = assertThrows(
                VertexCacheSdkException.class,
                () -> sdk.set("key", "value", "sec-key","")
        );
        assertTrue(ex.getMessage().contains("Tertiary key can't be empty when used"));
    }
}
