package com.vertexcache.core.cache.impl;

public enum EvictionPolicy {
    NONE("None", "None", "No eviction"),
    RANDOM("RANDOM", "Random", "Evict randomly"),

    LRU("LRU", "Least Recently Used", "Evict least recently accessed items"),
    MRU("MRU", "Most Recently Used", "Evict most recently accessed items"),
    FIFO("FIFO", "First In First Out", "Evict oldest items first"),
    LFU("LFU", "Least Frequently Used", "Evict least frequently accessed items"),

    ARC("ARC", "Adaptive Replacement Cache", "Combines the benefits of both LRU (Least Recently Used) and LFU (Least Frequently Used) cache"),
    TwoQueues("2Queue", "Two Queues", "Cache by prioritizing recently accessed items (inQueue) and identifying less frequently accessed items for potential eviction (outQueue)"),
    Clock("Clock", "Clock (or Clock-Pro)", "Cache based on the Clock (or Clock-Pro) algorithm, designed to efficiently manage a cache of key-value pairs with a limited capacity"),
    TinyLFU("TinyLFU", "Tiny Least Frequently Used", " Cache designed to efficiently manage cache evictions while maintaining high hit ratios, especially in scenarios with skewed access patterns"),
    ;

    private final String abbreviation;
    private final String description;
    private final String details;

    EvictionPolicy(String abbreviation, String description, String details) {
        this.abbreviation = abbreviation;
        this.description = description;
        this.details = details;
    }

    public static EvictionPolicy fromString(String value) {
        for (EvictionPolicy policy : EvictionPolicy.values()) {
            if (policy.name().equalsIgnoreCase(value) || policy.getAbbreviation().equalsIgnoreCase(value)) {
                return policy;
            }
        }
        throw new IllegalArgumentException("Unknown eviction policy: " + value);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return this.abbreviation + " (" + this.description + "), " + this.description;
    }
}
