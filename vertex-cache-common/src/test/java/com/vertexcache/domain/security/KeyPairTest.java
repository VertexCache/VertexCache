package com.vertexcache.domain.security;

import com.vertexcache.common.security.KeyPairHelper;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;


public class KeyPairTest {

    @Test
    public void testGenerate() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            assertNotNull(keyPair);
            assertNotNull(keyPair.getPublic());
            assertNotNull(keyPair.getPrivate());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEncodeAndDecodePublicKey() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyBase64 = KeyPairHelper.encodeKey(publicKey);
            assertNotNull(publicKeyBase64);

            PublicKey decodedPublicKey = KeyPairHelper.decodePublicKey(publicKeyBase64);
            assertNotNull(decodedPublicKey);
            assertEquals(publicKey, decodedPublicKey);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEncodeAndDecodePrivateKey() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PrivateKey privateKey = keyPair.getPrivate();
            String privateKeyBase64 = KeyPairHelper.encodeKey(privateKey);
            assertNotNull(privateKeyBase64);

            PrivateKey decodedPrivateKey = KeyPairHelper.decodePrivateKey(privateKeyBase64);
            assertNotNull(decodedPrivateKey);
            assertEquals(privateKey, decodedPrivateKey);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testPublicKeyToString() {
        try {
            KeyPair keyPair = KeyPairHelper.generate();
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = KeyPairHelper.publicKeyToString(publicKey);
            assertNotNull(publicKeyString);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
