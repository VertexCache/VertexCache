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
package com.vertexcache.common.config.reader;

import java.util.Map;

public interface ConfigLoader {
    boolean loadFromPath(String filePath);
    boolean isExist(String key);
    String getProperty(String key);
    String getProperty(String key, String defaultValue);
    Map<String, String> getAllProperties();
    int getIntProperty(String key, int defaultValue);
    boolean getBooleanProperty(String key, boolean defaultValue);
}

