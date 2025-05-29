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
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.model.Role;

public class RoleCommandValidator implements Validator {

    private final Role role;
    private final String commandName;

    public RoleCommandValidator(Role role, String commandName) {
        this.role = role;
        this.commandName = commandName;
    }

    @Override
    public void validate() throws VertexCacheValidationException {
        if (role == null || commandName == null || !role.canExecute(commandName)) {
            throw new VertexCacheValidationException("Command not permitted for role " + role);
        }
    }
}
