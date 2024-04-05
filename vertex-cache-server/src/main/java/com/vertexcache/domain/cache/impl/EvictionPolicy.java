package com.vertexcache.domain.cache.impl;

public enum EvictionPolicy {
    NONE, // Unbounded, no eviction
    LRU, // Least Recently Used
    MRU, // Most Recently Used
    FIFO, // First in and First Out
    LFU, // Least Frequently Used
    RANDOM // Random
}
