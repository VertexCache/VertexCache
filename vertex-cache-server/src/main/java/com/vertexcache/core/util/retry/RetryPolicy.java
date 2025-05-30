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
package com.vertexcache.core.util.retry;

/**
 * Interface defining a strategy for computing retry delays.
 *
 * Implementations determine how long to wait before retrying an operation
 * based on the current attempt count.
 *
 * Can be used to plug in exponential backoff, fixed delay, or other custom logic.
 */
public interface RetryPolicy {
    int computeDelayForAttempt(int attempt);
}