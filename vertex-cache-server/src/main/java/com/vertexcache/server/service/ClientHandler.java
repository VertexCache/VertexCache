package com.vertexcache.server.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.MessageCodec;
import com.vertexcache.server.domain.command.CommandService;
import com.vertexcache.server.domain.config.Config;

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

            Cipher cipher = config.isEncryptMessage() ? Cipher.getInstance("RSA/ECB/PKCS1Padding") : null;

            while (true) {
                byte[] framedRequest = MessageCodec.readFramedMessage(inputStream);
                if (framedRequest == null) break;

                byte[] processedData = processInputData(framedRequest, cipher);
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

    private byte[] processInputData(byte[] data, Cipher cipher) throws Exception {
        byte[] response;

        if (config.isEncryptMessage()) {
            cipher.init(Cipher.DECRYPT_MODE, config.getPrivateKey());
            data = cipher.doFinal(data);
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
