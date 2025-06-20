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
package com.vertexcache.sdk


import com.vertexcache.sdk.model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.*

@EnabledIfEnvironmentVariable(named = "VC_LIVE_TLS_ASYMMETRIC_TEST", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VertexCacheSDKLiveTest {

    companion object {
        private const val CLIENT_ID = "sdk-client-kotlin"
        private const val CLIENT_TOKEN = "5f38c3a4-753b-4339-a2a5-06b2446b7ae1"
        private const val VERTEXCACHE_SERVER_HOST = "localhost"
        private const val VERTEXCACHE_SERVER_PORT = 50505
        private const val ENABLE_TLS = true
        private const val TEST_TLS_CERT = "..."
        private const val TEST_PUBLIC_KEY = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q
            bw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI
            UzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm
            GzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR
            NwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo
            6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV
            EwIDAQAB
            -----END PUBLIC KEY-----
        """
        private val ENABLE_PUBLIC_PRIVATE_KEY_USE = EncryptionMode.ASYMMETRIC
    }

    private lateinit var sdk: VertexCacheSDK

    @BeforeEach
    fun setUp() {
        val clientOption = ClientOption().apply {
            clientId = CLIENT_ID
            clientToken = CLIENT_TOKEN
            serverHost = VERTEXCACHE_SERVER_HOST
            serverPort = VERTEXCACHE_SERVER_PORT
            enableTlsEncryption = ENABLE_TLS
            tlsCertificate = TEST_TLS_CERT
            encryptionMode = ENABLE_PUBLIC_PRIVATE_KEY_USE
            publicKey = TEST_PUBLIC_KEY
        }
        sdk = VertexCacheSDK(clientOption)
        sdk.openConnection()
    }

    @AfterEach
    fun tearDown() {
        sdk.close()
    }

    @Test @Order(1)
    fun testPingShouldSucceed() {
        val result = sdk.ping()
        assertTrue(result.isSuccess())
        assertTrue(result.getMessage().startsWith("PONG"))
    }

    @Test @Order(2)
    fun testSetShouldSucceed() {
        val result = sdk.set("test-key", "value-123")
        assertTrue(result.isSuccess())
        assertEquals("OK", result.getMessage())
    }

    @Test @Order(3)
    fun testGetShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123")
        val result = sdk.get("test-key")
        assertTrue(result.isSuccess())
        assertEquals("value-123", result.getValue())
    }

    @Test @Order(4)
    fun testDelShouldSucceedAndRemoveKey() {
        sdk.set("delete-key", "to-be-deleted")
        val delResult = sdk.del("delete-key")
        assertTrue(delResult.isSuccess())
        val getResult = sdk.get("delete-key")
        assertTrue(getResult.isSuccess())
        assertTrue(getResult.getValue().isNullOrEmpty())
    }

    @Test @Order(5)
    fun testGetOnMissingKeyShouldFail() {
        val result = sdk.get("nonexistent-key")
        assertTrue(result.isSuccess())
        assertTrue(result.getValue().isNullOrEmpty())
    }

    @Test @Order(6)
    fun testSetSecondaryIndexShouldSucceed() {
        val result = sdk.set("test-key", "value-123", "test-secondary-index")
        assertTrue(result.isSuccess())
        assertEquals("OK", result.getMessage())
    }

    @Test @Order(7)
    fun testSetSecondaryAndTertiaryIndexShouldSucceed() {
        val result = sdk.set("test-key", "value-123", "test-secondary-index", "test-tertiary-index")
        assertTrue(result.isSuccess())
        assertEquals("OK", result.getMessage())
    }

    @Test @Order(8)
    fun testGetBySecondaryIndexShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123", "test-secondary-index")
        val result = sdk.getBySecondaryIndex("test-secondary-index")
        assertTrue(result.isSuccess())
        assertEquals("value-123", result.getValue())
    }

    @Test @Order(9)
    fun testGetByTertiaryIndexShouldReturnPreviouslySetValue() {
        sdk.set("test-key", "value-123", "test-secondary-index", "test-tertiary-index")
        val result = sdk.getByTertiaryIndex("test-tertiary-index")
        assertTrue(result.isSuccess())
        assertEquals("value-123", result.getValue())
    }

    @Test
    @Order(10)
    fun testMultibyteKeyAndValueShouldSucceed() {
        val multibyteKey = "键🔑値🌟"
        val multibyteValue = "测试🧪データ💾"
        val setResult = sdk.set(multibyteKey, multibyteValue)
        assertTrue(setResult.isSuccess())
        assertEquals("OK", setResult.getMessage())
        val getResult = sdk.get(multibyteKey)
        assertTrue(getResult.isSuccess())
        assertEquals(multibyteValue, getResult.getValue())
    }

}
