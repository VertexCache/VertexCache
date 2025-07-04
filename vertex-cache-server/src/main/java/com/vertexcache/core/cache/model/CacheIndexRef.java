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
package com.vertexcache.core.cache.model;

/**
 * Represents a reference to a secondary or tertiary index associated with a cache entry.
 * This object is used to maintain reverse mappings from index values (e.g., idx1, idx2)
 * back to the primary cache key, enabling fast lookup and index-based retrieval.
 *
 * Typically managed internally by the indexing subsystem to support efficient multi-key access patterns.
 */
public class CacheIndexRef {
    public final Object idx1;
    public final Object idx2;

    public CacheIndexRef(Object idx1, Object idx2) {
        this.idx1 = idx1;
        this.idx2 = idx2;
    }

    public Object getIdx1() {
        return idx1;
    }

    public Object getIdx2() {
        return idx2;
    }
}
