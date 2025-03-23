package com.vertexcache.common.security;

import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyPairHelper {

    private static final String ALGO_RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    public static KeyPair generate() throws Exception {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGO_RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new Exception("Failed to generate public/private key pair", e);
        }
    }

    public static String encodeKey(Key key) {
        if (key == null) {
            throw new IllegalStateException("❌ Key is null — likely failed to load or decode.");
        }
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return encodeKey(publicKey);
    }

    public static PublicKey loadPublicKey(String source) throws Exception {
        String pem = resolvePemSource(source);
        String base64 = extractBase64FromPem(pem, "PUBLIC KEY");
        return decodePublicKey(base64);
    }

    public static PrivateKey loadPrivateKey(String source) throws Exception {
        String pem = resolvePemSource(source);
        String base64 = extractBase64FromPem(pem, "PRIVATE KEY");
        return decodePrivateKey(base64);
    }

    public static PublicKey decodePublicKey(String keyBase64) throws Exception {
        try {
            return (PublicKey) decodeKey(keyBase64, ALGO_RSA, false);
        } catch (Exception e) {
            System.err.println("❌ Exception during decodePublicKey: " + e.getMessage());
            throw new Exception("Failed to decode public key — ensure it's valid Base64 content", e);
        }
    }

    public static PrivateKey decodePrivateKey(String keyBase64) throws Exception {
        try {
            return (PrivateKey) decodeKey(keyBase64, ALGO_RSA, true);
        } catch (Exception e) {
            System.err.println("❌ Exception during decodePrivateKey: " + e.getMessage());
            throw new Exception("Failed to decode private key — ensure it's valid Base64 content", e);
        }
    }

    private static Key decodeKey(String base64Key, String algorithm, boolean isPrivate)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        System.out.println("🔎 Decoded key byte length: " + decodedKey.length);

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return isPrivate
                ? keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey))
                : keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    private static String resolvePemSource(String source) throws Exception {
        File file = new File(source);
        if (file.exists() && file.isFile()) {
            System.out.println("📁 Loading PEM from file: " + file.getAbsolutePath());
            return Files.readString(file.toPath());
        }

        System.out.println("📄 Treating source as embedded PEM string.");
        return source;
    }

    private static String extractBase64FromPem(String pem, String type) {
        String beginMarker = "-----BEGIN " + type + "-----";
        String endMarker = "-----END " + type + "-----";

        if (!pem.contains(beginMarker) || !pem.contains(endMarker)) {
            throw new IllegalArgumentException("PEM format invalid: BEGIN/END block not found");
        }

        // 🔧 Strip the surrounding markers
        String base64Body = pem
                .replace(beginMarker, "")
                .replace(endMarker, "")
                .replaceAll("\\s+", ""); // remove all whitespace

        return base64Body;
    }
}


