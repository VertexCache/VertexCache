package com.vertexcache.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VertexCacheSDKTest {
    @Test
    void testGetMessage() {
        VertexCacheSDK sdk = new VertexCacheSDK();
        assertEquals("VertexCache SDK!", sdk.getMessage());
    }
}
