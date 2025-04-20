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

