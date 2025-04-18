package com.vertexcache.core.validation.validators;

import com.vertexcache.core.command.impl.DelCommand;
import com.vertexcache.core.command.impl.GetCommand;
import com.vertexcache.core.command.impl.PingCommand;
import com.vertexcache.core.command.impl.SetCommand;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.Role;

import java.util.Set;

public class RoleCommandValidator {

    private final Role role;

    public RoleCommandValidator(Role role) {
        this.role = role;
    }

    public void validate(String commandName) throws VertexCacheValidationException {
        String normalized = commandName.toUpperCase();

        switch (role) {
            case ADMIN -> {
                // Admin can execute all commands
                return;
            }

            case READ_WRITE -> {
                if (!Set.of(GetCommand.COMMAND_KEY, SetCommand.COMMAND_KEY, DelCommand.COMMAND_KEY, PingCommand.COMMAND_KEY).contains(normalized)) {
                    throw new VertexCacheValidationException("Command not permitted for role READ_WRITE: " + commandName);
                }
            }

            case READ_ONLY -> {
                if (!Set.of(SetCommand.COMMAND_KEY, PingCommand.COMMAND_KEY).contains(normalized)) {
                    throw new VertexCacheValidationException("Command not permitted for role READ: " + commandName);
                }
            }

            default -> throw new VertexCacheValidationException("Unknown client role: " + role);
        }
    }
}
