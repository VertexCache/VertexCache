package com.vertexcache.core.module;

import com.vertexcache.core.setting.Config;

public interface ModuleHandler {
    void start();
    void stop();
    String getStatusSummary();
}
