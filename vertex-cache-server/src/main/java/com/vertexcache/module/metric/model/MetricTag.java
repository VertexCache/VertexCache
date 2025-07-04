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
package com.vertexcache.module.metric.model;

/**
 * Enumeration of metric tag categories used to classify different types of metrics
 * within the system. Tags include core operations, TTL distribution, value analysis,
 * index usage, and hot key tracking.
 *
 * Provides a label for each tag and a utility method to resolve tags from string labels.
 */
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