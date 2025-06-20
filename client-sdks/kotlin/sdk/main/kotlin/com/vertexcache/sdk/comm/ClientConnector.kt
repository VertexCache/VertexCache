// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ------------------------------------------------------------------------------

package com.vertexcache.sdk.comm

import com.vertexcache.sdk.model.EncryptionMode
import com.vertexcache.sdk.model.VertexCacheSdkException
import com.vertexcache.sdk.model.ClientOption
import java.io.*
import java.net.Socket
import java.security.PublicKey
import javax.crypto.Cipher

/**
 * ClientConnector is the core transport used by the VertexCache SDK to connect to and communicate with a VertexCache server.
 * It manages socket creation, TLS negotiation (with optional certificate verification), client authentication via IDENT,
 * and encryption of messages using either symmetric (AES-GCM) or asymmetric (RSA) methods.
 */
class ClientConnector(private val options: ClientOption) {

    private var socket: Socket? = null
    private var writer: BufferedOutputStream? = null
    private var reader: BufferedInputStream? = null
    private var connected: Boolean = false

    /**
     * Establishes a connection and performs IDENT handshake.
     * Uses TLS or plain socket depending on configuration.
     */
    fun connect() {
        try {
            socket = if (options.enableTlsEncryption) {
                SocketHelper.createSocketTLS(options)
            } else {
                SocketHelper.createSocketNonTLS(options)
            }

            writer = BufferedOutputStream(socket!!.getOutputStream())
            reader = BufferedInputStream(socket!!.getInputStream())

            val identBytes = encryptIfEnabled(options.buildIdentCommand().toByteArray())
            MessageCodec.writeFramedMessage(writer!!, identBytes)
            writer!!.flush()

            val response = MessageCodec.readFramedMessage(reader!!)
            val identResponse = response?.toString(Charsets.UTF_8)?.trim() ?: ""
            if (!identResponse.startsWith("+OK")) {
                throw VertexCacheSdkException("Authorization failed: $identResponse")
            }
            connected = true
        } catch (e: Exception) {
            throw VertexCacheSdkException("Connection failed", e)
        }
    }

    /**
     * Sends a message and waits for framed response.
     */
    @Synchronized
    fun send(message: String): String {
        try {
            val payload = encryptIfEnabled(message.toByteArray())
            MessageCodec.writeFramedMessage(writer!!, payload)
            writer!!.flush()
            val response = MessageCodec.readFramedMessage(reader!!)
                ?: throw VertexCacheSdkException("Connection closed by server")
            return String(response, Charsets.UTF_8)
        } catch (e: Exception) {
            throw VertexCacheSdkException("Unexpected failure during send", e)
        }
    }

    /**
     * Applies encryption based on configured mode.
     */
    private fun encryptIfEnabled(plainText: ByteArray): ByteArray {
        return try {
            when (options.encryptionMode) {
                EncryptionMode.ASYMMETRIC -> {
                    MessageCodec.switchToAsymmetric()
                    val cipher = Cipher.getInstance("RSA")
                    cipher.init(Cipher.ENCRYPT_MODE, options.getPublicKeyAsObject())
                    cipher.doFinal(plainText)
                }
                EncryptionMode.SYMMETRIC -> {
                    MessageCodec.switchToSymmetric()
                    GcmCryptoHelper.encrypt(plainText, options.getSharedEncryptionKeyAsBytes())
                }
                EncryptionMode.NONE -> plainText
            }
        } catch (e: Exception) {
            throw VertexCacheSdkException("Encryption failed for, text redacted *****", e)
        }
    }

    /**
     * Checks connection state.
     */
    fun isConnected(): Boolean {
        return connected && socket != null && socket!!.isConnected &&
                !socket!!.isClosed && !socket!!.isInputShutdown && !socket!!.isOutputShutdown
    }

    /**
     * Closes socket.
     */
    fun close() {
        try {
            socket?.close()
        } catch (_: IOException) {
        }
        connected = false
    }
}

