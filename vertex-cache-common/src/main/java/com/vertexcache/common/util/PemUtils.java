package com.vertexcache.common.util;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PemUtils {

    private static final Pattern CERT_PATTERN = Pattern.compile(
            "-----BEGIN CERTIFICATE-----([^-]+)-----END CERTIFICATE-----",
            Pattern.DOTALL
    );

    public static String normalizePemBlock(String pem) {
        if (pem == null) throw new IllegalArgumentException("PEM content is null");

        Matcher matcher = CERT_PATTERN.matcher(pem.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException("PEM format invalid: BEGIN/END block not found");
        }

        // Extract base64 body and strip all whitespace
        String base64Body = matcher.group(1).replaceAll("\\s+", "");

        // Validate base64 length
        if (base64Body.length() % 4 != 0) {
            throw new IllegalArgumentException("PEM base64 body is invalid length: " + base64Body.length());
        }

        // Validate base64 content
        try {
            Base64.getDecoder().decode(base64Body);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PEM base64 body is invalid: " + e.getMessage(), e);
        }

        // Rewrap as standard PEM
        StringBuilder pemOut = new StringBuilder();
        pemOut.append("-----BEGIN CERTIFICATE-----\n");
        for (int i = 0; i < base64Body.length(); i += 64) {
            pemOut.append(base64Body, i, Math.min(i + 64, base64Body.length())).append("\n");
        }
        pemOut.append("-----END CERTIFICATE-----\n");

        return pemOut.toString();
    }
}



