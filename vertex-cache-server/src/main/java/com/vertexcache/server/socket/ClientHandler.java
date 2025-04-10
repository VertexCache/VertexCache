package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.protocol.MessageCodec;
import com.vertexcache.common.security.GcmCryptoHelper;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.setting.Config;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Config config;
    private final CommandService commandProcessor;
    private String clientName = null;

    public ClientHandler(Socket clientSocket, Config config, CommandService commandProcessor) {
        this.clientSocket = clientSocket;
        this.config = config;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            Cipher rsaCipher = config.getEncryptionMode() == EncryptionMode.ASYMMETRIC
                    ? Cipher.getInstance("RSA/ECB/PKCS1Padding")
                    : null;

            byte[] aesKeyBytes = null;
            if (config.getEncryptionMode() == EncryptionMode.SYMMETRIC) {
                aesKeyBytes = GcmCryptoHelper.decodeBase64Key(config.getSharedEncryptionKey());
            }

            while (true) {
                byte[] framedRequest = MessageCodec.readFramedMessage(inputStream);
                if (framedRequest == null) break;

                byte[] processedData = processInputData(framedRequest, rsaCipher, aesKeyBytes);
                MessageCodec.writeFramedMessage(outputStream, processedData);
            }

        } catch (Exception e) {
            LogHelper.getInstance().logFatal("Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal("Socket close error: " + e.getMessage());
            }
        }
    }

    private byte[] processInputData(byte[] data, Cipher rsaCipher, byte[] aesKeyBytes) throws Exception {
        byte[] response;

        // Handle encryption based on mode
        if (config.getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
            rsaCipher.init(Cipher.DECRYPT_MODE, config.getPrivateKey());
            data = rsaCipher.doFinal(data);
        } else if (config.getEncryptionMode() == EncryptionMode.SYMMETRIC) {
            data = GcmCryptoHelper.decrypt(data, aesKeyBytes);
        }

        String input = new String(data, StandardCharsets.UTF_8).trim();
        String logTag = "[client:" + (clientName != null ? clientName : clientSocket.getRemoteSocketAddress()) + "]";

        if (input.startsWith("IDENT ")) {
            this.clientName = input.substring(6).trim();
            response = ("+OK identified as " + this.clientName).getBytes(StandardCharsets.UTF_8);
            if (config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo(logTag + " IDENT received: " + this.clientName);
            }
        } else {
            response = commandProcessor.execute(data);
            if (config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo(logTag + " Request: " + input);
                LogHelper.getInstance().logInfo(logTag + " Response: " + new String(response, StandardCharsets.UTF_8));
            }
        }

        return response;
    }
}

