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
package com.vertexcache.core.setting.loaders;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.model.EvictionPolicy;
import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

/**
 * Configuration loader responsible for parsing and validating cache-related settings.
 *
 * Loads core cache options such as eviction policy (e.g., LRU, LFU, ARC).
 *
 * Ensures that the cache engine is initialized with consistent and valid parameters,
 * and that default values are applied where necessary.
 *
 * This loader runs early in the startup process to guarantee correct cache behavior
 * before any data is written or retrieved.
 */
public class CacheConfigLoader extends LoaderBase {

    private static int DEFAULT_CACHE_SIZE=1000000;

    private EvictionPolicy cacheEvictionPolicy = EvictionPolicy.NONE;
    private int cacheSize;

    public CacheConfigLoader() {
    }

    @Override
    public void load() {
        this.cacheEvictionPolicy = EvictionPolicy.NONE;
        if (this.getConfigLoader().isExist(ConfigKey.CACHE_EVICTION)) {
            try {
                this.cacheEvictionPolicy = EvictionPolicy.fromString(this.getConfigLoader().getProperty(ConfigKey.CACHE_EVICTION));
            } catch (IllegalArgumentException ie) {
                LogHelper.getInstance().logWarn("Invalid eviction policy given, defaulting to NONE");
                this.cacheEvictionPolicy = EvictionPolicy.NONE;
            }
        } else {
            LogHelper.getInstance().logWarn("Non-existent eviction policy given, defaulting to NONE");
        }

        // Cache Size, applied when Eviction Policy is not set to NONE
        this.cacheSize = DEFAULT_CACHE_SIZE;
        if (this.getConfigLoader().isExist(ConfigKey.CACHE_SIZE)) {
            long cacheSize = Long.parseLong(this.getConfigLoader().getProperty(ConfigKey.CACHE_SIZE));
            if (cacheSize <= Integer.MAX_VALUE) {
                this.cacheSize = (int) cacheSize;
            } else {
                LogHelper.getInstance().logWarn("Cache maximum size exceeded, defaulting to " + DEFAULT_CACHE_SIZE);
            }
        } else {
            LogHelper.getInstance().logWarn("Non-existent cache size, defaulting to " + DEFAULT_CACHE_SIZE);
        }
    }

    public void loadCacheSettings() {
        this.cacheEvictionPolicy = EvictionPolicy.NONE;
        if (this.getConfigLoader().isExist(ConfigKey.CACHE_EVICTION)) {
            try {
                this.cacheEvictionPolicy = EvictionPolicy.fromString(this.getConfigLoader().getProperty(ConfigKey.CACHE_EVICTION));
            } catch (IllegalArgumentException ignored) {}
        }

        this.cacheSize = DEFAULT_CACHE_SIZE;
        if (this.getConfigLoader().isExist(ConfigKey.CACHE_SIZE)) {
            long cacheSize = Long.parseLong(this.getConfigLoader().getProperty(ConfigKey.CACHE_SIZE));
            if (cacheSize <= Integer.MAX_VALUE) {
                this.cacheSize = (int) cacheSize;
            }
        }
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
