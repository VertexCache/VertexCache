package com.vertexcache.common.config.reader;

public interface ConfigLoader {
    boolean loadFromPath(String filePath);
    boolean isExist(String key);
    String getProperty(String key);
}

