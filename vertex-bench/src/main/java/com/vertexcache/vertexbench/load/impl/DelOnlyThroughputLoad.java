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

import com.vertexcache.vertexbench.load.BaseThroughputLoad;
import com.vertexcache.vertexbench.load.LoadType;
import com.vertexcache.vertexbench.util.BenchConstants;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

import java.util.Random;

public class DelOnlyThroughputLoad extends BaseThroughputLoad {

    private static final LoadType TYPE = LoadType.DEL_ONLY;

    public DelOnlyThroughputLoad(VertexBenchConfig vertexBenchConfig) {
        super(TYPE, vertexBenchConfig);
    }

    @Override
    protected void performOperation(Random rand) {
        String key = BenchConstants.BENCH_KEY + rand.nextInt(getVertexBenchConfig().getTotalKeyCount());
        this.getVertexBenchConfig().getVertexCacheSDK().del(key);
    }
}
