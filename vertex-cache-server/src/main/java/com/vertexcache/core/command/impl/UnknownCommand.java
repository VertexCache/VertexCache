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
package com.vertexcache.core.command.impl;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.Command;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Fallback command used when an unrecognized or unsupported command is received.
 *
 * Acts as a catch-all to handle invalid or malformed input gracefully,
 * providing a clear error response rather than causing protocol disruption.
 *
 * Typically triggered when the command name does not match any registered handlers.
 * Useful for logging, debugging, and maintaining robustness against client errors.
 */
public class UnknownCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "UNKNOWN";

    public CommandResponse execute() {
        return execute(null,null);
    }

    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setResponseError("Unknown command");
        return commandResponse;
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }
}
