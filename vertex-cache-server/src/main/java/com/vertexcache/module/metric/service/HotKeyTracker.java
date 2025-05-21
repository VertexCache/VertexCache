package com.vertexcache.module.metric.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

/**
 * Tracks access frequency for keys to support hot key detection.
 */
public class HotKeyTracker {

    private final ConcurrentHashMap<String, AtomicLong> accessCounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LongAdder> hotKeyCounts = new ConcurrentHashMap<>();


    public void recordAccess(String key) {
        accessCounts.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
    }

    /**
     * Returns the top N most accessed keys and their access counts.
     */
    public Map<String, Long> getTopKeys(int n) {
        return accessCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, AtomicLong>comparingByValue(Comparator.comparingLong(AtomicLong::get)).reversed())
                .limit(n)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, LongAdder> entry : hotKeyCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().sum());
        }
        return result;
    }

}
