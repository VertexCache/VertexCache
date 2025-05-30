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
package com.vertexcache.core.util.runtime;

/**
 * Lightweight utility class for tracking VertexCache process runtime.
 *
 * Captures the server startup timestamp and provides methods to calculate
 * current uptime in milliseconds.
 *
 * Designed to support diagnostics, logging, and system health reporting without
 * introducing additional dependencies or state.
 *
 * This class is static-only and cannot be instantiated.
 */
public final class RuntimeInfo {
    private static final long STARTUP_TIME_MILLIS = System.currentTimeMillis();

    private RuntimeInfo() {} // prevent instantiation

    public static long getStartupTimeMillis() {
        return STARTUP_TIME_MILLIS;
    }

    public static long getUptimeMillis() {
        return System.currentTimeMillis() - STARTUP_TIME_MILLIS;
    }
}