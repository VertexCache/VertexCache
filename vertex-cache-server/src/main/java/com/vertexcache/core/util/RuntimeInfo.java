package com.vertexcache.core.util;

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