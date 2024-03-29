package com.vertexcache.common.cli;

import java.util.HashMap;
import java.util.Map;

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
