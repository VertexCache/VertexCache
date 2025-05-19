package com.vertexcache.module.alert.model;

import java.time.Instant;
import java.util.Map;

public class AlertEvent {
    private final AlertEventType event;
    private final String timestamp;
    private final String node;
    private final Map<String, Object> details;

    public AlertEvent(AlertEventType event, String node, Map<String, Object> details) {
        this.event = event;
        this.timestamp = Instant.now().toString();
        this.node = node;
        this.details = details;
    }

    public AlertEvent(AlertEventType event, String node) {
        this(event, node, Map.of());
    }

    public AlertEventType getEvent() { return event; }
    public String getTimestamp() { return timestamp; }
    public String getNode() { return node; }
    public Map<String, Object> getDetails() { return details; }
}