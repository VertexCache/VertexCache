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

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.module.auth.model.Role;

/**
 * RoleValidator is responsible for validating role identifiers provided in configuration
 * or command inputs. It ensures that the specified role is recognized and supported
 * by the VertexCache system.
 *
 * Typical roles include READ, READ_WRITE, ADMIN, and REST_API_ADMIN. This validator
 * helps enforce security and correctness by rejecting invalid or unsupported roles.
 */
public class RoleValidator implements Validator {
    private final String value;

    public RoleValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        try {
            Role.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid role: " + value + " (must be ADMIN, READ_WRITE, READ_ONLY, REST_API_READ_ONLY or REST_API_READ_WRITE)");
        }
    }
}
