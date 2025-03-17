package com.vertexcache.client;

import com.vertexcache.sdk.VertexCacheSDK;

public class VertexCacheClient {
    public static void main(String[] args) {
        VertexCacheSDK sdk = new VertexCacheSDK();
        System.out.println(sdk.getMessage());
    }
}
