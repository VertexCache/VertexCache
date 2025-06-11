// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache)
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

package com.vertexcache.sdk.model

import com.vertexcache.sdk.comm.KeyParserHelper
import java.security.PublicKey

/**
 * Configuration container for initializing the VertexCache SDK client.
 *
 * This class holds all user-specified options required to establish a connection
 * to a VertexCache server, including host, port, TLS settings, authentication tokens,
 * encryption modes (asymmetric or symmetric), and related keys or certificates.
 *
 * It provides a flexible way to customize client behavior, including security preferences.
 */
class ClientOption {

    companion object {
        const val DEFAULT_CLIENT_ID = "sdk-client"
        const val DEFAULT_HOST = "127.0.0.1"
        const val DEFAULT_PORT = 50505
        const val DEFAULT_READ_TIMEOUT = 3000
        const val DEFAULT_CONNECT_TIMEOUT = 3000
    }

    var clientId: String? = DEFAULT_CLIENT_ID
    var clientToken: String? = null

    var serverHost: String = DEFAULT_HOST
    var serverPort: Int = DEFAULT_PORT

    var enableTlsEncryption: Boolean = false
    var tlsCertificate: String? = null
    var verifyCertificate: Boolean = false

    var encryptionMode: EncryptionMode = EncryptionMode.NONE
    var encryptWithPublicKey: Boolean = false
    var encryptWithSharedKey: Boolean = false

    var publicKey: String? = null
    var sharedEncryptionKey: String? = null

    var readTimeout: Int = DEFAULT_READ_TIMEOUT
    var connectTimeout: Int = DEFAULT_CONNECT_TIMEOUT

    fun getPublicKeyAsObject(): PublicKey = KeyParserHelper.configPublicKeyIfEnabled(publicKey ?: "")
    fun getSharedEncryptionKeyAsBytes(): ByteArray = KeyParserHelper.configSharedKeyIfEnabled(sharedEncryptionKey ?: "")

    fun buildClientId(): String {
        return clientId ?: ""
    }

    fun buildClientToken(): String {
        return clientToken ?: ""
    }

    fun buildIdentCommand(): String {
        return "IDENT {\"client_id\":\"${buildClientId()}\", \"token\":\"${buildClientToken()}\"}"
    }
}
