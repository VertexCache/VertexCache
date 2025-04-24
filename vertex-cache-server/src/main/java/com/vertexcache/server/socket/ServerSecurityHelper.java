package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.PemUtil;
import com.vertexcache.core.setting.Config;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class ServerSecurityHelper {

    public static SSLServerSocket createSecureSocket() throws VertexCacheSSLServerSocketException {
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
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(Config.getInstance().getCoreConfigLoader().getServerPort());

            serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA256"});

            return serverSocket;

        } catch (Exception e) {
            LogHelper.getInstance().logError(e.getMessage());
            throw new VertexCacheSSLServerSocketException("TLS Initialization failed, due to invalid certs");
        }
    }

}
