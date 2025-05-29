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
package com.vertexcache.console.domain.terminal;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.common.security.MessageCodec;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.console.domain.config.Config;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerNoVerification;
import com.vertexcache.common.security.CertificateTrustManager.ServerCertificateTrustManagerVerification;
import com.vertexcache.common.security.GcmCryptoHelper;

import javax.crypto.Cipher;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Scanner;

/**
 * Console CLI Terminal Client that interacts with VertexCache, key difference between this and the SDKs
 * is that is display the raw VCMP protocol responses.  This is helpful for debugging and/or developing
 * your TCP Client.
 */
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

            // Send IDENT with client_id and token
            String clientId = config.getClientId() != null ? config.getClientId() : "";
            String token = config.getClientToken() != null ? config.getClientToken() : "";
            String identPayload = String.format("{\"client_id\":\"%s\", \"token\":\"%s\"}", clientId, token);
            String identMessage = "IDENT " + identPayload;

            byte[] identBytes = encryptPayload(identMessage.getBytes(StandardCharsets.UTF_8));
            MessageCodec.writeFramedMessage(outputStream, identBytes);

            // Read IDENT response
            byte[] identResponse = MessageCodec.readFramedMessage(inputStream);
            if (identResponse != null) {
                String identReply = new String(identResponse, StandardCharsets.UTF_8);
                LogHelper.getInstance().logInfo(identReply);
            }

            this.outputStartupOK();

            while (true) {
                System.out.print(ConsoleTerminal.consolePrompt);

                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase(CMD_EXIT) || userInput.equalsIgnoreCase(CMD_QUIT)) {
                    System.out.println(DISPLAY_EXIT);
                    break;
                }

                byte[] encryptedCommand = encryptPayload(userInput.getBytes(StandardCharsets.UTF_8));
                MessageCodec.writeFramedMessage(outputStream, encryptedCommand);

                byte[] responseBytes = MessageCodec.readFramedMessage(inputStream);
                if (responseBytes != null) {
                    String receivedMessage = new String(responseBytes);
                    LogHelper.getInstance().logInfo(receivedMessage);
                } else {
                    LogHelper.getInstance().logInfo(DISPLAY_NO_RESPONSE);
                }
            }
        } catch (Exception e) {
            outputStartUpError("Error, unexpected error", e);
        } finally {
            scanner.close();
        }
    }

    private byte[] encryptPayload(byte[] plainText) throws Exception {
        if (config.getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, config.getPublicKey());
            return cipher.doFinal(plainText);
        } else if (config.getEncryptionMode() == EncryptionMode.SYMMETRIC) {
            byte[] keyBytes = Base64.getDecoder().decode(config.getSharedEncryptionKey());
            return GcmCryptoHelper.encrypt(plainText, keyBytes);
        } else {
            return plainText;
        }
    }

    private SSLSocket buildSecureSocket() throws NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        SSLContext sslContext = SSLContext.getInstance(SOCKET_PROTOCOL);

        if (config.isVerifyTLSCertificate()) {
            sslContext.init(null, new X509TrustManager[]{new ServerCertificateTrustManagerVerification(config.getTlsCertificate())}, null);
        } else {
            sslContext.init(null, new TrustManager[]{new ServerCertificateTrustManagerNoVerification()}, null);
        }

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(config.getServerHost(), config.getServerPort());
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
                .append("  Encryption: ").append(System.lineSeparator())
                .append("    TLS Enabled (Transport): ").append(config.isEncryptTransport() ? "Yes" : "No").append(System.lineSeparator())
                .append("    Message Layer Encrypted: ").append(config.getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No").append(config.getEncryptNote()).append(System.lineSeparator())
                .append("      Private/Public Key (RSA) Enabled: ").append(config.isEncryptWithPublicKey() ? "Yes" : "No").append(System.lineSeparator())
                .append("      Shared Key (AES) Enabled: ").append(config.isEncryptWithSharedKey() ? "Yes" : "No").append(System.lineSeparator())
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
