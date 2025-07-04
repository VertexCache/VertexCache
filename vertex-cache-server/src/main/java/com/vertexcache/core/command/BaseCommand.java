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
package com.vertexcache.core.command;

/**
 * Base class for all commands in the VertexCache protocol.
 *
 * Defines the common structure and behavior shared by all command types.
 *
 * All concrete commands must extend this class and implement their specific logic.
 */
public abstract class BaseCommand<T> implements Command<T> {

    protected abstract String getCommandKey(); // required by subclass

    protected static final String COMMAND_PRETTY = "PRETTY";

    @Override
    public String getCommandName() {
        return getCommandKey();
    }
}
