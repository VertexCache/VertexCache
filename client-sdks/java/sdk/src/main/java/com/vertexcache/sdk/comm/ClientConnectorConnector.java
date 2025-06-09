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
package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.VertexCacheSdkException;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;

/**
 * TcpClient is the core transport used by the VertexCache SDK to connect to and communicate with a VertexCache server.
 * It manages socket creation, TLS negotiation (with optional certificate verification), client authentication via IDENT,
 * and encryption of messages using either symmetric (AES-GCM) or asymmetric (RSA) methods.
 *
 * Key features:
 *  - Establishes raw or TLS socket connections
 *  - Supports both secure and insecure TLS modes
 *  - Automatically sends an IDENT command with client credentials
 *  - Encrypts all outgoing payloads based on configured encryption mode (could be NONE if NONE set)
 *  - Handles reconnect and retry logic on I/O failures
 *  - Wraps exceptions into meaningful SDK-specific types for better debugging
 *  - Configuration (host, port, TLS, encryption mode, etc.) is provided via the ClientOption class.
 */
public class ClientConnectorConnector implements ClientConnectorInterface {

    private Socket socket;
    private OutputStream writer;
    private InputStream reader;
    private ClientOption options;
    private boolean connected = false;

    public ClientConnectorConnector(ClientOption options) {
        this.options = options;
    }

    /**
     * Establishes a connection to the VertexCache server using the settings defined in the ClientOption.
     *
     * If TLS encryption is enabled, a secure SSL/TLS socket is created; otherwise, a plain socket is used.
     *
     * After establishing the connection, the method performs an IDENT handshake by sending the client ID
     * and token in a framed and optionally encrypted message. It then reads the response from the server.
     *
     * If the server does not respond with a valid "+OK" acknowledgment, an authorization failure is raised.
     *
     * Any exceptions during socket creation, stream setup, encryption, or handshake are wrapped and rethrown
     * as a VertexCacheSdkException.
     */
    public void connect() {
        try {
            if (options.isEnableTlsEncryption()) {
                this.socket = SocketHelper.createSecureSocket(options);
            } else {
                this.socket = SocketHelper.createSocketNonTLS(options);
            }
            this.writer = new BufferedOutputStream(socket.getOutputStream());
            this.reader = new BufferedInputStream(socket.getInputStream());
            MessageCodec.writeFramedMessage(writer, encryptIfEnabled(this.options.buildIdentCommand().getBytes()));
            writer.flush();
            byte[] identResponse = MessageCodec.readFramedMessage(reader);
            String identStr = identResponse == null ? "" : new String(identResponse).trim();
            if (!identStr.startsWith("+OK")) {
                throw new VertexCacheSdkException("Authorization failed: " + identStr);
            }
            this.connected = true;
        } catch (Exception e) {
            throw new VertexCacheSdkException(e.getMessage());
        }
    }

    /**
     * Sends a message to the VertexCache server and waits for a framed response.
     *
     * The message is first encrypted if encryption is enabled, then written to the server
     * using the defined framing protocol. After flushing the output stream, it waits for
     * a response from the server and reads it using the same framing logic.
     *
     * If the server closes the connection before sending a response, an exception is thrown.
     * Any errors during message transmission, encryption, or framing are wrapped and rethrown
     * as a VertexCacheSdkException.
     *
     * This method is synchronized to ensure thread-safe access to the underlying socket streams.
     *
     * @param message the plain text message to send
     * @return the server's response as a decoded string
     * @throws VertexCacheSdkException if the connection is interrupted or an error occurs during send/receive
     */
    public synchronized String send(String message) {
        try {
            byte[] toSend = encryptIfEnabled(message.getBytes());
            MessageCodec.writeFramedMessage(writer, toSend);
            writer.flush();
            byte[] response = MessageCodec.readFramedMessage(reader);
            if (response == null) {
                throw new VertexCacheSdkException("Connection closed by server");
            }
            return new String(response);
        } catch (Exception ex) {
            throw new VertexCacheSdkException("Unexpected failure during send");
        }
    }

    /**
     * Encrypts the given plaintext based on the configured encryption mode.
     *
     * If the encryption mode is ASYMMETRIC, it encrypts using the configured RSA public key.
     * If the mode is SYMMETRIC, it encrypts using AES-GCM with the provided shared key.
     * If encryption is disabled (NONE), it returns the plaintext unchanged.
     *
     * This abstraction allows the caller to remain agnostic to encryption details, while ensuring
     * proper encryption is applied consistently according to the client's configuration.
     *
     * @param plainText the raw byte array to encrypt
     * @return the encrypted byte array or the original plaintext if encryption is disabled
     * @throws VertexCacheSdkException if encryption fails due to missing or invalid key configuration
     */
    private byte[] encryptIfEnabled(byte[] plainText) throws VertexCacheSdkException {
        try {
            switch (options.getEncryptionMode()) {
                case ASYMMETRIC:
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, options.getPublicKeyAsObject());
                    return cipher.doFinal(plainText);
                case SYMMETRIC:
                    return GcmCryptoHelper.encrypt(plainText, options.getSharedEncryptionKeyAsBytes());
                case NONE:
                default:
                    return plainText;
            }
        } catch (Exception e) {
            throw new VertexCacheSdkException("Encryption failed for, text redacted *****");
        }
    }

    /**
     * Checks whether the underlying socket connection is currently active.
     *
     * This method verifies that the socket is non-null, connected, not closed,
     * and that both input and output streams are still operational.
     *
     * @return true if the socket is in a valid connected state, false otherwise
     */
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected()
                && !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    /**
     * Closes the underlying socket connection if it is open.
     *
     * This method safely shuts down the connection, ignoring any I/O exceptions
     * that may occur during the close operation.
     */
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        connected = false;
    }
}
