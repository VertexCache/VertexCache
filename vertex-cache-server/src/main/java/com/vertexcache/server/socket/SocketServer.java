package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.status.SystemStatusReport;

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

    private static boolean isRunning = false;
    private static String statusMessage = "";

    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RESET = "\u001B[0m";

    public SocketServer() {

    }

    public void execute() throws Exception {
        try {
            CommandService commandService = new CommandService();
            Cache.getInstance(Config.getInstance().getCacheEvictionPolicy(), Config.getInstance().getCacheSize());

            ServerSocket serverSocket;
            if (Config.getInstance().isEncryptTransport()) {
                serverSocket = secureSocket();
            } else {
                serverSocket = new ServerSocket(Config.getInstance().getServerPort());
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
                String messageEncryption = Config.getInstance().getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No";

                outputInfo(transport + " client connected from " + address + ":" + port +
                        " (Encrypted Messages: " + messageEncryption + ")");

                this.executor.execute(new ClientHandler(clientSocket, Config.getInstance(), commandService));
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
            String certPem = Config.getInstance().getTlsCertificate();
            String keyPem = Config.getInstance().getTlsPrivateKey();

            X509Certificate certificate = loadCertificate(certPem);
            PrivateKey privateKey = loadPrivateKey(keyPem);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setKeyEntry("server", privateKey, Config.getInstance().getTlsKeyStorePassword().toCharArray(), new X509Certificate[]{certificate});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, Config.getInstance().getTlsKeyStorePassword().toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(Config.getInstance().getServerPort());

            serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA256"});

            return serverSocket;

        } catch (Exception e) {
            LogHelper.getInstance().logError(e.getMessage());
            throw new VertexCacheSSLServerSocketException(e);
        }
    }

    private void outputStartupOK() {
        statusMessage = "Server Started";
        isRunning = true;
        this.outputStartup();
    }

    private void outputStartup() {
        LogHelper.getInstance().logInfo(SystemStatusReport.getFullSystemReport());
    }

    private void outputStartUpError(String message, Exception exception) {
        statusMessage = message;
        isRunning = false;
        this.outputStartup();
        LogHelper.getInstance().logError(exception.getMessage());
    }

    private void outputInfo(String message) {
        if (Config.getInstance().isEnableVerbose()) {
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

    public static String getStatusSummary() {
        StringBuilder sb = new StringBuilder();
                sb.append(Config.getInstance().getAppName()).append(" Server Startup Report:").append(System.lineSeparator())
                .append("  Status: ").append(ANSI_GREEN).append(statusMessage).append(ANSI_RESET).append(System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Port: ").append(Config.getInstance().getServerPort()).append(System.lineSeparator())
                .append("  Verbose: ").append(Config.getInstance().isEnableVerbose() ? "ENABLED" : "DISABLED").append(System.lineSeparator())
                .append("  Cache Eviction Policy: ").append(Config.getInstance().getCacheEvictionPolicy().toString()).append(System.lineSeparator())
                .append("  Cache Size: ").append(Config.getInstance().getCacheSize()).append(System.lineSeparator())
                .append("  Config file set: ").append(Config.getInstance().isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!Config.getInstance().isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(Config.getInstance().getConfigFilePath() != null ? Config.getInstance().getConfigFilePath() : "n/a").append(System.lineSeparator());
        return sb.toString();
    }

    public static String getMemoryStatusSummary() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() / (1024 * 1024);
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        StringBuilder sb = new StringBuilder();
        sb
         .append("  Memory Status: ").append(System.lineSeparator())
         .append("    Used Memory: ").append(usedMem).append(" MB").append(System.lineSeparator())
         .append("    Max Memory: ").append(maxMem).append(" MB").append(System.lineSeparator());
        return sb.toString();
    }
}
