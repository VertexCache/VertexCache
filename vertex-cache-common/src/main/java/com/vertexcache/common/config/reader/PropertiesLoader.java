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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader implements ConfigLoader {

    private Properties properties;

    public PropertiesLoader() {
        this.properties = new Properties();
    }

    public boolean loadFromPath(String filePath) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            this.properties.load(input);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isExist(String key) {
        return this.properties.containsKey(key);
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public Map<String, String> getAllProperties() {
        Map<String, String> map = new java.util.LinkedHashMap<>();
        for (String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name));
        }
        return map;
    }

    @Override
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

}
