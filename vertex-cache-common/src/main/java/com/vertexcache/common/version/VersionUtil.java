package com.vertexcache.common.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {
    public static String getAppVersion() {
        Properties properties = new Properties();
        try (InputStream input = VersionUtil.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input == null) {
                return "Unknown";
            }
            properties.load(input);
            return properties.getProperty("app.version", "Unknown");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Unknown";
        }
    }
}
