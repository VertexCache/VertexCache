package com.vertexcache.core.setting.loader;

import com.vertexcache.common.config.reader.ConfigLoader;

public abstract class LoaderBase<T extends LoaderBase<T>> {

    private ConfigLoader configLoader;

    @SuppressWarnings("unchecked")
    public T setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        return (T) this;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public abstract void load();
}
