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

import java.util.List;

/**
 * Represents a single argument or parameter passed to a Command.
 *
 * Encapsulates raw byte data and provides utilities for decoding into
 * strings, numbers, or other types as needed by the command logic.
 *
 * Used during command parsing to standardize input handling and reduce duplication.
 */
public class Argument {
    private String name;
    private List<String> args;

    public Argument(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) { this.args = args;}

    public boolean isArgsExist() {
        return !args.isEmpty();
    }
}