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
 * Enum representing the jitter strategy to apply during retry backoff calculations.
 *
 * Jitter is used to randomize retry delays and avoid thundering herd problems when multiple
 * clients retry simultaneously.
 *
 * Available strategies:
 * - NONE: No jitter, fixed exponential delay
 * - FULL: Random delay between 0 and the computed maximum
 * - EQUAL: Half the delay ± a small randomized range
 *
 * Used by RetryBackoffService to add randomness to retry intervals.
 */
public enum JitterStrategy {
    NONE,      // Deterministic exponential delay
    FULL,      // Random between 0 and capped
    EQUAL      // Half of capped ± random jitter within half
}