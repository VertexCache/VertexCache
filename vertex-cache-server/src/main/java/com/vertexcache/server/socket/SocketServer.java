package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.module.ModuleStatus;
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

    private static ModuleStatus status = ModuleStatus.NOT_STARTED;


    public SocketServer() {

    }

    public void execute() throws Exception {
        try {
            status = ModuleStatus.STARTUP_IN_PROGRESS;

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

    public static ModuleStatus getStartupStatus() {
        return SocketServer.status;
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
        status = ModuleStatus.STARTUP_SUCCESSFUL;
        this.outputStartup();
    }

    private void outputStartup() {
        LogHelper.getInstance().logInfo(SystemStatusReport.getFullSystemReport());
    }

    private void outputStartUpError(String message, Exception exception) {
        status = ModuleStatus.STARTUP_FAILED;
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

}
