package com.vertexcache.core.module;

import java.time.Instant;

public record ModuleSnapshot(
        String name,
        ModuleStatus status,
        String runtimeStatus,
        String message,
        Instant timestamp
) {}
