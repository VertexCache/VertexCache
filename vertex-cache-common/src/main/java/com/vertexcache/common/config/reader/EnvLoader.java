package com.vertexcache.common.config.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
                        multilineValue.append(line, 0, line.length() - 1).append("\\n");
                    } else {
                        multilineValue.append(line);
                        String fullValue = multilineValue.toString();
                        envVariables.put(lastKey, removeSurroundingQuotes(fullValue));
                        printDebug(lastKey, fullValue);
                        lastKey = null;
                        multilineValue.setLength(0);
                    }
                    continue;
                }

                // New key=value pair
                int delimiterIndex = line.indexOf('=');
                if (delimiterIndex == -1) {
                    System.err.println("⚠️ Skipping invalid line: " + line);
                    continue;
                }

                String key = line.substring(0, delimiterIndex).trim();
                String value = line.substring(delimiterIndex + 1).trim();

                if (value.endsWith("\\")) {
                    lastKey = key;
                    multilineValue.append(value, 0, value.length() - 1).append("\\n");
                } else {
                    envVariables.put(key, removeSurroundingQuotes(value));
                    printDebug(key, value);
                }
            }

            // Final unterminated multiline value
            if (lastKey != null && multilineValue.length() > 0) {
                String fullValue = multilineValue.toString();
                envVariables.put(lastKey, removeSurroundingQuotes(fullValue));
                printDebug(lastKey, fullValue);
            }

            return true;
        } catch (IOException e) {
            System.err.println("❌ Error reading .env file: " + e.getMessage());
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
        System.out.println("🔎 Loaded Environment Variables:");
        envVariables.forEach(this::printDebug);
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

    private void printDebug(String key, String value) {
        /*
        System.out.println("✅ Loaded ENV key: " + key);
        System.out.println("🔹 Value (first 80 chars): " +
                (value.length() > 80 ? value.substring(0, 80) + "..." : value));
        System.out.println("🔹 Contains BEGIN: " + value.contains("-----BEGIN"));
        System.out.println("🔹 Contains END: " + value.contains("-----END"));
        System.out.println("🔹 Contains newline: " + value.contains("\n"));
        System.out.println("🔹 Total length: " + value.length());
        System.out.println();
        */
    }
}

