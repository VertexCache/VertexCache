package com.vertexcache.server.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.config.Config;
import com.vertexcache.server.domain.command.CommandService;
import com.vertexcache.server.exception.VertexCacheSSLServerSocketException;

import javax.net.ssl.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.*;

public class SocketServer {

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int QUEUE_SIZE = 1000;
    private static final int SOCKET_IDLE_TIMEOUT_MS = 30000;

    private ExecutorService executor;
    private ServerSocket serverSocket = null;
    private Config config;

    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_RESET = "\u001B[0m";

    public SocketServer() {
        this.config = Config.getInstance();
    }

    public void execute() throws Exception {
        try {
            CommandService commandService = new CommandService();
            Cache.getInstance(config.getCacheEvictionPolicy(), config.getCacheSize());

            ServerSocket serverSocket;
            if (config.isEncryptTransport()) {
                serverSocket = secureSocket();
            } else {
                serverSocket = new ServerSocket(config.getServerPort());
            }

            outputStartupOK();
            this.executor = new ThreadPoolExecutor(
                    MAX_THREADS,
                    MAX_THREADS,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(QUEUE_SIZE),
                    new ThreadPoolExecutor.AbortPolicy()
            );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);

                String address = clientSocket.getInetAddress().getHostAddress();
                int port = clientSocket.getPort();
                String transport = (clientSocket instanceof SSLSocket) ? "TLS" : "Plain";
                String messageEncryption = config.getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No";

                outputInfo(transport + " client connected from " + address + ":" + port +
                        " (Encrypted Messages: " + messageEncryption + ")");

                this.executor.execute(new ClientHandler(clientSocket, config, commandService));
            }
        } catch (BindException e) {
            outputStartUpError("Error, Port already in use", e);
        } catch (IOException e) {
            outputStartUpError("Error, unexpected error, please try again.", e);
        } catch (VertexCacheSSLServerSocketException e) {
            outputStartUpError("Error with Transport Layer Encryption configuration.", e);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    public void shutdown() {
        try {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException exception) {
            LogHelper.getInstance().logError(exception.getMessage());
        }
    }

    private SSLServerSocket secureSocket() throws VertexCacheSSLServerSocketException {
        try {
            String certPem = this.config.getTlsCertificate();
            String keyPem = this.config.getTlsPrivateKey();

            X509Certificate certificate = loadCertificate(certPem);
            PrivateKey privateKey = loadPrivateKey(keyPem);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setKeyEntry("server", privateKey, this.config.getTlsKeyStorePassword().toCharArray(), new X509Certificate[]{certificate});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, this.config.getTlsKeyStorePassword().toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(config.getServerPort());

            serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA256"});

            return serverSocket;

        } catch (Exception e) {
            System.err.println("Secure socket initialization failed:");
            e.printStackTrace();
            throw new VertexCacheSSLServerSocketException(e);
        }
    }

    private void outputStartupOK() {
        this.outputStartup("Server Started");
    }

    private void outputStartup(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append(this.config.getAppName()).append(":").append(System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Port: ").append(config.getServerPort()).append(System.lineSeparator())
                .append("  Verbose: ").append(config.isEnableVerbose() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Cache Eviction Policy: ").append(config.getCacheEvictionPolicy().toString()).append(System.lineSeparator())
                .append("  Cache Size: ").append(config.getCacheSize()).append(System.lineSeparator())
                .append("  Encryption: ").append(System.lineSeparator())
                .append("    TLS Enabled (Transport): ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("    Message Layer Encrypted: ").append(config.getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No").append(config.getEncryptNote()).append(System.lineSeparator())
                .append("      Private/Public Key (RSA) Enabled: ").append(config.isEncryptWithPrivateKey() ? "Yes" : "No").append(System.lineSeparator())
                .append("      Shared Key (AES) Enabled: ").append(config.isEncryptWithSharedKey() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file set: ").append(config.isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!config.isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a").append(System.lineSeparator())
                .append("  Status: ").append(ANSI_GREEN).append(message).append(ANSI_RESET).append(System.lineSeparator());
        LogHelper.getInstance().logInfo(stringBuilder.toString());
    }

    private void outputStartUpError(String message, Exception exception) {
        this.outputStartup(message);
        LogHelper.getInstance().logError(exception.getMessage());
    }

    private void outputInfo(String message) {
        if (this.config.isEnableVerbose()) {
            LogHelper.getInstance().logInfo(message);
        }
    }

    private X509Certificate loadCertificate(String input) throws Exception {
        String content = loadPemContent(input);
        try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(stream);
        }
    }

    private PrivateKey loadPrivateKey(String input) throws Exception {
        String content = loadPemContent(input);
        if (!content.contains("-----BEGIN PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Invalid private key: missing BEGIN header");
        }
        String base64 = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    private String loadPemContent(String input) throws IOException {
        Path path = Paths.get(input);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8).trim();
        } else {
            return input.replace("\\n", "\n").trim();
        }
    }
}
