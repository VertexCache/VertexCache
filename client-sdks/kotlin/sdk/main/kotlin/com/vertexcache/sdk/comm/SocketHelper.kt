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

package com.vertexcache.sdk.comm

import com.vertexcache.sdk.model.ClientOption
import com.vertexcache.sdk.model.VertexCacheSdkException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocket

object SocketHelper {

    @Throws(VertexCacheSdkException::class)
    @JvmStatic
    fun createSocketTLS(opt: ClientOption): Socket {
        return try {
            val baseSocket = Socket()
            baseSocket.connect(InetSocketAddress(opt.serverHost, opt.serverPort), opt.connectTimeout)
            baseSocket.soTimeout = opt.readTimeout

            val factory = if (opt.verifyCertificate) {
                SSLHelper.createVerifiedSocketFactory(opt.tlsCertificate ?: throw VertexCacheSdkException("TLS certificate must be provided"))
            } else {
                SSLHelper.createInsecureSocketFactory()
            }

            val sslSocket = factory.createSocket(baseSocket, opt.serverHost, opt.serverPort, true) as SSLSocket
            sslSocket.soTimeout = opt.readTimeout
            sslSocket.startHandshake()
            sslSocket

        } catch (ex: Exception) {
            throw VertexCacheSdkException("Failed to create Secure Socket", ex)
        }
    }

    @JvmStatic
    @Throws(VertexCacheSdkException::class)
    fun createSocketNonTLS(opt: ClientOption): Socket {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(opt.serverHost, opt.serverPort), opt.connectTimeout)
            socket.soTimeout = opt.readTimeout
            socket
        } catch (ex: Exception) {
            throw VertexCacheSdkException("Failed to create Non Secure Socket", ex)
        }
    }

}

