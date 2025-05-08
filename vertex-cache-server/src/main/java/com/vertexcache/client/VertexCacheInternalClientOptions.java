package com.vertexcache.client;

import com.vertexcache.common.security.EncryptionMode;

import java.security.PublicKey;

public class VertexCacheInternalClientOptions {

    public static String DEFAULT_CLIENT_ID = "sdk-client";
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int DEFAULT_PORT = 50505;
    public static int DEFAULT_READ_TIMEOUT = 3000;
    public static int DEFAULT_CONNECT_TIMEOUT = 3000;

    private String clientId = VertexCacheInternalClientOptions.DEFAULT_CLIENT_ID;
    private String clientToken = null;

    private String serverHost = VertexCacheInternalClientOptions.DEFAULT_HOST;
    private int serverPort = VertexCacheInternalClientOptions.DEFAULT_PORT;

    private boolean enableTlsEncryption  = false;
    private String tlsCertificate = null;
    private boolean verifyCertificate = false;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPublicKey = false;
    private boolean encryptWithSharedKey = false;
    private PublicKey publicKey = null;
    private String sharedEncryptionKey;

    private int readTimeout = VertexCacheInternalClientOptions.DEFAULT_READ_TIMEOUT;
    private int connectTimeout = VertexCacheInternalClientOptions.DEFAULT_CONNECT_TIMEOUT;

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

    public PublicKey getPublicKey() { return publicKey;}

    public void setPublicKey(PublicKey publicKey) {
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