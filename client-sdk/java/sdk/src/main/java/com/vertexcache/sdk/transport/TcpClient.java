package com.vertexcache.sdk.transport;

import com.vertexcache.sdk.result.VertexCacheSdkException;

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

public class TcpClient {

    private final String host;
    private final int port;
    private final boolean useTls;
    private final boolean verifyServerCert;
    private final String serverCertPem;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final boolean encryptMessages;
    private final PublicKey publicKey;

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
                     boolean encryptMessages,
                     String publicKeyPem) {

        this.host = host;
        this.port = port;
        this.useTls = useTls;
        this.verifyServerCert = verifyServerCert;
        this.serverCertPem = serverCertPem;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.encryptMessages = encryptMessages;

        try {
            this.publicKey = encryptMessages ? loadPublicKey(publicKeyPem) : null;
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

        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to connect to " + host + ":" + port, e);
        }
    }

    public synchronized String send(String message) {
        return send(message.getBytes());
    }

    public synchronized String send(byte[] rawBytes) {
        try {
            byte[] toSend = encryptMessages ? encrypt(rawBytes) : rawBytes;

            out.write(toSend);
            out.flush();

            byte[] buffer = new byte[4096];
            int read = in.read(buffer);
            if (read == -1) {
                throw new VertexCacheSdkException("Connection closed by server");
            }

            return new String(buffer, 0, read);

        } catch (IOException e) {
            try {
                reconnect();
                byte[] toSend = encryptMessages ? encrypt(rawBytes) : rawBytes;

                out.write(toSend);
                out.flush();

                byte[] buffer = new byte[4096];
                int read = in.read(buffer);
                if (read == -1) {
                    throw new VertexCacheSdkException("Connection closed after retry");
                }

                return new String(buffer, 0, read);

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
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new VertexCacheSdkException("Failed to encrypt message", e);
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
