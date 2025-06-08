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
package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.VertexCacheSdkException;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

/**
 * SSLHelper provides SSL/TLS socket factory utilities for use within the VertexCache SDK.
 *
 * This class supports two distinct TLS configurations:
 *
 * - createVerifiedSocketFactory(String pemCert): Creates an SSLSocketFactory that validates the server’s
 *   certificate against a trusted PEM-encoded X.509 certificate. Suitable for secure production deployments.
 *
 * - createInsecureSocketFactory(): Creates an SSLSocketFactory that bypasses certificate validation entirely.
 *   Intended for development or testing environments where self-signed certificates may be used.
 *
 * All methods throw VertexCacheSdkException on error. This helper abstracts SSL context and trust manager setup,
 * enabling flexible but secure configuration of encrypted socket connections.
 */
public class SSLHelper {

    /**
     * Creates an SSL socket factory that enforces server certificate verification using a provided PEM certificate.
     * This ensures the TLS connection is only established if the server's certificate matches the trusted PEM.
     *
     * Recommended for secure production deployments where server authenticity must be validated.
     *
     * @param pemCert PEM-encoded X.509 certificate string for the expected server certificate
     * @throws VertexCacheSdkException if the certificate is invalid or SSL context creation fails
     */
    public static SSLSocketFactory createVerifiedSocketFactory(String pemCert) throws VertexCacheSdkException {
        try (InputStream certInputStream = new ByteArrayInputStream(pemCert.getBytes())) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certInputStream);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null);
            String alias = "cert-" + System.currentTimeMillis();
            ks.setCertificateEntry(alias, cert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            return ctx.getSocketFactory();
        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to create secure socket connection");
        }
    }

    /**
     * Creates an SSL socket factory that disables certificate verification.
     * This enables encrypted communication without validating the server's certificate,
     * which is useful for development or testing environments where self-signed or invalid certs are used.
     *
     * Not recommended for production due to the lack of authentication and potential for MITM attacks.
     */
    public static SSLSocketFactory createInsecureSocketFactory() throws VertexCacheSdkException {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new java.security.SecureRandom());
            return ctx.getSocketFactory();
        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to create non secure socket connection");
        }
    }
}
