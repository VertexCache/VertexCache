package com.vertexcache.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PemUtils {

    private static final Pattern PEM_PATTERN = Pattern.compile(
            "-----BEGIN ([A-Z ]+)-----(.*?)-----END \\1-----",
            Pattern.DOTALL
    );

    /**
     * Normalize a PEM block (CERTIFICATE, PUBLIC KEY, PRIVATE KEY).
     * - Strips whitespace
     * - Validates base64
     * - Rewraps at 64-char lines
     */
    public static String normalizePemBlock(String pem) {
        if (pem == null || pem.trim().isEmpty()) {
            throw new IllegalArgumentException("PEM content is null or empty");
        }

        Matcher matcher = PEM_PATTERN.matcher(pem.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("PEM format invalid: BEGIN/END block not found");
        }

        String type = matcher.group(1).trim();
        String base64Body = matcher.group(2).replaceAll("\\s+", "");

        if (base64Body.length() % 4 != 0) {
            throw new IllegalArgumentException("PEM base64 body is invalid length: " + base64Body.length());
        }

        // Base64 decode for validation
        try {
            Base64.getDecoder().decode(base64Body);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PEM base64 body is invalid: " + e.getMessage(), e);
        }

        // Rewrap to PEM format
        StringBuilder normalized = new StringBuilder();
        normalized.append("-----BEGIN ").append(type).append("-----\n");
        for (int i = 0; i < base64Body.length(); i += 64) {
            normalized.append(base64Body, i, Math.min(i + 64, base64Body.length())).append("\n");
        }
        normalized.append("-----END ").append(type).append("-----\n");

        return normalized.toString();
    }

    /**
     * Extracts the base64 body from a PEM block, stripping headers and whitespace.
     */
    public static String extractBase64FromPem(String pem) {
        Matcher matcher = PEM_PATTERN.matcher(pem.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("PEM format invalid: BEGIN/END block not found");
        }
        return matcher.group(2).replaceAll("\\s+", "");
    }

    public static String extractBase64FromPem(String pem, String type) {
        String begin = "-----BEGIN " + type + "-----";
        String end = "-----END " + type + "-----";

        if (!pem.contains(begin) || !pem.contains(end)) {
            throw new IllegalArgumentException("PEM format invalid: missing BEGIN/END block");
        }

        return pem
                .replace(begin, "")
                .replace(end, "")
                .replaceAll("\\s+", ""); // 🧼 Remove whitespace/newlines
    }

    public static X509Certificate parseCertificate(String certPem) throws CertificateException, IOException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(certPem.getBytes(StandardCharsets.UTF_8))) {
            return (X509Certificate) factory.generateCertificate(inputStream);
        }
    }

    public static String loadPem(String value) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PEM source is null or empty");
        }

        File file = new File(value.trim());
        if (file.exists() && file.isFile()) {
            return Files.readString(file.toPath());
        } else {
            return value;
        }
    }

}