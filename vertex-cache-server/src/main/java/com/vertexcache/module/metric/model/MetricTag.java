package com.vertexcache.module.metric.model;

public enum MetricTag {
    CORE("core"),
    TTL("ttl"),
    VALUE("value"),
    INDEX("index"),
    HOTKEYS("hotkeys");

    private final String label;

    MetricTag(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static MetricTag fromLabel(String label) {
        for (MetricTag tag : values()) {
            if (tag.label.equals(label)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown tag: " + label);
    }
}