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
package com.vertexcache.server.socket;

import com.google.gson.Gson;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.common.security.MessageCodec;
import com.vertexcache.common.security.GcmCryptoHelper;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.IdentValidator;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.model.Role;
import com.vertexcache.module.auth.model.TenantId;
import com.vertexcache.module.auth.service.AuthService;
import com.vertexcache.module.auth.util.AuthModuleHelper;
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

/**
 * Handles individual client connections to the VertexCache server.
 *
 * Manages the client socket lifecycle, including reading framed messages,
 * decrypting input based on configured encryption mode, authenticating clients,
 * processing commands, and sending responses.
 *
 * Supports idle timeouts, with extended timeout for ADMIN role clients.
 * Tracks session context and registers/unregisters sessions in SessionRegistry.
 *
 * Processes IDENT commands for client identification and authentication,
 * including legacy and JSON payload formats. Enforces authentication if enabled.
 *
 * Logs requests and responses when verbose mode is enabled.
 * Handles exceptions and ensures proper resource cleanup on disconnect.
 */
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

            Cipher rsaCipher = config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.ASYMMETRIC
                    ? Cipher.getInstance("RSA/ECB/PKCS1Padding")
                    : null;

            byte[] aesKeyBytes = null;
            if (config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
                aesKeyBytes = GcmCryptoHelper.decodeBase64Key(config.getSecurityConfigLoader().getSharedEncryptionKey());
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

        if (config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
            rsaCipher.init(Cipher.DECRYPT_MODE, config.getSecurityConfigLoader().getPrivateKey());
            decrypted = rsaCipher.doFinal(data);
        } else if (config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
            decrypted = GcmCryptoHelper.decrypt(data, aesKeyBytes);
        } else {
            decrypted = data;
        }

        String input = new String(decrypted, StandardCharsets.UTF_8).trim();
        String logTag = "[client:" + (clientName != null ? clientName : clientSocket.getRemoteSocketAddress()) + "]";

        if(Config.getInstance().getClusterConfigLoader().isSecondaryNode() && !Config.getInstance().getClusterConfigLoader().getSecondaryEnabledClusterNode().isPromotedToPrimary()) {
            return "-ERR Access denied: Secondary node is still in standby mode and has not been promoted to primary.".getBytes(StandardCharsets.UTF_8);
        }

        if (input.startsWith("IDENT ")) {
            String payload = input.substring(6).trim();

            if (payload.startsWith("{")) {
                IdentPayload ident = new Gson().fromJson(payload, IdentPayload.class);
                String clientId = ident.client_id != null ? ident.client_id.trim() : "";
                String token = ident.token != null ? ident.token.trim() : "";

                if (clientId.isEmpty()) {
                    return "-ERR IDENT Failed: missing client_id".getBytes(StandardCharsets.UTF_8);
                }

                try {
                    new IdentValidator(clientId).validate();
                } catch (VertexCacheValidationException e) {
                    return ("-ERR IDENT Failed: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
                }

                if (config.getAuthWithTenantConfigLoader().isAuthEnabled()) {
                    Optional<AuthService> optAuthService = AuthModuleHelper.getAuthService();
                    if (optAuthService.isEmpty()) {
                        return "-ERR IDENT Failed: Auth module not available".getBytes(StandardCharsets.UTF_8);
                    }

                    AuthEntry authEntry = AuthService.getInstance().authenticate(clientId, token)
                            .orElse(null);

                    boolean isClusterNode = Config.getInstance().getClusterConfigLoader().getAllClusterNodes().containsKey(clientId);

                    if (authEntry == null && !isClusterNode) {
                        return "-ERR IDENT Failed: invalid token or unknown client".getBytes(StandardCharsets.UTF_8);
                    }

                    if (isClusterNode) {
                        session.setClientId(clientId);
                        session.setTenantId(TenantId.DEFAULT);
                        session.setRole(Role.NODE);
                    } else {

                        session.setClientId(authEntry.getClientId());
                        session.setTenantId(authEntry.getTenantId());
                        session.setRole(authEntry.getRole());

                    }
                    this.clientName = clientId;
                    this.isIdentified = true;
                    SessionRegistry.register(connectionId, session);

                    return "+OK IDENT successful".getBytes(StandardCharsets.UTF_8);
                } else {
                    session.setClientId(clientId);
                    session.setTenantId(TenantId.DEFAULT);

                    boolean isClusterNode = Config.getInstance().getClusterConfigLoader().getAllClusterNodes().containsKey(clientId);

                    if (isClusterNode) {
                        session.setRole(Role.NODE);
                    } else {
                        session.setRole(Role.ADMIN);
                    }

                    this.clientName = clientId;
                    this.isIdentified = true;
                    SessionRegistry.register(connectionId, session);

                    return ("+OK IDENT (auth disabled, role=" + session.getRole() + ")").getBytes(StandardCharsets.UTF_8);
                }

            } else {
                this.clientName = payload;
                this.isIdentified = true;
                SessionRegistry.register(connectionId, session);
                return ("+OK IDENT (legacy): " + this.clientName).getBytes(StandardCharsets.UTF_8);
            }
        }

        if (!isIdentified && config.getAuthWithTenantConfigLoader().isAuthEnabled()) {
            return "-ERR Unauthorized: IDENT required".getBytes(StandardCharsets.UTF_8);
        }

        if (config.getCoreConfigLoader().isEnableVerbose()) {
            LogHelper.getInstance().logInfo(logTag + " Request: " + input);
        }

        byte[] response = commandProcessor.execute(decrypted, this.session);

        if (config.getCoreConfigLoader().isEnableVerbose()) {
            LogHelper.getInstance().logInfo(logTag + " Response: " + new String(response, StandardCharsets.UTF_8));
        }

        return response;
    }
}
