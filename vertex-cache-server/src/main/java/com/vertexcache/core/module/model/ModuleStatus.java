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
 * Enum representing the current operational state of a VertexCache module.
 *
 * Tracks lifecycle and error states from initial load through shutdown.
 * Used by the ModuleHandler and monitoring tools to assess module health and readiness.
 *
 * Statuses include:
 * - ENABLED / DISABLED: Static configuration flag controlling whether the module should run
 *
 * - NOT_STARTED: Module has been registered but not yet started
 * - STARTUP_IN_PROGRESS: Module is currently initializing
 * - STARTUP_SUCCESSFUL: Module started successfully
 * - STARTUP_FAILED: Module failed to start properly
 * - STARTUP_STANDBY: Module initialized in standby mode (typically for clustering)
 *
 * - SHUTDOWN_SUCCESSFUL: Module shut down cleanly
 * - SHUTDOWN_FAILED: An error occurred during shutdown
 *
 * - ERROR_LOAD: Failure during module loading (e.g., classpath or config issue)
 * - ERROR_RUNTIME: Unhandled error occurred while running
 */
public enum ModuleStatus {
    ENABLED,
    DISABLED,

    NOT_STARTED,
    STARTUP_IN_PROGRESS,
    STARTUP_SUCCESSFUL,
    STARTUP_FAILED,
    STARTUP_STANDBY,
    SHUTDOWN_SUCCESSFUL,
    SHUTDOWN_FAILED,

    ERROR_LOAD,
    ERROR_RUNTIME
}
