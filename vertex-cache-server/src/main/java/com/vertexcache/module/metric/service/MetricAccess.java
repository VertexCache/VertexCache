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
package com.vertexcache.module.metric.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.util.RuntimeInfo;
import com.vertexcache.module.metric.MetricModule;
import com.vertexcache.module.metric.model.MetricName;
import com.vertexcache.module.metric.model.MetricViewKey;

import java.util.*;

public class MetricAccess {

    private MetricRegistry metricRegistry;
    private MetricCollector metricCollector;
    private MetricAnalysisHelper metricAnalysisHelper;
    private HotKeyTracker hotKeyTracker;
    private ClientCommandCounters clientCommandCounters;

    public void clearAll() {
        metricCollector = null;
        metricAnalysisHelper = null;
        hotKeyTracker = null;
        clientCommandCounters = null;
    }

    public Map<String, Object> getCommandUsageView() {
        Map<String, Object> view = new HashMap<>();
        Optional<MetricModule> optMetricModule = ModuleRegistry.getInstance().getModule(MetricModule.class);
        MetricModule metricModule = optMetricModule.get();
        view.put(MetricViewKey.COMMAND_GET_TOTAL, metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_GET_TOTAL));
        view.put(MetricViewKey.COMMAND_SET_TOTAL, metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_SET_TOTAL));
        view.put(MetricViewKey.COMMAND_DEL_TOTAL, metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_DEL_TOTAL));
        return view;
    }

    public Map<String, Object> getCacheEffectivenessView() {
        Map<String, Object> view = new HashMap<>();
        Optional<MetricModule> optMetricModule = ModuleRegistry.getInstance().getModule(MetricModule.class);
        MetricModule metricModule = optMetricModule.get();
        long hitCount = metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_HIT_COUNT);
        long missCount = metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_MISS_COUNT);
        long keyCount = metricModule.getMetricAccess().getMetricCollector().getGauge(MetricName.CACHE_KEY_COUNT);
        long total = hitCount + missCount;
        double hitRatio = (total > 0) ? ((double) hitCount / total) : 0.0;
        String formattedPercent = String.format("%.1f%%", hitRatio * 100); // for 63.6%
        view.put(MetricViewKey.CACHE_HIT_COUNT, hitCount);
        view.put(MetricViewKey.CACHE_MISS_COUNT, missCount);
        view.put(MetricViewKey.CACHE_HIT_RATIO, formattedPercent);
        view.put(MetricViewKey.CACHE_KEY_COUNT, keyCount);
        return view;
    }

    public Map<String, Object> getIndexUsageView() {
        Map<String, Object> view = new HashMap<>();
        Optional<MetricModule> optMetricModule = ModuleRegistry.getInstance().getModule(MetricModule.class);
        MetricModule metricModule = optMetricModule.get();
        view.put(MetricViewKey.INDEX_USAGE_IDX1, metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_INDEX_USAGE_IDX1));
        view.put(MetricViewKey.INDEX_USAGE_IDX2, metricModule.getMetricAccess().getMetricCollector().getCounter(MetricName.CACHE_INDEX_USAGE_IDX2));
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
            view.put(MetricViewKey.HOT_KEY_PREFIX + rank, entry.getKey());
            view.put(MetricViewKey.HOT_HITS_PREFIX + rank, entry.getValue());
            rank++;
        }

        return view;
    }

    public Map<String, Object> getJvmMemoryView() {
        Map<String, Object> view = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        view.put(MetricViewKey.JVM_MEMORY_USED_MB, bytesToMb(usedMemory));
        view.put(MetricViewKey.JVM_MEMORY_FREE_MB, bytesToMb(freeMemory));
        view.put(MetricViewKey.JVM_MEMORY_MAX_MB, bytesToMb(maxMemory));
        view.put(MetricViewKey.JVM_MEMORY_ALLOCATED_MB, bytesToMb(totalMemory));

        view.put(MetricViewKey.JVM_UPTIME, formatUptime(System.currentTimeMillis() - RuntimeInfo.getStartupTimeMillis()));

        return view;
    }

    private int bytesToMb(long bytes) {
        return (int) (bytes / (1024 * 1024));
    }

    public Map<String, Object> getFullMetricSnapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>(); // preserve order for flat/pretty views

        snapshot.put("command_usage", getCommandUsageView());
        snapshot.put("cache_effectiveness", getCacheEffectivenessView());
        snapshot.put("index_usage", getIndexUsageView());
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

    @SuppressWarnings("unchecked")
    private void appendPrettySection(StringBuilder sb, String title, Object section) {
        if (!(section instanceof Map<?, ?> map)) return;

        sb.append("=== ").append(title).append(" ===\n");
        if (map.isEmpty()) {
            sb.append("(none)\n\n");
            return;
        }

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
        List<String> lines = new ArrayList<>();

        for (Map.Entry<String, Object> top : snapshot.entrySet()) {
            String groupKey = top.getKey();
            Object value = top.getValue();

            if (value instanceof Map<?, ?> nested) {
                flattenVcmp(groupKey, nested, lines);
            } else {
                lines.add("#" + groupKey + "=" + value);
            }
        }

        sb.append("[").append(lines.size()).append("\n");
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    private void flattenVcmp(String parent, Map<?, ?> map, List<String> lines) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            String flatKey = parent + "." + key;

            if (flatKey.startsWith("jvm_memory.memory.") && flatKey.endsWith(".bytes")) {
                long bytes = (value instanceof Number) ? ((Number) value).longValue() : 0L;
                long mb = bytes / (1024 * 1024);
                String vcmpKey = "#memory_" + key.replace(".bytes", "_mb");
                lines.add(vcmpKey + "=" + mb);
            } else {
                lines.add("#" + flatKey + "=" + value);
            }
        }
    }

    private String formatUptime(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        days %= 365;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append(" year").append(years > 1 ? "s " : " ");
        if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append(" second").append(seconds != 1 ? "s" : "");

        return sb.toString().trim();
    }

    public void setMetricRegistry(MetricRegistry metricRegistry) {this.metricRegistry = metricRegistry;}
    public void setMetricCollector(MetricCollector metricCollector) {this.metricCollector = metricCollector;}
    public void setMetricAnalysisHelper(MetricAnalysisHelper metricAnalysisHelper) {this.metricAnalysisHelper = metricAnalysisHelper;}
    public void setHotKeyTracker(HotKeyTracker tracker) {this.hotKeyTracker = tracker;}
    public void setClientCommandCounters(ClientCommandCounters clientCommandCounters) {this.clientCommandCounters = clientCommandCounters;}

    public MetricRegistry getMetricRegistry() {return metricRegistry;}
    public MetricCollector getMetricCollector() {return metricCollector;}
    public MetricAnalysisHelper getMetricAnalysisHelper() {return metricAnalysisHelper;}
    public HotKeyTracker getHotKeyTracker() {return hotKeyTracker;}
    public ClientCommandCounters getClientCommandCounters() {return clientCommandCounters;}
}
