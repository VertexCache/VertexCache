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
package com.vertexcache.module.metric.model;

/**
 * Centralized definition of all metric string keys used in the system.
 * Each enum constant maps to its full metric name string.
 */
public enum MetricKey {
    CACHE_HIT_COUNT("cache.hit.count"),
    CACHE_MISS_COUNT("cache.miss.count"),
    CACHE_SET_TOTAL("cache.set.total"),
    CACHE_GET_TOTAL("cache.get.total"),
    CACHE_DEL_TOTAL("cache.del.total"),
    CACHE_EVICTIONS_TOTAL("cache.evictions.total"),
    CACHE_EXPIRED_TOTAL("cache.expired.total"),
    CACHE_KEY_COUNT("cache.key.count"),

    CACHE_VALUE_SIZE_BYTES("cache.value.size.bytes"),
    CACHE_AVG_VALUE_SIZE_BYTES("cache.avg.value.size.bytes"),

    CACHE_TTL_LT_10S("cache.ttl.distribution.lt10s"),
    CACHE_TTL_10S_60S("cache.ttl.distribution.10s_60s"),
    CACHE_TTL_60S_600S("cache.ttl.distribution.60s_600s"),
    CACHE_TTL_600S_PLUS("cache.ttl.distribution.600s_plus"),

    CACHE_INDEX_USAGE_IDX1("cache.index.usage.idx1"),
    CACHE_INDEX_USAGE_IDX2("cache.index.usage.idx2"),

    CACHE_HOTKEYS_TOP_N("cache.hotkeys.topN");

    private final String value;

    MetricKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
