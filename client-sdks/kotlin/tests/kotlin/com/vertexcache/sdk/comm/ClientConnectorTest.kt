package com.vertexcache.sdk.comm


import com.vertexcache.sdk.comm.ClientConnector
import com.vertexcache.sdk.model.ClientOption
import com.vertexcache.sdk.model.EncryptionMode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientConnectorTest {

    @Test
    fun `constructor should initialize without error`() {
        val opt = ClientOption()
        val client = ClientConnector(opt)
        assertNotNull(client)
    }

    @Test
    fun `isConnected should be false before connect`() {
        val opt = ClientOption()
        val client = ClientConnector(opt)
        assertFalse(client.isConnected())
    }

    @Test
    fun `close should not throw when socket is not connected`() {
        val opt = ClientOption()
        val client = ClientConnector(opt)
        assertDoesNotThrow {
            client.close()
        }
    }

    @Test
    fun `buildIdentCommand should format correctly`() {
        val opt = ClientOption()
        opt.clientId = "foo"
        opt.clientToken = "bar"
        val expected = "IDENT {\"client_id\":\"foo\", \"token\":\"bar\"}"
        assertEquals(expected, opt.buildIdentCommand())
    }

    @Test
    fun `getSharedEncryptionKeyAsBytes should decode base64`() {
        val opt = ClientOption()
        val key = "c2VjcmV0MTIzNDU2Nzg5MDEyMw==" // "secret1234567890123" in base64
        opt.sharedEncryptionKey = key
        val bytes = opt.getSharedEncryptionKeyAsBytes()
        assertEquals(19, bytes.size)
        assertEquals("secret1234567890123", bytes.toString(Charsets.UTF_8))
    }

    @Test
    fun `getPublicKeyAsObject should throw on invalid key`() {
        val opt = ClientOption()
        opt.publicKey = "invalid-key"
        assertThrows(Exception::class.java) {
            opt.getPublicKeyAsObject()
        }
    }

    @Test
    fun `encryption mode NONE should return plaintext`() {
        val opt = ClientOption()
        opt.encryptionMode = EncryptionMode.NONE
        val client = ClientConnector(opt)

        val method = client.javaClass.getDeclaredMethod("encryptIfEnabled", ByteArray::class.java)
        method.isAccessible = true
        val input = "hello".toByteArray()
        val result = method.invoke(client, input) as ByteArray

        assertArrayEquals(input, result)
    }
}
