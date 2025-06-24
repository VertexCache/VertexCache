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

import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.vertexbench.load.impl.*;

public class LoadTestFactory {

    public static BaseThroughputLoad createTest(String testName, VertexCacheSDK sdk, int threads, int durationSeconds) {

        return switch (testName.toLowerCase()) {
            case "openloop" -> new OpenLoopThroughputLoad(sdk, threads, durationSeconds);
            case "getonly" -> new GetOnlyThroughputLoad(sdk, threads, durationSeconds);
            case "setonly" -> new SetOnlyThroughputLoad(sdk, threads, durationSeconds);
            case "secondaryindex" -> new SecondaryIndexLookupThroughputLoad(sdk, threads, durationSeconds);
            case "tertiaryindex" -> new TertiaryIndexLookupThroughputLoad(sdk, threads, durationSeconds);
            default -> throw new IllegalArgumentException("Unknown test name: " + testName);
        };
    }
}
