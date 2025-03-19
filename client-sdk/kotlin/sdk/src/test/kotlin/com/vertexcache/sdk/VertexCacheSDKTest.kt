package com.vertexcache.sdk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VertexCacheSDKTest {

    @Test
    fun `should return correct message`() {
        val sdk = VertexCacheSDK()
        val result = sdk.getMessage()
        assertEquals("VertexCache SDK!", result)
    }
}
