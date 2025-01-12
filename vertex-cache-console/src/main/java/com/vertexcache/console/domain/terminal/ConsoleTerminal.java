package com.vertexcache.console.domain.terminal;

import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.console.domain.config.Config;
import com.vertexcache.common.log.LogUtil;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerNoVerification;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerVerification;
import com.vertexcache.common.security.KeyPairHelper;

import javax.crypto.Cipher;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class ConsoleTerminal {

    private static final LogUtil logger = new LogUtil(ConsoleTerminal.class);

    private static final String CMD_EXIT = "exit";
    private static final String CMD_QUIT = "quit";

    private static final String DISPLAY_EXIT = "Existing...";
    private static final String DISPLAY_NO_RESPONSE = "No response received.";

    private static final String CIPHER_RSA = "RSA";
    private static final String SOCKET_PROTOCOL = "TLS";

    private Config config;

    private static String consolePrompt;

    public ConsoleTerminal() {
        this.config = Config.getInstance();
        ConsoleTerminal.consolePrompt = Config.APP_NAME + ", " + this.config.getServerHost() + ":" + this.config.getServerPort() + "> ";
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(Config.APP_WELCOME);
        try {
            OutputStream outputStream = null;
            InputStream inputStream = null;

            if(config.isEncryptTransport()) {
                SSLSocket socket = buildSecureSocket();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } else {
                Socket socket = new Socket(this.config.getServerHost(), this.config.getServerPort());
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            }

            this.outputStartupOK();

            // Start the main loop
            while (true) {
                System.out.print(ConsoleTerminal.consolePrompt);

                // Read the user's input
                String userInput = scanner.nextLine();

                // Check for exit command
                if (userInput.equalsIgnoreCase(ConsoleTerminal.CMD_EXIT) || userInput.equalsIgnoreCase(ConsoleTerminal.CMD_QUIT)) {
                    System.out.println(ConsoleTerminal.DISPLAY_EXIT);
                    break;
                }

                logger.info("Request: " + userInput);

                byte[] bytesToSend;
                if (config.isEncryptMessage()) {
                    // Encrypt the user's input
                    Cipher cipher = Cipher.getInstance(ConsoleTerminal.CIPHER_RSA);
                    cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(KeyPairHelper.publicKeyToString(this.config.getPublicKey())));
                    bytesToSend = cipher.doFinal(userInput.getBytes());
                } else {
                    bytesToSend = userInput.getBytes();
                }

                // Send data to the server
                outputStream.write(bytesToSend);
                outputStream.flush();

                // Read the response from the server and display it
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println(receivedMessage);
                    logger.info("Response: " + receivedMessage);
                } else {
                    System.out.println(ConsoleTerminal.DISPLAY_NO_RESPONSE);
                    logger.info("Response: " + ConsoleTerminal.DISPLAY_NO_RESPONSE);
                }
            }
        } catch (Exception e) {
            outputStartUpError("Error, unexpected error", e);
        } finally {
            scanner.close();
        }
    }

    private SSLSocket buildSecureSocket() throws NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        SSLContext sslContext = SSLContext.getInstance(ConsoleTerminal.SOCKET_PROTOCOL);

        if(config.isVerifyServerCertificate()) {
            sslContext.init(null, new X509TrustManager[]{new ServerCertificateTrustManagerVerification(config.getServerCertificatePath())}, null);
        } else {
            sslContext.init(null, new TrustManager[]{new ServerCertificateTrustManagerNoVerification()}, null);
        }

        // Create SSL socket factory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Create SSL socket
        SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(this.config.getServerHost(), this.config.getServerPort());

        // Perform SSL handshake
        socket.startHandshake();

        return socket;
    }

    private void outputStartupOK() {
        this.outputStartup("OK, Console Client Started");
    }

    private void outputStartup(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(Config.APP_NAME).append(":").append(System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Host: ").append(config.getServerHost()).append(System.lineSeparator())
                .append("  Port: ").append(config.getServerPort()).append(System.lineSeparator())
                .append("  Message Layer Encryption Enabled: ").append(config.isEncryptMessage() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Transport Layer Encryption Enabled: ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Transport Layer Verify Certificate: ").append(config.isVerifyServerCertificate() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file set: ").append(config.isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!config.isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a").append(System.lineSeparator())
                .append("  Log4j2 config file loaded with no errors: ").append(config.isLogLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Log4j2 config file location: ").append(config.getLogFilePath() != null ? config.getLogFilePath() : "n/a").append(System.lineSeparator())
                .append("Status: ").append(message).append(System.lineSeparator()) ;

        logger.info(stringBuilder.toString());
    }

    private void outputStartUpError(String message, Exception exception) {
        this.outputStartup(message);
        logger.error(exception.getMessage());
    }

}
