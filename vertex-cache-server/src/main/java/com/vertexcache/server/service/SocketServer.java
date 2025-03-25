package com.vertexcache.server.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.KeyPairHelper;
import com.vertexcache.common.util.PemUtils;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.server.domain.cache.Cache;
import com.vertexcache.server.domain.config.Config;
import com.vertexcache.server.exception.VertexCacheSSLServerSocketException;
import com.vertexcache.server.domain.command.CommandService;

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
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class SocketServer {

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

            Cache.getInstance(config.getCacheEvictionPolicy(),config.getCacheSize());

            ServerSocket serverSocket;
            if (config.isEncryptTransport()) {
                serverSocket = secureSocket();
            } else {
                serverSocket = new ServerSocket(config.getServerPort());
            }

            outputStartupOK();
            this.executor = Executors.newCachedThreadPool(); // maybe use fixed-size thread pool instead?

            while (true) {
                Socket clientSocket = serverSocket.accept();
                outputInfo("Client Connection initiated, " + clientSocket);
                this.executor.execute(new ClientHandler(clientSocket, config, commandService));
            }
        } catch (BindException e) {
            outputStartUpError("Error, Port already in use", e);
        } catch (IOException e) {
            outputStartUpError("Error, unexpected error, please try again.", e);
        } catch (VertexCacheSSLServerSocketException e) {
            outputStartUpError("Error with Transport Layer Encryption configuration, please check your properties file that the correct keystore location and keystore password is correct; ensure the keystore file is not corrupt.", e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                outputStartUpError("Error, unexpected error and unable to shutdown server.", e);
            }
        }
    }

    public void shutdown() {
        try {
            if(this.executor != null) {
                this.executor.shutdown();
            }
            if(this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException exception) {
            LogHelper.getInstance().logError(exception.getMessage());
        }
    }


    /**
     * Config SSLServerSocket, does require valid keystore file and associated password
     *
     *  Generate:
     *    keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -validity 365 -keystore server_keystore.jks
     *
     *  Set keystore information in properties file, see vertex-cache-server.properties
     *
     * @return
     * @throws VertexCacheSSLServerSocketException
     */

    private SSLServerSocket secureSocket() throws VertexCacheSSLServerSocketException {
        try {
            String certPem = this.config.getTlsCertificate();
            String keyPem = this.config.getTlsPrivateKey();

            // Load certificate
            X509Certificate certificate = loadCertificate(certPem);

            // Load private key
            PrivateKey privateKey = loadPrivateKey(keyPem);

            // Load into in-memory keystore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setKeyEntry("server", privateKey, "changeit".toCharArray(), new X509Certificate[]{certificate});

            // Init managers
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, "changeit".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            // SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Server socket
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


    /*
    private SSLServerSocket secureSocket___Orig() throws VertexCacheSSLServerSocketException {

        try {
            // Load the keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(this.config.getKeystoreFilePath()), this.config.getKeystorePassword().toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keystore, this.config.getKeystorePassword().toCharArray());

            // Set up the trust manager factory to use our key store
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keystore);

            // Create SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create SSL socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create SSL server socket
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(config.getServerPort());

            // Set up the enabled protocols and cipher suites (optional)
            String[] enabledProtocols = {"TLSv1.2"};
            serverSocket.setEnabledProtocols(enabledProtocols);

            // Set up the enabled cipher suites (optional)
            String[] enabledCipherSuites = {"TLS_RSA_WITH_AES_256_CBC_SHA256"};
            serverSocket.setEnabledCipherSuites(enabledCipherSuites);

            return serverSocket;

        } catch (UnrecoverableKeyException | CertificateException | IOException | NoSuchAlgorithmException |
                 KeyManagementException | KeyStoreException e) {
            throw new VertexCacheSSLServerSocketException(e);
        }
    }

*/


    private void outputStartupOK() {
        this.outputStartup("Server Started");
    }

    /**
     * On startup output, regardless of verbose enabled or disabled
     */
    private void outputStartup(String message) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append(this.config.getAppName()).append(":").append(System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Port: ").append(config.getServerPort()).append(System.lineSeparator())
                .append("  Verbose: ").append(config.isEnableVerbose() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Cache Eviction Policy: ").append(config.getCacheEvictionPolicy().toString()).append(System.lineSeparator())
                .append("  Cache Size (only applies when eviction is not NONE): ").append(config.getCacheSize()).append(System.lineSeparator())
                .append("  Transport Layer Encryption Enabled: ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Message Layer Encryption Enabled: ").append(config.isEncryptMessage() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file set: ").append(config.isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!config.isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a").append(System.lineSeparator())
                .append("  Status: ").append(ANSI_GREEN).append(message).append(ANSI_RESET).append(System.lineSeparator()) ;
        LogHelper.getInstance().logInfo(stringBuilder.toString());
    }

    /**
     * On startup output with error, regardless of verbose enabled or disabled
     */
    private void outputStartUpError(String message, Exception exception) {
        this.outputStartup(message);
        LogHelper.getInstance().logError(exception.getMessage());
    }

    /**
     * General Output, only if Verbose is enabled (set to true)
     *
     * Additional relies on Log4j2 config
     */
    private void outputInfo(String message) {
        if(this.config.isEnableVerbose()) {
            LogHelper.getInstance().logInfo(message);
        }
    }

    private X509Certificate loadCertificate(String input) throws Exception {
        String content = loadPemContent(input); // already handles newline conversion

        try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(stream);
        }
    }


    private PrivateKey loadPrivateKey(String input) throws Exception {
        String content = loadPemContent(input); // already handles newline conversion

        // Sanity check: validate header
        if (!content.contains("-----BEGIN PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Invalid private key: missing BEGIN header");
        }

        // Strip headers and collapse to Base64
        String base64 = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(base64);

        // Generate private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }


    private String loadPemContent(String input) throws IOException {
        Path path = Paths.get(input);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8).trim();
        } else {
            // Convert escaped `\n` to real newlines for inline .env values
            return input.replace("\\n", "\n").trim();
        }
    }



}
