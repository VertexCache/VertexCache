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
package com.vertexcache.vertexbench.load;

import com.vertexcache.vertexbench.load.impl.*;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

public class LoadTestFactory {

    public static BaseThroughputLoad createTest(String testName, VertexBenchConfig vertexBenchConfig) {
        LoadType type = LoadType.fromKey(testName);
        return switch (type) {
            case DEL_ONLY -> new DelOnlyThroughputLoad(vertexBenchConfig);
            case GET_ONLY -> new GetOnlyThroughputLoad(vertexBenchConfig);
            case MIXED -> new MixedThroughputLoad(vertexBenchConfig);
            case OPEN_LOOP -> new OpenLoopThroughputLoad(vertexBenchConfig);
            case SECONDARY_INDEX -> new SecondaryIndexLookupThroughputLoad(vertexBenchConfig);
            case SET_ONLY -> new SetOnlyThroughputLoad(vertexBenchConfig);
            case TERTIARY_INDEX -> new TertiaryIndexLookupThroughputLoad(vertexBenchConfig);
        };
    }
}
