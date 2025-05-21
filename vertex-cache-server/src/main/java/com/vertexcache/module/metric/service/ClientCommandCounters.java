package com.vertexcache.module.metric.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class ClientCommandCounters {
    private final LongAdder get = new LongAdder();
    private final LongAdder set = new LongAdder();
    private final LongAdder del = new LongAdder();
    private Map<String, LongAdder> commandCounts;

    public void increment(String command) {
        switch (command.toLowerCase()) {
            case "get" -> get.increment();
            case "set" -> set.increment();
            case "del" -> del.increment();
        }
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> snapshot = new HashMap<>();
        for (Map.Entry<String, LongAdder> entry : commandCounts.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().sum());
        }
        return snapshot;
    }

    public long getGetCount() { return get.sum(); }
    public long getSetCount() { return set.sum(); }
    public long getDelCount() { return del.sum(); }
}
