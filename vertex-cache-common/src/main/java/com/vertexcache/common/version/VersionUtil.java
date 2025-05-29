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
package com.vertexcache.common.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for retrieving the application version from the JAR manifest or POM metadata.
 *
 * This class is useful for:
 *  - Extracting the implementation version embedded in the built JAR's manifest
 *  - Providing a consistent way to expose version information programmatically
 *  - Supporting diagnostic commands and API endpoints that report application version
 *
 * Note: The version is typically injected during build time from the Maven POM.
 * If unavailable, fallback behavior may return null or a default.
 */
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
