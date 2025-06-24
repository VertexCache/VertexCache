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
package com.vertexcache.vertexbench;

import com.vertexcache.sdk.VertexCacheSDK;

import com.vertexcache.vertexbench.load.LoadTestFactory;
import com.vertexcache.vertexbench.load.ThroughputLoad;
import com.vertexcache.vertexbench.setup.CacheDataPopulator;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

public class VertexBench {


    public static void main(String[] args) throws Exception {

        System.out.println("VertexBench!");

        VertexBenchConfig vertexBenchConfig = new VertexBenchConfig();
        VertexCacheSDK sdk = vertexBenchConfig.buildSdk();

        if(vertexBenchConfig.isEnablePreload()) {
            CacheDataPopulator populator = new CacheDataPopulator(sdk);
            populator.populateBasic(1000);
            populator.populateWithIdx1(1000);
            populator.populateWithIdx2(1000);
        }

        ThroughputLoad loadTest = LoadTestFactory.createTest(
                "getonly",
                sdk,
                vertexBenchConfig.getThreads(),
                vertexBenchConfig.getDuration()
        );

        loadTest.execute();
    }
}
