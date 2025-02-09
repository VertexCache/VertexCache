package com.vertexcache.server.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.server.domain.config.Config;
import com.vertexcache.server.domain.command.CommandService;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private Config config;
    private CommandService commandProcessor;

    public ClientHandler(Socket clientSocket, Config config, CommandService commandProcessor) {
        this.clientSocket = clientSocket;
        this.config = config;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        try (InputStream inputStream = clientSocket.getInputStream(); OutputStream outputStream = clientSocket.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            Cipher cipher = config.isEncryptMessage() ? Cipher.getInstance("RSA/ECB/PKCS1Padding") : null;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] processedData = processInputData(buffer, bytesRead, cipher);
                outputStream.write(processedData);
            }

        } catch (IOException e) {
            // Log or handle the exception appropriately
            LogHelper.getInstance().logFatal(e.getMessage());
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            LogHelper.getInstance().logFatal(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Log or handle the exception appropriately
                LogHelper.getInstance().logFatal(e.getMessage());
            }
        }
    }

    private byte[] processInputData(byte[] buffer, int bytesRead, Cipher cipher) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        if(this.config.isEncryptMessage()) {
            cipher.init(Cipher.PRIVATE_KEY, this.config.getPrivateKey());
            byte[] decryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
            String decryptedMessage = new String(decryptedBytes);
            if(this.config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo("Request: " + decryptedMessage);
            }
            byte[] response = commandProcessor.execute(decryptedBytes);
            if(this.config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo("Response: " + new String(response));
            }
            return response;
        } else {
            byte[] unencryptedData = new byte[bytesRead];
            if(this.config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo("Request: " + new String(buffer, 0, bytesRead));
            }
            System.arraycopy(buffer, 0, unencryptedData, 0, bytesRead);
            byte[] response = commandProcessor.execute(unencryptedData);
            if(this.config.isEnableVerbose()) {
                LogHelper.getInstance().logInfo("Response: " + new String(response));
            }
            return response;
        }
    }
}
