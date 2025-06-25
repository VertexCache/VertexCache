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
import com.vertexcache.vertexbench.exception.VertexBenchException;
import com.vertexcache.vertexbench.load.BaseThroughputLoad;
import com.vertexcache.vertexbench.load.LoadType;
import com.vertexcache.vertexbench.util.BenchConstants;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

import java.util.Random;

public class MixedThroughputLoad extends BaseThroughputLoad {

    private static final LoadType TYPE = LoadType.MIXED;

    public MixedThroughputLoad(VertexBenchConfig vertexBenchConfig) {
        super(TYPE, vertexBenchConfig);
        if (vertexBenchConfig.getPercentageReads() < 0 || vertexBenchConfig.getPercentageWrites() < 0 || (vertexBenchConfig.getPercentageReads() + vertexBenchConfig.getPercentageWrites()) > 100) {
            throw new VertexBenchException("Invalid operation ratios. GET + SET must be <= 100%");
        }
    }

    @Override
    protected void performOperation(Random rand) {
        String key = BenchConstants.BENCH_KEY + rand.nextInt(getVertexBenchConfig().getTotalKeyCount());
        int op = rand.nextInt(getVertexBenchConfig().getPercentageScale());  // 0 to 99

        VertexCacheSDK sdk = this.getVertexBenchConfig().getVertexCacheSDK();

        if (op < this.getVertexBenchConfig().getPercentageReads()) {
            sdk.get(key);
        } else if (op < this.getVertexBenchConfig().getPercentageReads() + this.getVertexBenchConfig().getPercentageWrites()) {
            sdk.set(key, BenchConstants.BENCH_VALUE + rand.nextInt(getVertexBenchConfig().getMaxValueSuffix()), null, null);
        } else {
            sdk.del(key);
        }
    }
}
