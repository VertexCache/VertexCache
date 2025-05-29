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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for loading and resolving environment variables used in VertexCache configuration.
 */
public class EnvLoader implements ConfigLoader {
    private final Map<String, String> envVariables;

    public EnvLoader() {
        this.envVariables = new LinkedHashMap<>();
    }

    @Override
    public boolean loadFromPath(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder multilineValue = new StringBuilder();
            String lastKey = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Handle continuation of a multiline value
                if (lastKey != null) {
                    if (line.endsWith("\\")) {
                        multilineValue.append(line, 0, line.length() - 1).append("\n");
                    } else {
                        multilineValue.append(line);
                        String fullValue = multilineValue.toString();
                        envVariables.put(lastKey, removeSurroundingQuotes(fullValue));
                        lastKey = null;
                        multilineValue.setLength(0);
                    }
                    continue;
                }

                // New key=value pair
                int delimiterIndex = line.indexOf('=');
                if (delimiterIndex == -1) {
                    continue;
                }

                String key = line.substring(0, delimiterIndex).trim();
                String value = line.substring(delimiterIndex + 1).trim();

                value = removeSurroundingQuotes(value);

                if (value.endsWith("\\")) {
                    lastKey = key;
                    multilineValue.append(value, 0, value.length() - 1).append("\n");
                } else {
                    envVariables.put(key, value);
                }
            }

            // Final unterminated multiline value
            if (lastKey != null && multilineValue.length() > 0) {
                String fullValue = multilineValue.toString();
                envVariables.put(lastKey, removeSurroundingQuotes(fullValue));
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isExist(String key) {
        return envVariables.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        return envVariables.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return envVariables.getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, String> getAllProperties() {
        return new LinkedHashMap<>(envVariables); // Preserve order
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


    private String removeSurroundingQuotes(String input) {
        if ((input.startsWith("\"") && input.endsWith("\"")) ||
                (input.startsWith("'") && input.endsWith("'"))) {
            input = input.substring(1, input.length() - 1);
        }

        return input
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }
}
