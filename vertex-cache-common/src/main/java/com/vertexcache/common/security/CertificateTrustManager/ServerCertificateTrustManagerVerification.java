package com.vertexcache.common.security.CertificateTrustManager;

import com.vertexcache.common.util.PemUtils;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ServerCertificateTrustManagerVerification implements X509TrustManager {

    private final X509Certificate serverCertificate;

    public ServerCertificateTrustManagerVerification(String certificateSource) throws CertificateException {
        try {
            String pemContent;

            // Heuristic: if it ends in `.pem` or looks like a file, treat it as a path
            File possibleFile = new File(certificateSource);
            if (possibleFile.exists() && possibleFile.isFile()) {
                pemContent = new String(java.nio.file.Files.readAllBytes(possibleFile.toPath()));
            } else {
                pemContent = certificateSource;
            }

            String normalized = PemUtils.normalizePemBlock(pemContent);
            this.serverCertificate = loadServerCertificate(normalized);
        } catch (IOException e) {
            throw new CertificateException("Could not read certificate", e);
        }
    }


    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Not implemented
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        for (X509Certificate cert : x509Certificates) {
            cert.checkValidity();
            if (cert.equals(serverCertificate)) {
                return;
            }
        }
        throw new CertificateException("Server's certificate is not trusted");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private X509Certificate loadServerCertificate(String certSource) throws CertificateException {
        try {
            String pemContent;

            Path path = Paths.get(certSource);
            if (Files.exists(path)) {
                // Read PEM from file
                pemContent = Files.readString(path, StandardCharsets.UTF_8);
            } else {
                // Assume inline PEM from config
                pemContent = certSource;
            }

            String normalizedPem;
            try {
                normalizedPem = PemUtils.normalizePemBlock(certSource);
            } catch (Exception e) {
                throw new CertificateException("Failed to load server certificate, malformed", e);
            }

            try (InputStream certStream = new ByteArrayInputStream(normalizedPem.getBytes(StandardCharsets.UTF_8))) {
                return (X509Certificate) CertificateFactory.getInstance("X.509")
                        .generateCertificate(certStream);
            }

        } catch (IOException e) {
            throw new CertificateException("Failed to load server certificate", e);
        }
    }
}
