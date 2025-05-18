package com.vertexcache.module.alert.model;

import java.time.Instant;
import java.util.Map;

public class AlertEvent {
    private final String event;
    private final Instant timestamp;
    private final String node;
    private final String clusterId;
    private final Map<String, Object> details;

    public AlertEvent(String event, String node, String clusterId, Map<String, Object> details) {
        this.event = event;
        this.timestamp = Instant.now();
        this.node = node;
        this.clusterId = clusterId;
        this.details = details;
    }

    public String getEvent() { return event; }
    public Instant getTimestamp() { return timestamp; }
    public String getNode() { return node; }
    public String getClusterId() { return clusterId; }
    public Map<String, Object> getDetails() { return details; }
}
