package com.vertexcache.core.setting.loader;

import com.vertexcache.common.config.reader.ConfigLoader;

abstract public class LoaderBase {

    private ConfigLoader configLoader;

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    protected ConfigLoader getConfigLoader() {
        return configLoader;
    }

    abstract public void load();
}
