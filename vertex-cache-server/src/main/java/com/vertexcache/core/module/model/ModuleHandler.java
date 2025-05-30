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
 * Manager responsible for loading, initializing, and coordinating all VertexCache modules.
 *
 * Discovers module implementations at startup and invokes their lifecycle methods
 * (e.g., start, shutdown) in a controlled and predictable order.
 *
 * Also handles inter-module dependencies, configuration propagation, and error isolation.
 *
 * Ensures consistent behavior and modularity across all pluggable components in the system.
 */
public interface ModuleHandler {
    void start();
    void stop();
    String getStatusSummary();
}
