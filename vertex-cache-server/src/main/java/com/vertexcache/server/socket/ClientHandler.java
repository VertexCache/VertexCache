package com.vertexcache.server.socket;

import com.google.gson.Gson;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.protocol.MessageCodec;
import com.vertexcache.common.security.GcmCryptoHelper;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.*;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.server.session.IdentPayload;
import com.vertexcache.server.session.SessionRegistry;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private static final long DEFAULT_IDLE_TIMEOUT_MS = 30_000;
    private static final long ADMIN_IDLE_TIMEOUT_MS = 300_000;

    private final Socket clientSocket;
    private final Config config;
    private final CommandService commandProcessor;

    private final ClientSessionContext session = new ClientSessionContext();
    private boolean isIdentified = false;
    private String clientName = null;
    private String connectionId;

    public ClientHandler(Socket clientSocket, Config config, CommandService commandProcessor) {
        this.clientSocket = clientSocket;
        this.config = config;
        this.commandProcessor = commandProcessor;
        this.connectionId = clientSocket.getRemoteSocketAddress().toString();
    }

    @Override
    public void run() {
        long lastActivityTime = System.currentTimeMillis();
        long maxIdle = DEFAULT_IDLE_TIMEOUT_MS;

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            Cipher rsaCipher = config.getConfigSecurity().getEncryptionMode() == EncryptionMode.ASYMMETRIC
                    ? Cipher.getInstance("RSA/ECB/PKCS1Padding")
                    : null;

            byte[] aesKeyBytes = null;
            if (config.getConfigSecurity().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
                aesKeyBytes = GcmCryptoHelper.decodeBase64Key(config.getConfigSecurity().getSharedEncryptionKey());
            }

            while (true) {
                if ((System.currentTimeMillis() - lastActivityTime) > maxIdle) {
                    LogHelper.getInstance().logInfo("Idle timeout: " + clientSocket.getRemoteSocketAddress());
                    break;
                }

                clientSocket.setSoTimeout(1000); // short poll window
                byte[] framedRequest;

                try {
                    framedRequest = MessageCodec.readFramedMessage(inputStream);
                    if (framedRequest == null) break;
                    lastActivityTime = System.currentTimeMillis();
                } catch (IOException e) {
                    continue; // likely timeout, re-check idle
                }

                // Check for ADMIN role and extend idle timeout
                if (isIdentified && session.getRole() == Role.ADMIN) {
                    maxIdle = ADMIN_IDLE_TIMEOUT_MS;
                }

                byte[] response = processInputData(framedRequest, rsaCipher, aesKeyBytes);
                MessageCodec.writeFramedMessage(outputStream, response);
            }

        } catch (Exception e) {
            LogHelper.getInstance().logFatal("Client error: " + e.getMessage());
        } finally {
            SessionRegistry.unregister(connectionId);
            try {
                clientSocket.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal("Socket close error: " + e.getMessage());
            }
        }
    }

    private byte[] processInputData(byte[] data, Cipher rsaCipher, byte[] aesKeyBytes) throws Exception {
        byte[] decrypted;

        if (config.getConfigSecurity().getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
            rsaCipher.init(Cipher.DECRYPT_MODE, config.getConfigSecurity().getPrivateKey());
            decrypted = rsaCipher.doFinal(data);
        } else if (config.getConfigSecurity().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
            decrypted = GcmCryptoHelper.decrypt(data, aesKeyBytes);
        } else {
            decrypted = data;
        }

        String input = new String(decrypted, StandardCharsets.UTF_8).trim();
        String logTag = "[client:" + (clientName != null ? clientName : clientSocket.getRemoteSocketAddress()) + "]";

        if (input.startsWith("IDENT ")) {
            String payload = input.substring(6).trim();

            if (payload.startsWith("{")) {
                IdentPayload ident = new Gson().fromJson(payload, IdentPayload.class);
                String clientId = ident.client_id != null ? ident.client_id.trim() : "";
                String token = ident.token != null ? ident.token.trim() : "";

                if (clientId.isEmpty()) {
                    return "-ERR IDENT Failed: missing client_id".getBytes(StandardCharsets.UTF_8);
                }

                if (config.isAuthEnabled()) {
                    Optional<AuthService> optAuthService = AuthModuleHelper.getAuthService();
                    if (optAuthService.isEmpty()) {
                        return "-ERR IDENT Failed: Auth module not available".getBytes(StandardCharsets.UTF_8);
                    }

                    AuthService authService = optAuthService.get();
                    Optional<AuthEntry> result = authService.authenticate(clientId, token);

                    if (result.isEmpty()) {
                        return "-ERR IDENT Failed: invalid token or unknown client".getBytes(StandardCharsets.UTF_8);
                    }

                    AuthEntry entry = result.get();
                    session.setClientId(entry.getClientId());
                    session.setTenantId(entry.getTenantId());
                    session.setRole(entry.getRole());

                    this.clientName = clientId;
                    this.isIdentified = true;
                    SessionRegistry.register(connectionId, session);

                    return "+OK IDENT successful".getBytes(StandardCharsets.UTF_8);
                } else {
                    session.setClientId(clientId);
                    session.setTenantId(TenantId.DEFAULT);
                    session.setRole(Role.ADMIN);

                    this.clientName = clientId;
                    this.isIdentified = true;
                    SessionRegistry.register(connectionId, session);

                    return "+OK IDENT (auth disabled)".getBytes(StandardCharsets.UTF_8);
                }

            } else {
                this.clientName = payload;
                this.isIdentified = true;
                SessionRegistry.register(connectionId, session);
                return ("+OK IDENT (legacy): " + this.clientName).getBytes(StandardCharsets.UTF_8);
            }
        }

        if (!isIdentified && config.isAuthEnabled()) {
            return "-ERR Unauthorized: IDENT required".getBytes(StandardCharsets.UTF_8);
        }

        if (config.isEnableVerbose()) {
            LogHelper.getInstance().logInfo(logTag + " Request: " + input);
        }

        byte[] response = commandProcessor.execute(decrypted, this.session);

        if (config.isEnableVerbose()) {
            LogHelper.getInstance().logInfo(logTag + " Response: " + new String(response, StandardCharsets.UTF_8));
        }

        return response;
    }
}
