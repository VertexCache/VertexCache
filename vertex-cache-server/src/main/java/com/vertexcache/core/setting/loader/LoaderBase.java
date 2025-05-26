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
