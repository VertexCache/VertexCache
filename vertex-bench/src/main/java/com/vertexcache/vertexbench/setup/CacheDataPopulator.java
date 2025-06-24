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
package com.vertexcache.vertexbench.setup;

import com.vertexcache.sdk.VertexCacheSDK;

public class CacheDataPopulator {

    private VertexCacheSDK sdk;

    public CacheDataPopulator(VertexCacheSDK sdk) {
        this.sdk = sdk;
    }

    public void populateBasic(int entries) throws Exception {
        System.out.printf("Populating %d basic key/value pairs...\n", entries);
        for (int i = 0; i < entries; i++) {
            String key = "bench:key:" + i;
            sdk.set(key, "value-" + i, null, null);
        }
    }

    public void populateWithIdx1(int entries) throws Exception {
        System.out.printf("Populating %d key/value pairs with idx1...\n", entries);
        for (int i = 0; i < entries; i++) {
            String key = "bench:key:" + i;
            String idx1 = "bench:idx1:" + i;
            sdk.set(key, "value-" + i, idx1, null);
        }
    }

    public void populateWithIdx2(int entries) throws Exception {
        System.out.printf("Populating %d key/value pairs with idx1 and idx2...\n", entries);
        for (int i = 0; i < entries; i++) {
            String key = "bench:key:" + i;
            String idx1 = "bench:idx1:" + i;
            String idx2 = "bench:idx2:" + i;
            sdk.set(key, "value-" + i, idx1, idx2);
        }
    }
}
