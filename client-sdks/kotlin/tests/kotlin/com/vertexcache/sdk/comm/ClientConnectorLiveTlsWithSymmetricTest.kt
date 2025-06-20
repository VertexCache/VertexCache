package com.vertexcache.sdk.comm

import com.vertexcache.sdk.model.ClientOption
import com.vertexcache.sdk.model.EncryptionMode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "VC_LIVE_TLS_SYMMETRIC_TEST", matches = "true")
class ClientConnectorLiveTlsWithSymmetricTest {

    companion object {
        private const val HOST = "127.0.0.1"
        private const val PORT = 50505
        private const val CLIENT_ID = "sdk-client-kotlin"
        private const val CLIENT_TOKEN = "5f38c3a4-753b-4339-a2a5-06b2446b7ae1"

        private const val SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";
        private const val TLS_CERT = """-----BEGIN CERTIFICATE-----\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\nBAYTB1..."""
    }

    @Test
    fun testLiveConnectAndPing_shouldSucceed() {
        val opt = ClientOption().apply {
            clientId = CLIENT_ID
            clientToken = CLIENT_TOKEN
            serverHost = HOST
            serverPort = PORT
            enableTlsEncryption = true
            verifyCertificate = false
            tlsCertificate = TLS_CERT
            encryptionMode = EncryptionMode.SYMMETRIC
            sharedEncryptionKey = SHARED_KEY
        }

        val client = ClientConnector(opt)
        client.connect()
        assertTrue(client.isConnected())

        val reply = client.send("PING")
        assertNotNull(reply)
        assertTrue(reply.startsWith("+PONG"))

        client.close()
        assertFalse(client.isConnected())
    }
}
