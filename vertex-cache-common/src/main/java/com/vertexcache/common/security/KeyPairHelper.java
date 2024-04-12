package com.vertexcache.common.security;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyPairHelper {

    private static String ALGO_RSA = "RSA";
    private static int KEY_SIZE = 2048;

    public static KeyPair generate() throws Exception {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyPairHelper.ALGO_RSA);
            keyPairGenerator.initialize(KeyPairHelper.KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new Exception("Failed to generate public private key pair");
        }
    }

    /*
     * Encode Key Base64
     */
    public static String encodeKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey decodePublicKey(String keyBase64) throws Exception {
        try {
            return (PublicKey) decodeKey(keyBase64, "RSA", false);
        } catch (Exception exception) {
            throw new Exception("Failed to decode public key, check if valid key");
        }
    }

    public static PrivateKey decodePrivateKey(String keyBase64) throws Exception {
        try {
            return (PrivateKey) decodeKey(keyBase64, "RSA", true);
        } catch (Exception exception) {
            throw new Exception("Failed to decode private key, check if valid key");
        }
    }

    /*
     * Decode Key Base64
     */
    private static Key decodeKey(String base64Key, String algorithm, boolean isPrivate) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        if (isPrivate) {
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
        } else {
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        }
    }
}
