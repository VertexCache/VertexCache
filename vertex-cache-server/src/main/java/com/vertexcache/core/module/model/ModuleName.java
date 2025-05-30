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
package com.vertexcache.core.module.model;

/**
 * Enum representing the unique names of all recognized modules in VertexCache.
 *
 * Used for identifying, initializing, and managing module components across
 * configuration files, logs, and runtime orchestration.
 *
 * Each entry corresponds to a core subsystem, such as:
 * - CORE: Core caching and command services
 * - CLUSTER: Cluster coordination and failover logic
 * - ALERT: Alerting and webhook dispatching
 * - METRIC: Metric collection and reporting
 * - REST_API: RESTful API interface
 * - SMART: Smart index handling and data sweeper logic
 *
 * Enables structured module registration and status tracking.
 */
public enum ModuleName {
    AUTH("AuthModule"),
    RATELIMITER("RateLimiterModule"),
    METRIC("MetricModule"),
    REST_API("RestApiModule"),
    CLUSTER("ClusterModule"),
    ADMIN("AdminModule"),
    ALERT("AlertModule"),
    SMART("SmartModule");
    //EXPORTER("ExporterModule");

    private final String value;

    ModuleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
