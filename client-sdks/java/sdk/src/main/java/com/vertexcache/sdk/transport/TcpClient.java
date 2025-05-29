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
package com.vertexcache.sdk.transport;

import com.vertexcache.sdk.exception.*;
import com.vertexcache.sdk.transport.crypto.GcmCryptoHelper;
import com.vertexcache.sdk.transport.protocol.MessageCodec;
import com.vertexcache.sdk.transport.crypto.EncryptionMode;

import javax.crypto.Cipher;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * TcpClient is the core transport used by the VertexCache SDK to connect to and communicate with a VertexCache server.
 * It manages socket creation, TLS negotiation (with optional certificate verification), client authentication via IDENT,
 * and encryption of messages using either symmetric (AES-GCM) or asymmetric (RSA) methods.
 *
 * Key features:
 *  - Establishes raw or TLS socket connections
 *  - Supports both secure and insecure TLS modes
 *  - Automatically sends an IDENT command with client credentials
 *  - Encrypts all outgoing payloads based on configured encryption mode (could be NONE if NONE set)
 *  - Handles reconnect and retry logic on I/O failures
 *  - Wraps exceptions into meaningful SDK-specific types for better debugging
 *  - Configuration (host, port, TLS, encryption mode, etc.) is provided via the ClientOption class.
 *
 */
public class TcpClient implements TcpClientInterface {

    private final String host;
    private final int port;
    private final boolean useTls;
    private final boolean verifyServerCert;
    private final String serverCertPem;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final EncryptionMode encryptionMode;
    private PublicKey publicKey;
    private final String publicKeyPem;
    private final String sharedEncryptionKey;
    private byte[] sharedKeyBytes;
    private final String clientId;
    private final String clientToken;

    private Socket socket;
    private OutputStream out;
    private InputStream in;

    public TcpClient(String host,
                     int port,
                     boolean useTls,
                     boolean verifyServerCert,
                     String serverCertPem,
                     int connectTimeoutMs,
                     int readTimeoutMs,
                     EncryptionMode encryptionMode,
                     String publicKeyPem,
                     String sharedEncryptionKey,
                     String clientId,
                     String clientToken
                    ) {

        this.host = host;
        this.port = port;
        this.useTls = useTls;
        this.verifyServerCert = verifyServerCert;
        this.serverCertPem = serverCertPem;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.encryptionMode = encryptionMode;
        this.clientId = clientId != null ? clientId : "";
        this.clientToken = clientToken != null ? clientToken : "";
        this.publicKeyPem = publicKeyPem;
        this.sharedEncryptionKey = sharedEncryptionKey;
        connect();
    }

    /**
     * Establishes a connection to the VertexCache server using the configured host, port, TLS, and encryption options.
     *
     * This method performs the following steps:
     * - Loads the public or shared encryption key depending on the configured mode.
     * - Opens a socket connection to the server and applies timeouts.
     * - If TLS is enabled, wraps the socket with a secure or insecure SSL context depending on verification settings.
     * - Initializes buffered input/output streams.
     * - Sends an IDENT command with client credentials immediately upon connection.
     * - Reads and discards the IDENT response to ensure proper framing for subsequent commands.
     *
     * Exception handling includes detailed propagation for:
     * - Encryption configuration errors
     * - TLS certificate validation issues
     * - Socket or handshake failures
     *
     * @throws VertexCacheSdkException if the connection fails due to invalid encryption, TLS issues, or socket-level problems.
     */
    private void connect() {
        try {
            loadPublicKey();
            loadSharedKey();

            Socket baseSocket = new Socket();
            baseSocket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
            baseSocket.setSoTimeout(readTimeoutMs);

            if (useTls) {
                SSLSocketFactory factory = verifyServerCert
                        ? createVerifiedSocketFactory(serverCertPem)
                        : createInsecureSocketFactory();

                SSLSocket sslSocket = (SSLSocket) factory.createSocket(baseSocket, host, port, true);
                sslSocket.setSoTimeout(readTimeoutMs);
                sslSocket.startHandshake();
                this.socket = sslSocket;
            } else {
                this.socket = baseSocket;
            }

            this.out = new BufferedOutputStream(socket.getOutputStream());
            this.in = new BufferedInputStream(socket.getInputStream());

            // Send IDENT command immediately
            String safeClientId = clientId != null ? clientId : "";
            String safeToken = clientToken != null ? clientToken : "";
            String identPayload = String.format("{\"client_id\":\"%s\", \"token\":\"%s\"}", safeClientId, safeToken);
            String identCommand = "IDENT " + identPayload;
            byte[] identBytes = encrypt(identCommand.getBytes());
            MessageCodec.writeFramedMessage(out, identBytes);
            out.flush();

            // Read IDENT response immediately to avoid it leaking into next command
            byte[] identResponse = MessageCodec.readFramedMessage(in);
            if (identResponse != null) {
                String response = new String(identResponse);
                //System.out.println("[SDK] Server IDENT response: " + response);
            }

        } catch (VertexCacheSdkEncryptException encryptException) {
            throw new VertexCacheSdkException("Invalid encryption, potentially invalid Encryption Mode", encryptException);
        } catch (VertexCacheSdkPublicKeyException publicKeyException) {
            throw new VertexCacheSdkException("Invalid public key", publicKeyException);
        } catch (VertexCacheSdkNonSecureTLSCertificateException nonSecureTLSCertificateExceptiontlsException) {
            throw new VertexCacheSdkException("Invalid certificate. Fix the certificate.", nonSecureTLSCertificateExceptiontlsException);
        } catch (VertexCacheSdkSecureTLSCertificateException sdkSecureTLSCertificateException) {
            throw new VertexCacheSdkException("TLS verification failed or invalid certificate. Fix the certificate or disable verification by setting verify to false.", sdkSecureTLSCertificateException);
        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to connect to " + host + ":" + port, e);
        }
    }

    public synchronized String send(String message) {
        return send(message.getBytes());
    }

    /**
     * Sends an encrypted byte array over the TCP connection and returns the server's response as a String.
     * Automatically retries once on IOException by attempting to reconnect.
     *
     * This method is synchronized to ensure thread-safe access to the underlying socket streams.
     *
     * @param rawBytes the raw command bytes to be encrypted and sent
     * @return the decrypted response from the server as a String
     * @throws VertexCacheSdkException if the connection is closed, a reconnect attempt fails, or any unexpected error occurs
     */
    public synchronized String send(byte[] rawBytes) {
        try {
            byte[] toSend = encrypt(rawBytes);
            MessageCodec.writeFramedMessage(out, toSend);
            out.flush();
            byte[] response = MessageCodec.readFramedMessage(in);
            if (response == null) {
                throw new VertexCacheSdkException("Connection closed by server");
            }
            return new String(response);
        } catch (IOException e) {
            try {
                reconnect();
                byte[] toSend = encrypt(rawBytes);
                MessageCodec.writeFramedMessage(out, toSend);
                out.flush();
                byte[] response = MessageCodec.readFramedMessage(in);
                if (response == null) {
                    throw new VertexCacheSdkException("Connection closed after retry");
                }
                return new String(response);
            } catch (Exception retryEx) {
                throw new VertexCacheSdkException("Failed after reconnect attempt", retryEx);
            }
        } catch (Exception e) {
            throw new VertexCacheSdkException("Unexpected failure during send", e);
        }
    }

    /**
     * Attempts to close and re-establish the TCP connection.
     * Wraps any failure in a VertexCacheSdkException for consistent error propagation.
     *
     * Typically used after a connection drop or transient error.
     *
     * @throws VertexCacheSdkException if reconnection fails for any reason
     */
    private void reconnect() {
        try {
            close();
            connect();
        } catch (Exception e) {
            throw new VertexCacheSdkException("Reconnect failed", e);
        }
    }

    /**
     * Encrypts the provided plaintext using the configured encryption mode:
     * - ASYMMETRIC: Uses RSA public key encryption
     * - SYMMETRIC: Uses AES-GCM encryption via GcmCryptoHelper
     * - NONE: Returns the plaintext as-is without encryption
     *
     * @param plainText the raw data to encrypt
     * @return the encrypted byte array (or original if NONE)
     * @throws VertexCacheSdkEncryptException if encryption fails due to configuration or runtime error
     */
    private byte[] encrypt(byte[] plainText) throws VertexCacheSdkEncryptException {
        try {
            switch (encryptionMode) {
                case ASYMMETRIC:
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    return cipher.doFinal(plainText);
                case SYMMETRIC:
                    return GcmCryptoHelper.encrypt(plainText, sharedKeyBytes);
                case NONE:
                default:
                    return plainText;
            }
        } catch (Exception e) {
            throw new VertexCacheSdkEncryptException("Encryption failed for, text redacted *****");
        }
    }

    /**
     * Loads and decodes the symmetric encryption key from a Base64 string into raw bytes.
     * This is only performed if the encryption mode is SYMMETRIC; otherwise, the key is set to null.
     *
     * @throws VertexCacheSdkEncryptException if the Base64 decoding fails or the key is malformed
     */
    private void loadSharedKey() throws VertexCacheSdkSharedKeyException {
        try {
            if(encryptionMode == EncryptionMode.SYMMETRIC) {
                this.sharedKeyBytes = Base64.getDecoder().decode(this.sharedEncryptionKey);
            } else {
                this.sharedKeyBytes = null;
            }
        } catch (Exception ex) {
            throw new VertexCacheSdkEncryptException(ex);
        }
    }

    /**
     * Loads and parses an RSA public key from the provided PEM string if asymmetric encryption is enabled.
     * The PEM headers and any whitespace are stripped before base64 decoding and key generation.
     *
     * Only executed when the encryption mode is ASYMMETRIC; otherwise, the publicKey remains unset.
     *
     * @throws VertexCacheSdkPublicKeyException if the PEM string is invalid or key construction fails
     */
    private void loadPublicKey() throws VertexCacheSdkPublicKeyException {
        try {
            if(encryptionMode == EncryptionMode.ASYMMETRIC) {
                String cleaned = this.publicKeyPem
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
                byte[] decoded = Base64.getDecoder().decode(cleaned);
                this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            } else {
                this.publicKey = null;
            }
        } catch (Exception e) {
            throw new VertexCacheSdkPublicKeyException( e);
        }
    }

    /**
     * Creates an SSL socket factory that enforces server certificate verification using a provided PEM certificate.
     * This ensures the TLS connection is only established if the server's certificate matches the trusted PEM.
     *
     * Recommended for secure production deployments where server authenticity must be validated.
     *
     * @param pemCert PEM-encoded X.509 certificate string for the expected server certificate
     * @throws VertexCacheSdkSecureTLSCertificateException if the certificate is invalid or SSL context creation fails
     */
    private SSLSocketFactory createVerifiedSocketFactory(String pemCert) throws VertexCacheSdkSecureTLSCertificateException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = new ByteArrayInputStream(pemCert.getBytes());
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certInputStream);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null);
            ks.setCertificateEntry("server", cert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            return ctx.getSocketFactory();

        } catch (Exception e) {
            throw new VertexCacheSdkSecureTLSCertificateException(e);
        }
    }

    /**
     * Creates an SSL socket factory that disables certificate verification.
     * This enables encrypted communication without validating the server's certificate,
     * which is useful for development or testing environments where self-signed or invalid certs are used.
     *
     * Not recommended for production due to the lack of authentication and potential for MITM attacks.
     */
    private SSLSocketFactory createInsecureSocketFactory() throws VertexCacheSdkNonSecureTLSCertificateException{
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
            throw new VertexCacheSdkNonSecureTLSCertificateException(e);
        }
    }

    public boolean isConnected() {
        return socket != null &&
                socket.isConnected() &&
                !socket.isClosed() &&
                !socket.isInputShutdown() &&
                !socket.isOutputShutdown();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
