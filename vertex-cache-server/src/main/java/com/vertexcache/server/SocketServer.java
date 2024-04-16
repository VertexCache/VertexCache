package com.vertexcache.server;

import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogUtil;
import com.vertexcache.domain.cache.Cache;
import com.vertexcache.domain.config.Config;
import com.vertexcache.exception.VertexCacheSSLServerSocketException;
import com.vertexcache.domain.command.CommandService;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketServer {

    private static final LogUtil logger = new LogUtil(SocketServer.class);

    private ServerSocket serverSocket = null;
    private Config config;

    public SocketServer() {
        this.config = Config.getInstance();
    }

    /*
    public SocketServer(String[] args) throws Exception {

        this.config = Config.getInstance();
        this.config.loadPropertiesFromArgs(new CommandLineArgsParser(args));
    }

     */

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
            ExecutorService executor = Executors.newCachedThreadPool(); // maybe use fixed-size thread pool instead?

            while (true) {
                Socket clientSocket = serverSocket.accept();
                outputInfo("Connection: " + clientSocket);
                executor.execute(new ClientHandler(clientSocket, config, commandService));
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

    private void outputStartupOK() {
        this.outputStartup("OK, Server Started");
    }

    private void outputStartup(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(this.config.getAppName()).append(":").append(System.lineSeparator())
                .append("  PORT: ").append(config.getServerPort()).append(System.lineSeparator())
                .append("  Cache Eviction Policy: ").append(config.getCacheEvictionPolicy().toString()).append(System.lineSeparator())
                .append("  Cache Size (only applies when eviction is not NONE): ").append(config.getCacheSize()).append(System.lineSeparator())
                .append("  Transport Layer Encryption Enabled: ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Message Layer Encryption Enabled: ").append(config.isEncryptMessage() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file set: ").append(config.isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!config.isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a").append(System.lineSeparator())
                .append("  Log4j2 config file loaded with no errors: ").append(!config.isLogLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Log4j2 config file location: ").append(config.getLogFilePath() != null ? config.getLogFilePath() : "n/a").append(System.lineSeparator())
                .append("Status: ").append(message).append(System.lineSeparator()) ;

        // Relies on Log4j2 config
        logger.info(stringBuilder.toString());
    }

    private void outputStartUpError(String message, Exception exception) {
        this.outputStartup(message);
        logger.error(exception.getMessage());
    }

    // Relies on Log4j2 config
    private void outputInfo(String message) {
        logger.info(message);
    }
}
