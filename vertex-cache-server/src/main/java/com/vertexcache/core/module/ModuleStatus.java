package com.vertexcache.core.module;

public enum ModuleStatus {
    ENABLED,
    DISABLED,

    NOT_STARTED,
    STARTUP_SUCCESSFUL,
    STARTUP_FAILED,
    SHUTDOWN_SUCCESSFUL,
    SHUTDOWN_FAILED,

    ERROR_LOAD,
    ERROR_RUNTIME
}
