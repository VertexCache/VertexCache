package com.vertexcache.module.metric.model;

import java.util.Set;

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
