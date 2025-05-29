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
package com.vertexcache.common.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for encoding and decoding PEM-formatted strings.
 *
 * This class provides convenience methods for:
 *  - Encoding byte arrays as PEM strings with custom headers/footers
 *  - Decoding PEM strings back into raw byte arrays
 *  - Stripping PEM headers and formatting for parsing
 *
 * Commonly used when working with keys, certificates, and cryptographic material
 * in Base64-encoded PEM format.
 *
 * Note: This utility uses standard Java libraries and does not depend on external providers.
 */
public class PemUtil {

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

    public static X509Certificate loadCertificate(String input) throws Exception {
        String content = loadPemContent(input);
        try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(stream);
        }
    }

    public static PrivateKey loadPrivateKey(String input) throws Exception {
        String content = loadPemContent(input);
        if (!content.contains("-----BEGIN PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Invalid private key: missing BEGIN header");
        }
        String base64 = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    public static String loadPemContent(String input) throws IOException {
        Path path = Paths.get(input);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8).trim();
        } else {
            return input.replace("\\n", "\n").trim();
        }
    }

    public static boolean isFilePath(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        Path path = Paths.get(input);
        return Files.exists(path) && Files.isRegularFile(path);
    }

}