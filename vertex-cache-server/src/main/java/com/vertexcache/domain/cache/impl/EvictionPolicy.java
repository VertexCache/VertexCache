package com.vertexcache.domain.cache.impl;

public enum EvictionPolicy {
    NONE("None", "None", "No eviction"),
    LRU("LRU", "Least Recently Used", "Evict least recently accessed items"),
    MRU("MRU", "Most Recently Used", "Evict most recently accessed items"),
    FIFO("FIFO", "First In First Out", "Evict oldest items first"),
    LFU("LFU", "Least Frequently Used", "Evict least frequently accessed items"),
    RANDOM("RANDOM", "Random", "Evict randomly");

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
