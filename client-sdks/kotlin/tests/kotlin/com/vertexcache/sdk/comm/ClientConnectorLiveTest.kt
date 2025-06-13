package com.vertexcache.sdk.comm

import com.vertexcache.sdk.model.ClientOption
import com.vertexcache.sdk.model.EncryptionMode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "VC_LIVE_TEST", matches = "true")
class ClientConnectorLiveTest {

    companion object {
        private const val HOST = "127.0.0.1"
        private const val PORT = 50505
        private const val CLIENT_ID = "sdk-client-kotlin"
        private const val CLIENT_TOKEN = "5f38c3a4-753b-4339-a2a5-06b2446b7ae1"

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
            encryptionMode = EncryptionMode.ASYMMETRIC
            publicKey = TEST_PUBLIC_KEY
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
