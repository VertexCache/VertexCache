package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.VertexCacheSdkException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

class KeyParserHelperTest {

    private static final String VALID_PUBLIC_KEY_PEM = ""
            + "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\n"
            + "bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\n"
            + "UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\n"
            + "GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\n"
            + "NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n"
            + "6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\n"
            + "EwIDAQAB\n"
            + "-----END PUBLIC KEY-----";

    private static final String INVALID_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----INVALID-----END PUBLIC KEY-----";

    private static final String VALID_SHARED_KEY_BASE64 = "YWJjZGVmZ2hpamtsbW5vcA=="; // "abcdefghijklmnop"
    private static final String INVALID_SHARED_KEY_BASE64 = "%%%INVALID%%%";

    @Test
    void configPublicKeyIfEnabled_shouldSucceedWithValidPEM() {
        assertDoesNotThrow(() -> {
            PublicKey key = KeyParserHelper.configPublicKeyIfEnabled(VALID_PUBLIC_KEY_PEM);
            assertNotNull(key);
        });
    }

    @Test
    void configPublicKeyIfEnabled_shouldReturnRSAAlgorithm() {
        PublicKey key = KeyParserHelper.configPublicKeyIfEnabled(VALID_PUBLIC_KEY_PEM);
        assertEquals("RSA", key.getAlgorithm(), "Expected RSA algorithm");
    }

    @Test
    void configPublicKeyIfEnabled_shouldFailWithInvalidPEM() {
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            KeyParserHelper.configPublicKeyIfEnabled(INVALID_PUBLIC_KEY_PEM);
        });
        assertEquals("Invalid public key", ex.getMessage());
    }

    @Test
    void configSharedKeyIfEnabled_shouldSucceedWithValidBase64() {
        assertDoesNotThrow(() -> {
            byte[] key = KeyParserHelper.configSharedKeyIfEnabled(VALID_SHARED_KEY_BASE64);
            assertNotNull(key);
            assertEquals(16, key.length); // "abcdefghijklmnop"
        });
    }

    @Test
    void configSharedKeyIfEnabled_shouldReturnCorrectBytes() {
        byte[] expected = "abcdefghijklmnop".getBytes(StandardCharsets.UTF_8);
        byte[] actual = KeyParserHelper.configSharedKeyIfEnabled(VALID_SHARED_KEY_BASE64);
        assertArrayEquals(expected, actual, "Byte content mismatch");
    }

    @Test
    void configSharedKeyIfEnabled_shouldFailWithInvalidBase64() {
        VertexCacheSdkException ex = assertThrows(VertexCacheSdkException.class, () -> {
            KeyParserHelper.configSharedKeyIfEnabled(INVALID_SHARED_KEY_BASE64);
        });
        assertEquals("Invalid shared key", ex.getMessage());
    }
}
