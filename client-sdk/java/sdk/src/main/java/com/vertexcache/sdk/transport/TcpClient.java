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

import com.vertexcache.sdk.result.VertexCacheSdkException;
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

public class TcpClient implements TcpClientInterface {

    private final String host;
    private final int port;
    private final boolean useTls;
    private final boolean verifyServerCert;
    private final String serverCertPem;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final EncryptionMode encryptionMode;
    private final PublicKey publicKey;
    private final byte[] sharedKeyBytes;
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

        try {
            this.publicKey = encryptionMode == EncryptionMode.ASYMMETRIC ? loadPublicKey(publicKeyPem) : null;
            this.sharedKeyBytes = encryptionMode == EncryptionMode.SYMMETRIC
                    ? Base64.getDecoder().decode(sharedEncryptionKey)
                    : null;
            connect();
        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to initialize TcpClient", e);
        }
    }

    private void connect() {
        try {
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

        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to connect to " + host + ":" + port, e);
        }
    }

    public synchronized String send(String message) {
        return send(message.getBytes());
    }

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

    private void reconnect() {
        try {
            close();
            connect();
        } catch (Exception e) {
            throw new VertexCacheSdkException("Reconnect failed", e);
        }
    }

    private byte[] encrypt(byte[] plainText) {
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
            throw new VertexCacheSdkException("Encryption failed", e);
        }
    }

    private PublicKey loadPublicKey(String pem) {
        try {
            String cleaned = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(cleaned);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new VertexCacheSdkException("Invalid public key", e);
        }
    }

    private SSLSocketFactory createVerifiedSocketFactory(String pemCert) {
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
            throw new VertexCacheSdkException("Failed to create TLS socket factory from server certificate", e);
        }
    }

    private SSLSocketFactory createInsecureSocketFactory() {
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
            throw new VertexCacheSdkException("Failed to create insecure TLS context", e);
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
