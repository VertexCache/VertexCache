package com.vertexcache.common.security.CertificateTrustManager;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class ServerCertificateTrustManagerVerification implements X509TrustManager {

    private final X509Certificate serverCertificate;

    public ServerCertificateTrustManagerVerification(String certificateFilePath) throws CertificateException {
        this.serverCertificate = this.loadServerCertificate(certificateFilePath);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Not implemented
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Validate server's certificate
        for (X509Certificate cert : x509Certificates) {
            cert.checkValidity();
            if (cert.equals(serverCertificate)) {
                return; // Server's certificate matches the expected certificate
            }
        }
        throw new CertificateException("Server's certificate is not trusted");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private X509Certificate loadServerCertificate(String certificateFilePath) throws CertificateException {
        // Load server certificate from file
        //try (FileInputStream fileInputStream = new FileInputStream("/Users/devbot/Development/VertexCache/vertex-cache-config/example_server_certificate.pem")) {
        try (FileInputStream fileInputStream = new FileInputStream(certificateFilePath)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.startsWith("-----")) {
                        sb.append(line);
                    }
                }
                byte[] certBytes = Base64.getDecoder().decode(sb.toString());
                return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBytes));
            }
        } catch (IOException e) {
            throw new CertificateException("Failed to load server certificate", e);
        }
    }

    private X509Certificate loadServerCertificate2() throws CertificateException {
        // Load server certificate (Example: Load from a file)
        try (InputStream inputStream = getClass().getResourceAsStream("/example_server_certificate.pem")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.startsWith("-----")) {
                        sb.append(line);
                    }
                }
                byte[] certBytes = Base64.getDecoder().decode(sb.toString());
                return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBytes));
            }
        } catch (IOException e) {
            throw new CertificateException("Failed to load server certificate", e);
        }
    }
}
