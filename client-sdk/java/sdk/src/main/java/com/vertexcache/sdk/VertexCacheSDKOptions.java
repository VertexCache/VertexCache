package com.vertexcache.sdk;

import com.vertexcache.sdk.transport.crypto.EncryptionMode;

public class VertexCacheSDKOptions {

    public static String DEFAULT_CLIENT_ID = "sdk-client";
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int DEFAULT_PORT = 50505;
    public static int DEFAULT_READ_TIMEOUT = 3000;
    public static int DEFAULT_CONNECT_TIMEOUT = 3000;

    private String clientId = VertexCacheSDKOptions.DEFAULT_CLIENT_ID;
    private String clientToken = null;

    private String serverHost = VertexCacheSDKOptions.DEFAULT_HOST;
    private int serverPort = VertexCacheSDKOptions.DEFAULT_PORT;

    private boolean enableTlsEncryption  = false;
    private String tlsCertificate = null;
    private boolean verifyCertificate = false;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPublicKey = false;
    private boolean encryptWithSharedKey = false;
    private String publicKey = null;
    private String sharedEncryptionKey;

    private int readTimeout = VertexCacheSDKOptions.DEFAULT_READ_TIMEOUT;
    private int connectTimeout = VertexCacheSDKOptions.DEFAULT_CONNECT_TIMEOUT;

    public String getServerHost() {
        return serverHost;
    }

    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientId() { return clientId; }

    public void setClientToken(String clientToken) { this.clientToken = clientToken; }

    public String getClientToken() { return clientToken; }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isEnableTlsEncryption() {
        return enableTlsEncryption;
    }

    public void setEnableTlsEncryption(boolean enableTlsEncryption) {
        this.enableTlsEncryption = enableTlsEncryption;
    }

    public String getTlsCertificate() {
        return tlsCertificate;
    }

    public void setTlsCertificate(String tlsCertificate) {
        this.tlsCertificate = tlsCertificate;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public void setVerifyCertificate(boolean verifyCertificate) {
        this.verifyCertificate = verifyCertificate;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public EncryptionMode getEncryptionMode() { return encryptionMode; }

    public void setEncryptionMode(EncryptionMode encryptionMode) {this.encryptionMode = encryptionMode; }

    public String getSharedEncryptionKey() { return sharedEncryptionKey; }

    public void setSharedEncryptionKey(String sharedEncryptionKey) { this.sharedEncryptionKey = sharedEncryptionKey; }

}
