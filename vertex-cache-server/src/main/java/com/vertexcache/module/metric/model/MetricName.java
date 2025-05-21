package com.vertexcache.module.metric.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a metric name, its type, and associated tags.
 */
public enum MetricName {

    // Core cache operations
    CACHE_HIT_COUNT(MetricKey.CACHE_HIT_COUNT.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_MISS_COUNT(MetricKey.CACHE_MISS_COUNT.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_SET_TOTAL(MetricKey.CACHE_SET_TOTAL.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_GET_TOTAL(MetricKey.CACHE_GET_TOTAL.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_DEL_TOTAL(MetricKey.CACHE_DEL_TOTAL.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_EVICTIONS_TOTAL(MetricKey.CACHE_EVICTIONS_TOTAL.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_EXPIRED_TOTAL(MetricKey.CACHE_EXPIRED_TOTAL.value(), MetricType.COUNTER, List.of(MetricTag.CORE)),
    CACHE_KEY_COUNT(MetricKey.CACHE_KEY_COUNT.value(), MetricType.GAUGE, List.of(MetricTag.CORE)),

    // Value size tracking
    CACHE_VALUE_SIZE_BYTES(MetricKey.CACHE_VALUE_SIZE_BYTES.value(), MetricType.RECORDER, List.of(MetricTag.VALUE)),
    CACHE_AVG_VALUE_SIZE_BYTES(MetricKey.CACHE_AVG_VALUE_SIZE_BYTES.value(), MetricType.DERIVED, List.of(MetricTag.VALUE)),

    // TTL Distribution
    CACHE_TTL_LT_10S(MetricKey.CACHE_TTL_LT_10S.value(), MetricType.COUNTER, List.of(MetricTag.TTL)),
    CACHE_TTL_10S_60S(MetricKey.CACHE_TTL_10S_60S.value(), MetricType.COUNTER, List.of(MetricTag.TTL)),
    CACHE_TTL_60S_600S(MetricKey.CACHE_TTL_60S_600S.value(), MetricType.COUNTER, List.of(MetricTag.TTL)),
    CACHE_TTL_600S_PLUS(MetricKey.CACHE_TTL_600S_PLUS.value(), MetricType.COUNTER, List.of(MetricTag.TTL)),

    // Index usage
    CACHE_INDEX_USAGE_IDX1(MetricKey.CACHE_INDEX_USAGE_IDX1.value(), MetricType.COUNTER, List.of(MetricTag.INDEX)),
    CACHE_INDEX_USAGE_IDX2(MetricKey.CACHE_INDEX_USAGE_IDX2.value(), MetricType.COUNTER, List.of(MetricTag.INDEX));

    private static final Map<String, MetricName> VALUE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(MetricName::getName, Function.identity()));

    private final String name;
    private final MetricType type;
    private final List<MetricTag> tags;

    MetricName(String name, MetricType type, List<MetricTag> tags) {
        this.name = name;
        this.type = type;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public MetricType getType() {
        return type;
    }

    public List<MetricTag> getTags() {
        return tags;
    }

    public static MetricName fromValue(String value) {
        return VALUE_MAP.get(value);
    }

    // ---------------------------
    // Common key prefixes & keys
    // ---------------------------

    public static final String TTL_DISTRIBUTION_PREFIX = "cache.ttl.distribution.";
    public static final String HOTKEYS_KEY = MetricKey.CACHE_HOTKEYS_TOP_N.value();
    public static final String AVG_VALUE_SIZE_KEY = MetricKey.CACHE_AVG_VALUE_SIZE_BYTES.value();
}
