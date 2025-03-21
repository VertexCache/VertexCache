package com.vertexcache.common.config.reader;

import java.io.FileInputStream;
import java.io.IOException;
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

}
