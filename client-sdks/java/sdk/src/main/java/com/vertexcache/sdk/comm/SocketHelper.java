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

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * SocketHelper provides low-level socket creation utilities for the VertexCache SDK.
 *
 * It supports both secure (TLS) and non-secure (plain TCP) socket connections depending on client configuration:
 *
 * - createSecureSocket(ClientOption): Establishes a TLS socket using either certificate verification
 *   (with a trusted PEM certificate) or insecure TLS (skipping certificate checks). The underlying
 *   SSL context is configured using SSLHelper.
 *
 * - createNonSecureSocket(ClientOption): Establishes a standard TCP socket connection with configurable
 *   timeouts for connection and read operations.
 *
 * Throws VertexCacheSdkException on any connection or socket initialization error.
 * Intended to be used internally by the transport layer of the SDK.
 */
public class SocketHelper {

    /**
     * Establishes a TLS-encrypted socket connection to the VertexCache server using the provided client options.
     *
     * Depending on the `verifyCertificate` flag in {@link ClientOption}, this method creates either a verified
     * or insecure SSL socket factory. It performs a full TLS handshake and returns a connected and secured socket.
     *
     * @param options the client connection and TLS configuration
     * @return a connected SSLSocket with server certificate verification based on client settings
     * @throws VertexCacheSdkException if socket creation, configuration, or TLS handshake fails
     */
    public static Socket createSecureSocket(ClientOption options) throws VertexCacheSdkException {
        try {
            Socket baseSocket = new Socket();
            baseSocket.connect(new InetSocketAddress(options.getServerHost(), options.getServerPort()), options.getConnectTimeout());
            baseSocket.setSoTimeout(options.getReadTimeout());
            SSLSocketFactory factory = options.isVerifyCertificate()
                    ? SSLHelper.createVerifiedSocketFactory(options.getTlsCertificate())
                    : SSLHelper.createInsecureSocketFactory();

            SSLSocket sslSocket = (SSLSocket) factory.createSocket(baseSocket, options.getServerHost(), options.getServerPort(), true);
            sslSocket.setSoTimeout(options.getReadTimeout());
            sslSocket.startHandshake();
            return sslSocket;
        } catch (Exception ex) {
            throw new VertexCacheSdkException("Failed to create Secure Socket");
        }
    }

    /**
     * Establishes a plain (non-TLS) socket connection to the VertexCache server using the provided client options.
     *
     * This method connects a standard TCP socket and applies the specified connect and read timeouts.
     * It is typically used for development or environments where encryption is not required.
     *
     * @param options the client connection configuration
     * @return a connected non-secure {@link Socket} instance
     * @throws VertexCacheSdkException if the socket connection fails or times out
     */
    public static Socket createSocketNonTLS(ClientOption options) throws VertexCacheSdkException {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(options.getServerHost(), options.getServerPort()), options.getConnectTimeout());
            socket.setSoTimeout(options.getReadTimeout());
            return socket;
        } catch (Exception ex) {
            throw new VertexCacheSdkException("Failed to create Non Secure Socket");
        }
    }
}
