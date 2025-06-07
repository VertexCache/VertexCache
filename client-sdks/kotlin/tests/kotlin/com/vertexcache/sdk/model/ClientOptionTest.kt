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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientOptionTest {

    @Test
    fun testDefaults() {
        val option = ClientOption()

        assertEquals("sdk-client", option.buildClientId())
        assertEquals("", option.buildClientToken())
        assertEquals("127.0.0.1", option.serverHost)
        assertEquals(50505, option.serverPort)
        assertFalse(option.enableTlsEncryption)
        assertFalse(option.verifyCertificate)
        assertEquals(3000, option.readTimeout)
        assertEquals(3000, option.connectTimeout)
        assertEquals(EncryptionMode.NONE, option.encryptionMode)
        assertNotNull(option.buildIdentCommand())
    }

    @Test
    fun testSetValues() {
        val option = ClientOption()
        option.clientId = "test-client"
        option.clientToken = "token123"
        option.serverHost = "192.168.1.100"
        option.serverPort = 9999
        option.enableTlsEncryption = true
        option.verifyCertificate = true
        option.tlsCertificate = "cert"
        option.connectTimeout = 1234
        option.readTimeout = 5678
        option.encryptionMode = EncryptionMode.SYMMETRIC

        assertEquals("test-client", option.buildClientId())
        assertEquals("token123", option.buildClientToken())
        assertEquals("192.168.1.100", option.serverHost)
        assertEquals(9999, option.serverPort)
        assertTrue(option.enableTlsEncryption)
        assertTrue(option.verifyCertificate)
        assertEquals("cert", option.tlsCertificate)
        assertEquals(1234, option.connectTimeout)
        assertEquals(5678, option.readTimeout)
        assertEquals(EncryptionMode.SYMMETRIC, option.encryptionMode)
    }

    @Test
    fun testIdentCommandGeneration() {
        val option = ClientOption()
        option.clientId = "my-id"
        option.clientToken = "my-token"
        val expected = "IDENT {\"client_id\":\"my-id\", \"token\":\"my-token\"}"
        assertEquals(expected, option.buildIdentCommand())
    }

    @Test
    fun testNullTokenAndIdFallback() {
        val option = ClientOption()
        option.clientId = null
        option.clientToken = null
        val ident = option.buildIdentCommand()
        assertTrue(ident.contains("\"client_id\":\"\""))
        assertTrue(ident.contains("\"token\":\"\""))
    }
}
