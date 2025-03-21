package com.vertexcache.common.config.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lightweight .env file loader to avoid additional dependencies.
 */
public class EnvLoader implements ConfigLoader {
    private final Map<String, String> envVariables;

    public EnvLoader() {
        this.envVariables = new LinkedHashMap<>(); // Preserves insertion order
    }

    @Override
    public boolean loadFromPath(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder multilineValue = new StringBuilder();
            String lastKey = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Handle multiline values (lines ending with \)
                if (lastKey != null && line.endsWith("\\")) {
                    multilineValue.append(line, 0, line.length() - 1).append("\n");
                    continue;
                } else if (lastKey != null) {
                    multilineValue.append(line);
                    envVariables.put(lastKey, multilineValue.toString()); // Save multiline value
                    lastKey = null;
                    multilineValue.setLength(0); // Reset buffer
                    continue;
                }

                // Find first '=' to avoid regex overhead
                int delimiterIndex = line.indexOf('=');
                if (delimiterIndex == -1) {
                    System.err.println("Skipping invalid line: " + line);
                    continue; // Ignore malformed lines
                }

                // Extract and trim key and value
                String key = line.substring(0, delimiterIndex).trim();
                String value = line.substring(delimiterIndex + 1).trim();

                // Handle quoted values correctly
                value = removeSurroundingQuotes(value);

                // Store key without lowercasing (case-sensitive support)
                envVariables.put(key, value);

                // Check if value starts a multiline entry
                if (value.endsWith("\\")) {
                    lastKey = key;
                    multilineValue.append(value, 0, value.length() - 1).append("\n");
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
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

    public void printAllKeysAndValues() {
        System.out.println("Loaded Environment Variables:");
        envVariables.forEach((key, value) ->
                System.out.println(key + " = " + value));
    }

    private String removeSurroundingQuotes(String input) {
        if (input.startsWith("\"") && input.endsWith("\"") || input.startsWith("'") && input.endsWith("'")) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }
}
