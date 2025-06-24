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
package com.vertexcache.vertexbench.load.impl;

import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.vertexbench.load.BaseThroughputLoad;

import java.util.Random;

public class GetOnlyThroughputLoad extends BaseThroughputLoad {

    private final static String TITLE = "GET-Only";

    public GetOnlyThroughputLoad(VertexCacheSDK sdk, int threads, int duration) {
        super(TITLE, sdk, threads, duration);
    }

    protected void performOperation(VertexCacheSDK sdk, Random rand) throws Exception {
        String key = "bench:key:" + rand.nextInt(1000);
        getSdk().get(key);
    }
}
