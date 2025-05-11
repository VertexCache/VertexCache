package com.vertexcache.core.validation.validators;

import com.vertexcache.core.command.impl.DelCommand;
import com.vertexcache.core.command.impl.GetCommand;
import com.vertexcache.core.command.impl.PingCommand;
import com.vertexcache.core.command.impl.SetCommand;
import com.vertexcache.core.command.impl.internal.PeerPingCommand;
import com.vertexcache.core.command.impl.internal.RoleChangeCommand;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.model.Role;

import java.util.Set;

public class RoleCommandValidator implements Validator {

    private final Role role;
    private final String commandName;

    public RoleCommandValidator(Role role, String commandName) {
        this.role = role;
        this.commandName = commandName;
    }

    @Override
    public void validate() throws VertexCacheValidationException {
        String normalized = commandName.toUpperCase();

        switch (role) {
            case ADMIN -> {
                // Admin can execute all commands
                return;
            }

            case NODE -> {
                if (!Set.of(
                        PeerPingCommand.COMMAND_KEY,
                        RoleChangeCommand.COMMAND_KEY
                ).contains(normalized)) {
                    throw new VertexCacheValidationException("Command not permitted for Cluster Node: " + commandName);
                }
            }

            case READ_WRITE -> {
                if (!Set.of(
                        GetCommand.COMMAND_KEY,
                        SetCommand.COMMAND_KEY,
                        DelCommand.COMMAND_KEY,
                        PingCommand.COMMAND_KEY
                ).contains(normalized)) {
                    throw new VertexCacheValidationException("Command not permitted for role READ_WRITE: " + commandName);
                }
            }

            case READ_ONLY -> {
                if (!Set.of(
                        GetCommand.COMMAND_KEY,
                        PingCommand.COMMAND_KEY
                ).contains(normalized)) {
                    throw new VertexCacheValidationException("Command not permitted for role READ_ONLY: " + commandName);
                }
            }

            default -> throw new VertexCacheValidationException("Unknown client role: " + role);
        }
    }
}
