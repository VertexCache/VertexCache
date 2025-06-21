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
package com.vertexcache.core.command.argument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing and validating command arguments.
 *
 * Provides helper methods to:
 * - Extract typed values from a list of Argument objects
 * - Simplify argument handling for command implementations
 *
 * Ensures consistent and robust parsing logic across all commands.
 */
public class ArgumentParser {

    private String argumentString;
    private String[] parts;
    private Map<String, Boolean> subArguments;
    private List<Argument> arguments = new ArrayList<>();

    public ArgumentParser(String argumentString) {
        if(argumentString != null && !argumentString.isEmpty()) {
            this.argumentString = argumentString.trim();
            this.parts = this.splitWithQuotes(this.argumentString);
        }
        this.subArguments = new LinkedHashMap<>();
        this.parseArguments();
    }

    public void setSubArguments(ArrayList<String> subArguments) {
        this.arguments = new ArrayList<>(); // reset
        for (String arg : subArguments) {
            this.subArguments.put(arg.toUpperCase(), false); // initialize as not-yet-seen
        }
        this.parseArguments();
    }

    /* No Args, just the cmd */
    public Argument getPrimaryArgument() {
        return arguments.getFirst();
    }

    private void parseArguments() {

        if(parts == null || parts.length == 0) {
            return;
        }

        if(parts.length == 1) {
            // Only one cmd, ie: PING
            this.arguments.add(new Argument(parts[0], new ArrayList<>()));
        } else {
            this.arguments.add( new Argument(parts[0],new ArrayList<>()));
            List<String> args = new ArrayList<>();

            for (int i = 1; i < parts.length; i++) {
                if(this.isSubArgument(parts[i])) {
                    if(this.arguments.size() == 1) {
                        // Primary Cmd, 1st argument
                        this.arguments.getFirst().setArgs(args);
                        args = new ArrayList<>();

                        // Create next argument
                        this.arguments.add( new Argument(parts[i],new ArrayList<>()));
                    } else {
                        this.arguments.get(this.arguments.size() - 1).setArgs(args);
                        args = new ArrayList<>();
                        this.arguments.add( new Argument(parts[i],new ArrayList<>()));
                    }
                } else {
                    args.add(parts[i]);
                }

            }
            this.arguments.get(this.arguments.size() - 1).setArgs(args);
        }
    }

    private String[] splitWithQuotes(String argumentString) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\"[^\"]+\")\\s*").matcher(argumentString);
        while (m.find()) {
            String value = m.group(1);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1); // Remove surrounding quotes
            }
            parts.add(value);
        }
        return parts.toArray(new String[0]);
    }

    public Argument getSubArgumentByName(String subArgumentName) {
        for (Argument argument : this.arguments) {
            if (argument.getName().equalsIgnoreCase(subArgumentName)) {
                return argument;
            }
        }
        return null;
    }

    public boolean subArgumentExists(String subArgumentName) {
        return getSubArgumentByName(subArgumentName.toLowerCase()) != null;
    }

    public boolean isArgumentsExists() {
        return !this.arguments.isEmpty();
    }

    public boolean isSubArgument(String target) {
        String key = target.toUpperCase();
        if (this.subArguments.containsKey(key)) {
            if(this.subArguments.get(key) == false) {
                this.subArguments.put(key, true); // Mark as seen
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
