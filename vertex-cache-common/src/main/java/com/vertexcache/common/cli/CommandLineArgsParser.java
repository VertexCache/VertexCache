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
package com.vertexcache.common.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing command-line arguments passed to the VertexCache application.
 *
 * This class is responsible for:
 *  - Extracting key-value pairs from raw CLI arguments
 *  - Providing simple lookup methods for specific flags or values
 *  - Supporting optional and required argument use cases
 *
 * Used during early application startup to capture override settings such as config paths,
 * debug modes, or environment targeting.
 */

public class CommandLineArgsParser {
    private final Map<String, String> keyValuePairs;

    public CommandLineArgsParser(String[] args) throws Exception {
        keyValuePairs = new HashMap<>();
        parseArgs(args);
    }

    private void parseArgs(String[] args) throws Exception {
        for (String arg : args) {
            String[] parts = arg.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                keyValuePairs.put(key, value);
            } else {
                throw new Exception("Invalid command line argument: " + arg);
            }
        }
    }

    public String getValue(String key) {
        return keyValuePairs.get(key);
    }

    public boolean isExist(String key) {
        return keyValuePairs.containsKey(key) && !keyValuePairs.get(key).isEmpty();
    }
}
