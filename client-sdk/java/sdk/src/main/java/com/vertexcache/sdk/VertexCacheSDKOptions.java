package com.vertexcache.sdk;

public class VertexCacheSDKOptions {

    public static String DEFAULT_CLIENT_ID = "sdk-client";
    public static String DEFAULT_TENANT_ID = "sdk-tenant";
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int DEFAULT_PORT = 50505;
    public static int DEFAULT_READ_TIMEOUT = 3000;
    public static int DEFAULT_CONNECT_TIMEOUT = 3000;

    private String clientId = VertexCacheSDKOptions.DEFAULT_CLIENT_ID;
    private String tenantId = VertexCacheSDKOptions.DEFAULT_TENANT_ID;

    private String serverHost = VertexCacheSDKOptions.DEFAULT_HOST;
    private int serverPort = VertexCacheSDKOptions.DEFAULT_PORT;

    private boolean enableTlsEncryption  = false;
    private String tlsCertificate = null;
    private boolean verifyCertificate = false;

    private boolean enablePublicKeyEncryption = false;
    private String publicKey = null;

    private int readTimeout = VertexCacheSDKOptions.DEFAULT_READ_TIMEOUT;
    private int connectTimeout = VertexCacheSDKOptions.DEFAULT_CONNECT_TIMEOUT;

    public String getServerHost() {
        return serverHost;
    }

    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientId() { return clientId; }

    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getTenantId() { return tenantId; }

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

    public boolean isEnablePublicKeyEncryption() {
        return enablePublicKeyEncryption;
    }

    public void setEnablePublicKeyEncryption(boolean enablePublicKeyEncryption) {
        this.enablePublicKeyEncryption = enablePublicKeyEncryption;
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
}
