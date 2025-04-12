package com.vertexcache.core.module;

public enum ModuleStatus {
    // Enable or Not for Config
    ENABLED,
    DISABLED,

    // Running State
    NOT_STARTED,
    STARTUP_SUCCESSFUL,
    SHUTDOWN_SUCCESSFUL,
    ERROR_LOAD,
    ERROR_RUNTIME
}

