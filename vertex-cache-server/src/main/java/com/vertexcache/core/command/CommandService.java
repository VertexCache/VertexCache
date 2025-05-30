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

import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.impl.PingCommand;
import com.vertexcache.core.command.impl.UnknownCommand;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.RoleCommandValidator;
import com.vertexcache.module.ratelimiter.service.RateLimiterManager;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Set;

/**
 * Service responsible for processing and executing incoming Command instances.
 *
 * Acts as the central dispatch mechanism that:
 * - Validates incoming commands
 * - Delegates execution to the appropriate command handler
 * - Manages command lifecycle, including logging and error handling
 *
 */
public class CommandService {

    private static final Set<String> UNSECURED_COMMANDS = Set.of(
            PingCommand.COMMAND_KEY
    );

    private final CommandFactory commandFactory = new CommandFactory();

    public byte[] execute(byte[] requestAsBytes, ClientSessionContext session) {
        if (requestAsBytes != null && requestAsBytes.length > 0) {
            ArgumentParser argumentParser = new ArgumentParser(new String(requestAsBytes));
            Command<String> command = commandFactory.getCommand(argumentParser.getPrimaryArgument().getName());
            CommandResponse response = processCommand(command, argumentParser, session);
            return response.toVCMPAsBytes();
        }
        return (new UnknownCommand()).execute().toVCMPAsBytes();
    }

    private CommandResponse processCommand(Command<String> command, ArgumentParser argumentParser, ClientSessionContext session) {

        try {
            String commandName = command.getCommandName().toUpperCase();

            // Auth check
            if (Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled() &&
                    !UNSECURED_COMMANDS.contains(commandName)) {

                if (session == null) {
                    CommandResponse commandResponse = new CommandResponse();
                    commandResponse.setResponseError("Authentication required");
                    return commandResponse;
                }

                try {
                    new RoleCommandValidator(session.getRole(), commandName).validate();
                } catch (VertexCacheValidationException e) {
                    CommandResponse commandResponse = new CommandResponse();
                    commandResponse.setResponseError("Authorization failed, invalid role.");
                    return commandResponse;
                }
            }

            // Global Rate Limiting
            if (Config.getInstance().getRateLimitingConfigLoader().isRateLimitEnabled()) {
                if (!RateLimiterManager.getInstance().allowCommand()) {
                    CommandResponse rateLimitResponse = new CommandResponse();
                    rateLimitResponse.setResponseError("Rate Limit exceeded, too many requests. Please try again later.");
                    return rateLimitResponse;
                }
            }

            return command.execute(argumentParser, session);

        } catch (Exception e) {
            CommandResponse commandResponse = new CommandResponse();
            commandResponse.setResponseError("Unexpected command execution " + e.getMessage());
            return commandResponse;
        }
    }
}
