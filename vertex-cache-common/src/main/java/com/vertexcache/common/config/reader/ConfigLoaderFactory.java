package com.vertexcache.common.config.reader;

public class ConfigLoaderFactory {
    public static ConfigLoader getLoader(String filePath) {
        if (filePath.contains(".env")) {
            return new EnvLoader();
        } else if (filePath.endsWith(".properties")) {
            return new PropertiesLoader();
        }
        throw new IllegalArgumentException("Unsupported file type: " + filePath);
    }
}
