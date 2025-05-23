package com.vertexcache.module.metric.model;

public final class MetricViewKey {
    private MetricViewKey() {} // prevent instantiation

    public static final String COMMAND_GET_TOTAL = "commands.get.total";
    public static final String COMMAND_SET_TOTAL = "commands.set.total";
    public static final String COMMAND_DEL_TOTAL = "commands.del.total";

    public static final String CACHE_HIT_COUNT   = "cache.hit.count";
    public static final String CACHE_MISS_COUNT  = "cache.miss.count";
    public static final String CACHE_HIT_RATIO   = "cache.hit.ratio";
    public static final String CACHE_KEY_COUNT   = "cache.key.count";

    public static final String INDEX_USAGE_IDX1  = "cache.index.usage.idx1";
    public static final String INDEX_USAGE_IDX2  = "cache.index.usage.idx2";

    public static final String JVM_MEMORY_USED_MB         = "memory.used.mb";
    public static final String JVM_MEMORY_FREE_MB         = "memory.free.mb";
    public static final String JVM_MEMORY_MAX_MB          = "memory.max.mb";
    public static final String JVM_MEMORY_ALLOCATED_MB    = "memory.total.allocated.mb";
    public static final String JVM_UPTIME                 = "uptime";

    public static final String HOT_KEY_PREFIX = "key.";
    public static final String HOT_HITS_PREFIX = "hits.";
}
