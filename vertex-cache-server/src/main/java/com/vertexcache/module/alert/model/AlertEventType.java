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

/**
 * AlertEventType defines the set of alert categories that can be triggered
 * within the VertexCache system. Each enum value represents a specific type
 * of alert condition, such as memory pressure, node failure, or rate limit breaches.
 *
 * These event types are used to classify and route AlertEvents to the appropriate
 * handlers, such as logging subsystems or external webhook integrations.
 */
public enum AlertEventType {
    PRIMARY_PROMOTED,
    PRIMARY_DEMOTED,
    NODE_UNREACHABLE,
    NODE_RECOVERED,
    DATA_CORRUPTION_DETECTED,
    CONFIG_HASH_MISMATCH,
    AUTH_FAILURE,
    MEMORY_USAGE_HIGH,
    DISK_USAGE_HIGH,

    HOT_KEY_ALERT,
    KEY_CHURN,
    UNAUTHORIZED_ACCESS_ATTEMPT,
    HOT_KEY_ANOMALY
}