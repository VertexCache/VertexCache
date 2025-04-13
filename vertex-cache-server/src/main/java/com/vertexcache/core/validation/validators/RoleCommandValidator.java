package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.Role;

public class RoleCommandValidator implements ValidatorHandler<String> {

    private final Role role;

    public RoleCommandValidator(Role role) {
        this.role = role;
    }

    @Override
    public void validate(String command) {
        if (role == null || command == null || command.isBlank()) {
            throw new VertexCacheValidationException("Missing role or command");
        }

        if (!role.canExecute(command.toUpperCase())) {
            throw new VertexCacheValidationException("command '" + command + "' not permitted for role '" + role + "'");
        }
    }
}
