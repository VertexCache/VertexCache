package com.vertexcache.core.command;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.impl.UnknownCommand;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.RoleCommandValidator;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Set;

public class CommandService {

    private static final Set<String> UNSECURED_COMMANDS = Set.of(
            "PING", "HELP", "VERSION", "AUTH"
    );

    private CommandFactory commandFactory = new CommandFactory();

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
        String commandName = command.getCommandName().toUpperCase();

        if (Config.getInstance().isAuthEnabled() &&
                !UNSECURED_COMMANDS.contains(commandName)) {

            if (session == null) {
                CommandResponse commandResponse = new CommandResponse();
                commandResponse.setResponseError("Authentication required");
                return commandResponse;
            }

            try {
                new RoleCommandValidator(session.getRole()).validate(commandName);
            } catch (VertexCacheValidationException e) {
                CommandResponse commandResponse = new CommandResponse();
                commandResponse.setResponseError("Authorization failed, invalid role.");
                return commandResponse;
            }
        }

        return command.execute(argumentParser,session);
    }
}
