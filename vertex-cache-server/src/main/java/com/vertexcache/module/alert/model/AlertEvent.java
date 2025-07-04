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
package com.vertexcache.module.alert.model;

import java.time.Instant;
import java.util.Map;

/**
 * AlertEvent represents a triggered alert within the VertexCache system.
 * It encapsulates details such as the alert type, timestamp, triggering context,
 * and any associated metadata needed for downstream processing or dispatch.
 *
 * This class is used internally to pass alert information between components,
 * such as from monitoring modules to webhook dispatchers or logging systems.
 */
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