package com.vertexcache.console.domain.terminal;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.MessageCodec;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.console.domain.config.Config;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerNoVerification;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerVerification;
import com.vertexcache.common.security.KeyPairHelper;

import javax.crypto.Cipher;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class ConsoleTerminal {

    private static final String CMD_EXIT = "exit";
    private static final String CMD_QUIT = "quit";

    private static final String DISPLAY_EXIT = "VertexCache Console Terminated...";
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
        try {
            OutputStream outputStream;
            InputStream inputStream;

            if (config.isEncryptTransport()) {
                SSLSocket socket = buildSecureSocket();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } else {
                Socket socket = new Socket(this.config.getServerHost(), this.config.getServerPort());
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            }

            // Send IDENT on connect
            String clientId = config.getClientId() != null ? config.getClientId() : "console-client";
            String ident = "IDENT " + clientId;
            byte[] identBytes;
            if (config.isEncryptMessage()) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(KeyPairHelper.publicKeyToString(this.config.getPublicKey())));
                identBytes = cipher.doFinal(ident.getBytes());
            } else {
                identBytes = ident.getBytes();
            }
            MessageCodec.writeFramedMessage(outputStream, identBytes);

            // 🔧 Immediately read IDENT response to avoid leaking it into user commands
            byte[] identResponse = MessageCodec.readFramedMessage(inputStream);
            if (identResponse != null) {
                String identReply = new String(identResponse, StandardCharsets.UTF_8);
                LogHelper.getInstance().logInfo(identReply);
            }

            this.outputStartupOK();

            while (true) {
                System.out.print(ConsoleTerminal.consolePrompt);

                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase(ConsoleTerminal.CMD_EXIT) || userInput.equalsIgnoreCase(ConsoleTerminal.CMD_QUIT)) {
                    System.out.println(ConsoleTerminal.DISPLAY_EXIT);
                    break;
                }

                byte[] bytesToSend;
                if (config.isEncryptMessage()) {
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, KeyPairHelper.decodePublicKey(KeyPairHelper.publicKeyToString(this.config.getPublicKey())));
                    bytesToSend = cipher.doFinal(userInput.getBytes());
                } else {
                    bytesToSend = userInput.getBytes();
                }

                MessageCodec.writeFramedMessage(outputStream, bytesToSend);

                byte[] responseBytes = MessageCodec.readFramedMessage(inputStream);
                if (responseBytes != null) {
                    String receivedMessage = new String(responseBytes);
                    LogHelper.getInstance().logInfo(receivedMessage);
                } else {
                    LogHelper.getInstance().logInfo(ConsoleTerminal.DISPLAY_NO_RESPONSE);
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

        if (config.isVerifyTLSCertificate()) {
            sslContext.init(null, new X509TrustManager[]{new ServerCertificateTrustManagerVerification(config.getTlsCertificate())}, null);
        } else {
            sslContext.init(null, new TrustManager[]{new ServerCertificateTrustManagerNoVerification()}, null);
        }

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(this.config.getServerHost(), this.config.getServerPort());
        socket.startHandshake();

        return socket;
    }

    private void outputStartupOK() {
        this.outputStartup("OK, Console Client Started");
    }

    private void outputStartup(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append(Config.APP_NAME).append(":" + System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Host: ").append(config.getServerHost()).append(System.lineSeparator())
                .append("  Port: ").append(config.getServerPort()).append(System.lineSeparator())
                .append("  Message Layer Encryption Enabled: ").append(config.isEncryptMessage() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Transport Layer Encryption Enabled: ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Transport Layer Verify Certificate: ").append(config.isVerifyTLSCertificate() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file set: ").append(config.isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!config.isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a").append(System.lineSeparator())
                .append("Status: ").append(message).append(System.lineSeparator());

        LogHelper.getInstance().logInfo(stringBuilder.toString());
    }

    private void outputStartUpError(String message, Exception exception) {
        this.outputStartup(message);
        LogHelper.getInstance().logError(exception.getMessage());
    }
}
