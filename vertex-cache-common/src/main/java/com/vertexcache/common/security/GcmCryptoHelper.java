package com.vertexcache.common.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class GcmCryptoHelper {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

    private static final int GCM_IV_LENGTH = 12;
    private static final int CBC_IV_LENGTH = 16;
    private static final int GCM_TAG_LENGTH = 128;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] wencrypt(byte[] plaintext, byte[] keyBytes, String transformation) throws Exception {
        byte[] iv = generateIv(transformation);

        Cipher cipher = Cipher.getInstance(transformation);
        SecretKeySpec key = new SecretKeySpec(keyBytes, AES);

        if (AES_GCM_NO_PADDING.equals(transformation)) {
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } else if (AES_CBC_PKCS5.equals(transformation)) {
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } else {
            throw new IllegalArgumentException("Unsupported transformation: " + transformation);
        }

        byte[] ciphertext = cipher.doFinal(plaintext);

        // Concatenate IV + ciphertext
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return combined;
    }

    public static byte[] decrypt(byte[] encrypted, byte[] keyBytes, String transformation) throws Exception {
        int ivLength = getIvLength(transformation);
        if (encrypted.length < ivLength) {
            throw new IllegalArgumentException("Invalid encrypted data: too short");
        }

        byte[] iv = new byte[ivLength];
        byte[] ciphertext = new byte[encrypted.length - ivLength];
        System.arraycopy(encrypted, 0, iv, 0, ivLength);
        System.arraycopy(encrypted, ivLength, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(transformation);
        SecretKeySpec key = new SecretKeySpec(keyBytes, AES);

        if (AES_GCM_NO_PADDING.equals(transformation)) {
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        } else if (AES_CBC_PKCS5.equals(transformation)) {
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        } else {
            throw new IllegalArgumentException("Unsupported transformation: " + transformation);
        }

        return cipher.doFinal(ciphertext);
    }

    private static byte[] generateIv(String transformation) {
        int length = getIvLength(transformation);
        byte[] iv = new byte[length];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private static int getIvLength(String transformation) {
        if (AES_GCM_NO_PADDING.equals(transformation)) return GCM_IV_LENGTH;
        if (AES_CBC_PKCS5.equals(transformation)) return CBC_IV_LENGTH;
        throw new IllegalArgumentException("Unsupported transformation: " + transformation);
    }

    public static byte[] decodeBase64Key(String base64) {
        return Base64.getDecoder().decode(base64.trim());
    }

    public static String encodeBase64Key(byte[] raw) {
        return Base64.getEncoder().encodeToString(raw);
    }

    public static String generateBase64Key() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(256); // AES-256
        SecretKey key = keyGen.generateKey();
        return encodeBase64Key(key.getEncoded());
    }
}
