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
package com.vertexcache.sdk.model;

import com.vertexcache.sdk.comm.KeyParserHelper;

import java.security.PublicKey;

/**
 * Configuration container for initializing the VertexCache SDK client.
 *
 * This class holds all user-specified options required to establish a connection
 * to a VertexCache server, including host, port, TLS settings, authentication tokens,
 * encryption modes (asymmetric or symmetric), and related keys or certificates.
 *
 * It provides a flexible way to customize client behavior, including security preferences.
 */

public class ClientOption {

    public static String DEFAULT_CLIENT_ID = "sdk-client";
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int DEFAULT_PORT = 50505;
    public static int DEFAULT_READ_TIMEOUT = 3000;
    public static int DEFAULT_CONNECT_TIMEOUT = 3000;

    private String clientId = ClientOption.DEFAULT_CLIENT_ID;
    private String clientToken = null;

    private String serverHost = ClientOption.DEFAULT_HOST;
    private int serverPort = ClientOption.DEFAULT_PORT;

    private boolean enableTlsEncryption  = false;
    private String tlsCertificate = null;
    private boolean verifyCertificate = false;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPublicKey = false;
    private boolean encryptWithSharedKey = false;

    private PublicKey publicKey = null;
    private String publicKeyAsString = null;

    private byte[] sharedEncryptionKey;
    private String sharedEncryptionKeyAsString;

    private int readTimeout = ClientOption.DEFAULT_READ_TIMEOUT;
    private int connectTimeout = ClientOption.DEFAULT_CONNECT_TIMEOUT;

    public String getClientId() {
        return clientId != null ? clientId : "";
    }
    public String getClientToken() {
        return clientToken != null ? clientToken : "";
    }
    public String getIdentCommand() {
        return String.format(
                "IDENT {\"client_id\":\"%s\", \"token\":\"%s\"}",
                this.getClientId(),
                this.getClientToken()
        );
    }

    public void setPublicKey(String publicKey) throws VertexCacheSdkException {
        this.publicKey = KeyParserHelper.configPublicKeyIfEnabled(publicKey);
        this.sharedEncryptionKeyAsString = publicKey;
    }

    public void setSharedEncryptionKey(String sharedEncryptionKey) throws VertexCacheSdkException {
        this.sharedEncryptionKey = KeyParserHelper.configSharedKeyIfEnabled(sharedEncryptionKey);
        this.sharedEncryptionKeyAsString = sharedEncryptionKey;
    }

    public boolean isEnableTlsEncryption() {
        return enableTlsEncryption;
    }
    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public PublicKey getPublicKeyAsObject() {
        return publicKey;
    }
    public String getPublicKeyAsString() {
        return publicKeyAsString;
    }
    public byte[] getSharedEncryptionKeyAsBytes() {
        return sharedEncryptionKey;
    }
    public String getSharedEncryptionKeyAsString() {
        return sharedEncryptionKeyAsString;
    }
    public String getServerHost() {
        return serverHost;
    }
    public int getServerPort() {
        return serverPort;
    }
    public String getTlsCertificate() {
        return tlsCertificate;
    }
    public int getReadTimeout() {
        return readTimeout;
    }
    public int getConnectTimeout() {
        return connectTimeout;
    }
    public EncryptionMode getEncryptionMode() { return encryptionMode; }

    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setClientToken(String clientToken) { this.clientToken = clientToken; }
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public void setEnableTlsEncryption(boolean enableTlsEncryption) {
        this.enableTlsEncryption = enableTlsEncryption;
    }
    public void setTlsCertificate(String tlsCertificate) {
        this.tlsCertificate = tlsCertificate;
    }
    public void setVerifyCertificate(boolean verifyCertificate) {
        this.verifyCertificate = verifyCertificate;
    }
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    public void setEncryptionMode(EncryptionMode encryptionMode) {this.encryptionMode = encryptionMode; }
}
