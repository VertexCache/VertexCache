package com.vertexcache.core.setting;

import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.EvictionPolicy;

public class ConfigCache {

    private static int DEFAULT_CACHE_SIZE=1000000;

    private EvictionPolicy cacheEvictionPolicy = EvictionPolicy.NONE;
    private int cacheSize;

    private ConfigLoader configLoader;

    public ConfigCache() {
    }

    protected void loadFromConfigLoader() {
        this.cacheEvictionPolicy = EvictionPolicy.NONE;
        if (configLoader.isExist(ConfigKey.CACHE_EVICTION)) {
            try {
                this.cacheEvictionPolicy = EvictionPolicy.fromString(configLoader.getProperty(ConfigKey.CACHE_EVICTION));
            } catch (IllegalArgumentException ie) {
                LogHelper.getInstance().logWarn("Invalid eviction policy given, defaulting to NONE");
                this.cacheEvictionPolicy = EvictionPolicy.NONE;
            }
        } else {
            LogHelper.getInstance().logWarn("Non-existent eviction policy given, defaulting to NONE");
        }

        // Cache Size, applied when Eviction Policy is not set to NONE
        this.cacheSize = DEFAULT_CACHE_SIZE;
        if (configLoader.isExist(ConfigKey.CACHE_SIZE)) {
            long cacheSize = Long.parseLong(configLoader.getProperty(ConfigKey.CACHE_SIZE));
            if (cacheSize <= Integer.MAX_VALUE) {
                this.cacheSize = (int) cacheSize;
            } else {
                LogHelper.getInstance().logWarn("Cache maximum size exceeded, defaulting to " + DEFAULT_CACHE_SIZE);
            }
        } else {
            LogHelper.getInstance().logWarn("Non-existent cache size, defaulting to " + DEFAULT_CACHE_SIZE);
        }
    }

    protected void loadCacheSettings() {
        this.cacheEvictionPolicy = EvictionPolicy.NONE;
        if (configLoader.isExist(ConfigKey.CACHE_EVICTION)) {
            try {
                this.cacheEvictionPolicy = EvictionPolicy.fromString(configLoader.getProperty(ConfigKey.CACHE_EVICTION));
            } catch (IllegalArgumentException ignored) {}
        }

        this.cacheSize = DEFAULT_CACHE_SIZE;
        if (configLoader.isExist(ConfigKey.CACHE_SIZE)) {
            long cacheSize = Long.parseLong(configLoader.getProperty(ConfigKey.CACHE_SIZE));
            if (cacheSize <= Integer.MAX_VALUE) {
                this.cacheSize = (int) cacheSize;
            }
        }
    }

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public EvictionPolicy getCacheEvictionPolicy() {
        return cacheEvictionPolicy;
    }

    public void setCacheEvictionPolicy(EvictionPolicy cacheEvictionPolicy) {
        this.cacheEvictionPolicy = cacheEvictionPolicy;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
}
