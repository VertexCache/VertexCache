package com.vertexcache.module.metric.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertexcache.module.metric.model.MetricKey;
import com.vertexcache.module.metric.model.MetricName;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public class MetricAccess {

    private MetricRegistry metricRegistry;
    private MetricCollector metricCollector;
    private MetricAnalysisHelper metricAnalysisHelper;
    private HotKeyTracker hotKeyTracker;
    private ClientCommandCounters clientCommandCounters;

    public MetricRegistry getMetricRegistry() {return metricRegistry;}
    public void setMetricRegistry(MetricRegistry metricRegistry) {this.metricRegistry = metricRegistry;}
    public void setMetricCollector(MetricCollector metricCollector) {this.metricCollector = metricCollector;}
    public MetricCollector getMetricCollector() {
        return metricCollector;
    }
    public void setMetricAnalysisHelper(MetricAnalysisHelper metricAnalysisHelper) {this.metricAnalysisHelper = metricAnalysisHelper;}
    public MetricAnalysisHelper getMetricAnalysisHelper() {
        return metricAnalysisHelper;
    }
    public void setHotKeyTracker(HotKeyTracker tracker) {
        this.hotKeyTracker = tracker;
    }
    public HotKeyTracker getHotKeyTracker() {
        return hotKeyTracker;
    }
    public void setClientCommandCounters(ClientCommandCounters clientCommandCounters) {this.clientCommandCounters = clientCommandCounters;}
    public ClientCommandCounters getClientCommandCounters() {return clientCommandCounters;}

    public void clearAll() {
        metricCollector = null;
        metricAnalysisHelper = null;
        hotKeyTracker = null;
        clientCommandCounters = null;
    }

    public Map<String, Object> getCommandUsageView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        long totalGet = 0;
        long totalSet = 0;
        long totalDel = 0;

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();
            long count = entry.getValue().sum();

            switch (metricName) {
                case "commands.get.total" -> totalGet += count;
                case "commands.set.total" -> totalSet += count;
                case "commands.del.total" -> totalDel += count;
            }
        }

        view.put("commands.get.total", totalGet);
        view.put("commands.set.total", totalSet);
        view.put("commands.del.total", totalDel);
        return view;
    }

    public Map<String, Object> getCacheEffectivenessView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        long hitCount = 0;
        long missCount = 0;

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();
            long count = entry.getValue().sum();

            switch (metricName) {
                case "cache.hit.count" -> hitCount += count;
                case "cache.miss.count" -> missCount += count;
            }
        }

        long total = hitCount + missCount;
        double hitRatio = (total > 0) ? ((double) hitCount / total) : 0.0;

        view.put("cache.hit.count", hitCount);
        view.put("cache.miss.count", missCount);
        view.put("cache.hit.ratio", hitRatio);

        return view;
    }

    public Map<String, Object> getIndexUsageView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        long idx1Count = 0;
        long idx2Count = 0;

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();
            long count = entry.getValue().sum();

            switch (metricName) {
                case "cache.index.usage.idx1" -> idx1Count += count;
                case "cache.index.usage.idx2" -> idx2Count += count;
            }
        }

        view.put("cache.index.usage.idx1", idx1Count);
        view.put("cache.index.usage.idx2", idx2Count);

        return view;
    }

    public Map<String, Object> getTtlDistributionView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();

            if (metricName.startsWith(MetricName.TTL_DISTRIBUTION_PREFIX)) {
                long count = entry.getValue().sum();
                view.put(metricName, count);
            }
        }

        return view;
    }

    public Map<String, Object> getValueSizeDistributionView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();

            if (metricName.startsWith("cache.value.size.")) {
                long count = entry.getValue().sum();
                String shortName = metricName.substring("cache.value.size.".length());
                view.put(shortName, count);
            }
        }

        return view;
    }

    public Map<String, Object> getHotKeysView() {
        return getHotKeysView(10);
    }

    public Map<String, Object> getHotKeysView(int limit) {
        Map<String, Object> view = new LinkedHashMap<>(); // preserve order
        Map<String, Long> hotKeys = hotKeyTracker.getTopKeys(limit);

        int rank = 1;
        for (Map.Entry<String, Long> entry : hotKeys.entrySet()) {
            view.put("key." + rank, entry.getKey());
            view.put("hits." + rank, entry.getValue());
            rank++;
        }

        return view;
    }

    public Map<String, Object> getAvgValueSizeView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        long avgSize = 0;

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();
            if ("cache.avg.value.size.bytes".equals(metricName)) {
                avgSize = entry.getValue().sum(); // assume it’s pre-derived
                break;
            }
        }

        view.put("cache.avg.value.size.bytes", avgSize);
        return view;
    }


    public Map<String, Object> getCacheLifecycleStatsView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = metricRegistry.getKeyCounters();

        long evictions = 0;
        long expired = 0;
        long keyCount = 0;

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            String metricName = entry.getKey().value();
            long count = entry.getValue().sum();

            switch (metricName) {
                case "cache.evictions.total" -> evictions += count;
                case "cache.expired.total"   -> expired += count;
                case "cache.key.count"       -> keyCount = count; // it's a gauge, just overwrite
            }
        }

        view.put("cache.evictions.total", evictions);
        view.put("cache.expired.total", expired);
        view.put("cache.key.count", keyCount);

        return view;
    }

    public Map<String, Object> getJvmMemoryView() {
        Map<String, Object> view = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();

        view.put("memory.used.bytes", usedMemory);
        view.put("memory.free.bytes", freeMemory);
        view.put("memory.total.allocated.bytes", totalMemory);
        view.put("memory.max.bytes", maxMemory);

        return view;
    }

    public Map<String, Object> getFullMetricSnapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>(); // preserve order for flat/pretty views

        snapshot.put("command_usage", getCommandUsageView());
        snapshot.put("cache_effectiveness", getCacheEffectivenessView());
        snapshot.put("cache_lifecycle", getCacheLifecycleStatsView());
        snapshot.put("index_usage", getIndexUsageView());
        snapshot.put("ttl_distribution", getTtlDistributionView());
        snapshot.put("value_size_distribution", getValueSizeDistributionView());
        snapshot.put("avg_value_size", getAvgValueSizeView());
        snapshot.put("hot_keys", getHotKeysView(10));
        snapshot.put("jvm_memory", getJvmMemoryView());

        return snapshot;
    }

    public String toJson() {
        try {
            // Jackson or other JSON lib
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getFullMetricSnapshot());
        } catch (Exception e) {
            return "{\"error\": \"Failed to serialize metrics\"}";
        }
    }

    public String toSummaryAsPretty() {
        Map<String, Object> snapshot = getFullMetricSnapshot();
        StringBuilder sb = new StringBuilder();

        appendPrettySection(sb, "COMMAND USAGE", snapshot.get("command_usage"));
        appendPrettySection(sb, "CACHE EFFECTIVENESS", snapshot.get("cache_effectiveness"));
        appendPrettySection(sb, "CACHE LIFECYCLE", snapshot.get("cache_lifecycle"));
        appendPrettySection(sb, "INDEX USAGE", snapshot.get("index_usage"));
        appendPrettySection(sb, "TTL DISTRIBUTION", snapshot.get("ttl_distribution"));
        appendPrettySection(sb, "VALUE SIZE DISTRIBUTION", snapshot.get("value_size_distribution"));
        appendPrettySection(sb, "AVERAGE VALUE SIZE", snapshot.get("avg_value_size"));
        appendPrettySection(sb, "HOT KEYS", snapshot.get("hot_keys"));
        appendPrettySection(sb, "JVM MEMORY", snapshot.get("jvm_memory"));

        return sb.toString();
    }

    private void appendPrettySection(StringBuilder sb, String title, Object section) {
        if (!(section instanceof Map<?, ?> map)) return;

        sb.append("=== ").append(title).append(" ===\n");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(capitalize(entry.getKey().toString().replace("_", "")))
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        sb.append("\n");
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public String toSummaryAsFlat() {
        Map<String, Object> snapshot = getFullMetricSnapshot();
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> top : snapshot.entrySet()) {
            String groupKey = top.getKey();
            Object value = top.getValue();

            if (value instanceof Map<?, ?> nested) {
                flattenVcmp(groupKey, nested, sb);
            } else {
                sb.append("#").append(groupKey).append("=").append(value).append("\n");
            }
        }

        return sb.toString();
    }

    private void flattenVcmp(String parent, Map<?, ?> map, StringBuilder sb) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            String flatKey = parent + "." + key;

            // Special handling: format memory in megabytes
            if (flatKey.startsWith("jvm_memory.memory.") && flatKey.endsWith(".bytes")) {
                long bytes = (value instanceof Number) ? ((Number) value).longValue() : 0L;
                long mb = bytes / (1024 * 1024);
                String vcmpKey = "#memory_" + key.replace(".bytes", "_mb");
                sb.append(vcmpKey).append("=").append(mb).append("\n");
            } else {
                sb.append("#").append(flatKey).append("=").append(value).append("\n");
            }
        }
    }

}