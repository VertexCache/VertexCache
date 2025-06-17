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
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Handles the lifecycle of an individual client connection to the VertexCache server.
 *
 * Responsibilities include:
 * - Reading framed messages using the VertexCache binary protocol.
 * - Handling decryption using RSA or AES-GCM based on the configured encryption mode.
 * - Processing IDENT commands to authenticate and assign client roles (ADMIN, NODE, etc.).
 * - Executing cache commands through the central CommandService.
 * - Managing idle timeouts, with extended timeout support for ADMIN clients.
 * - Tracking authenticated session context and registering it with the SessionRegistry.
 * - Logging client interactions in verbose mode for debugging or auditing.
 *
 * This handler supports both secure (encrypted) and unencrypted client communication,
 * and gracefully handles disconnections, socket timeouts, and malformed input.
 *
 * It is instantiated per socket and run as a separate thread for each client connection.
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

    private Cipher rsaCipher;

    private String aesTransformation;
    private byte[] aesKeyBytes = null;

    public ClientHandler(Socket clientSocket, Config config, CommandService commandProcessor) {
        this.clientSocket = clientSocket;
        this.config = config;
        this.commandProcessor = commandProcessor;
        this.connectionId = clientSocket.getRemoteSocketAddress().toString();
        this.rsaCipher = null;
        this.aesTransformation = null;
    }

    @Override
    public void run() {
        long lastActivityTime = System.currentTimeMillis();
        long maxIdle = DEFAULT_IDLE_TIMEOUT_MS;

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            while (true) {
                if ((System.currentTimeMillis() - lastActivityTime) > maxIdle) {
                    LogHelper.getInstance().logInfo("Idle timeout: " + clientSocket.getRemoteSocketAddress());
                    break;
                }

                clientSocket.setSoTimeout(1000); // short poll window
                byte[] framedRequest = null;

                try {
                    framedRequest = MessageCodec.readFramedMessage(inputStream);

                    if (rsaCipher == null && config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
                        rsaCipher = CipherHelper.getCipherFromId(MessageCodec.extractEncryptionHint(), config.getSecurityConfigLoader().getPrivateKey());
                    } else if (aesTransformation == null && config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
                        aesTransformation = CipherHelper.getSymmetricTransformation(MessageCodec.extractEncryptionHint());
                        aesKeyBytes = GcmCryptoHelper.decodeBase64Key(config.getSecurityConfigLoader().getSharedEncryptionKey());
                    }

                    if (framedRequest == null) {
                        break;
                    }
                    lastActivityTime = System.currentTimeMillis();
                } catch (SocketTimeoutException timeout) {
                    // True read timeout; re-check idle
                    continue;
                } catch (IOException e) {
                    break;
                }

                // Check for ADMIN role and extend idle timeout
                if (isIdentified && session.getRole() == Role.ADMIN) {
                    maxIdle = ADMIN_IDLE_TIMEOUT_MS;
                }

                byte[] response = processInputData(framedRequest);

                if (response == null) {
                    LogHelper.getInstance().logFatal("Response is null. Skipping write to avoid crash.");
                    break;
                }

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

    private byte[] processInputData(byte[] data)  {
        byte[] decrypted;

        try {
            if (config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.ASYMMETRIC) {
                decrypted = rsaCipher.doFinal(data);
            } else if (config.getSecurityConfigLoader().getEncryptionMode() == EncryptionMode.SYMMETRIC) {
                decrypted = GcmCryptoHelper.decrypt(data, this.aesKeyBytes, this.aesTransformation);
            } else {
                decrypted = data;
            }
        } catch(Exception e) {
            return ("-ERR ENCRYPTION MODE Failed: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
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

        byte[] response;

        try {
            response = commandProcessor.execute(decrypted, this.session);
        } catch (Exception e) {
            LogHelper.getInstance().logFatal("Command execution failed: " + e.getMessage(), e);
            return errorToBytes("Command execution error: " + e.getMessage());
        }

        if (config.getCoreConfigLoader().isEnableVerbose()) {
            LogHelper.getInstance().logInfo(logTag + " Response: " + new String(response, StandardCharsets.UTF_8));
        }

        return response;
    }

    private byte[] errorToBytes(String message) {
        return ("-ERR " + message).getBytes(StandardCharsets.UTF_8);
    }
}
