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
package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.PemUtil;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.exception.VertexCacheSSLServerSocketException;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Utility class for creating an SSLServerSocket with TLS configuration.
 *
 * Loads server certificate and private key from configured PEM files,
 * initializes key and trust managers, and sets up SSLContext with TLSv1.2 protocol
 * and a specific cipher suite.
 *
 * Throws VertexCacheSSLServerSocketException on any failure during setup.
 */
public class TLSHelper {

    private static final String[] ENABLED_PROTOCOLS = {
            "TLSv1.2", "TLSv1.3"
    };

    private static final String[] ENABLED_CIPHER_SUITES = {
            // Modern AEAD + Forward Secure (best choices)
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", // Only if supported

            // CBC-based ECDHE (fallback)
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",

            // RSA-based AEAD (widely supported)
            "TLS_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_RSA_WITH_AES_256_GCM_SHA384",

            // RSA-based CBC (legacy compatibility)
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_256_CBC_SHA",
            "TLS_RSA_WITH_AES_256_CBC_SHA256"
    };

    public static SSLServerSocket createSecureSocket(int port) throws VertexCacheSSLServerSocketException {
        try {
            String certPem = Config.getInstance().getSecurityConfigLoader().getTlsCertificate();
            String keyPem = Config.getInstance().getSecurityConfigLoader().getTlsPrivateKey();

            X509Certificate certificate = PemUtil.loadCertificate(certPem);
            PrivateKey privateKey = PemUtil.loadPrivateKey(keyPem);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setKeyEntry("server", privateKey, Config.getInstance().getSecurityConfigLoader().getTlsKeyStorePassword().toCharArray(), new X509Certificate[]{certificate});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, Config.getInstance().getSecurityConfigLoader().getTlsKeyStorePassword().toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);

            serverSocket.setEnabledProtocols(ENABLED_PROTOCOLS);
            serverSocket.setEnabledCipherSuites(ENABLED_CIPHER_SUITES);

            return serverSocket;

        } catch (Exception e) {
            LogHelper.getInstance().logError(e.getMessage());
            throw new VertexCacheSSLServerSocketException("TLS Initialization failed, due to invalid certs " + e.getMessage());
        }
    }

}
