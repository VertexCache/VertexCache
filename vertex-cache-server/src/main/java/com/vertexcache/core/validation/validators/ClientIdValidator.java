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

/**
 * Validator that ensures the client ID is non-blank and adheres to an accepted format.
 *
 * Enforces that the client ID:
 * - Is not null or empty
 * - Contains only alphanumeric characters, dots (.), dashes (-), or underscores (_)
 *
 * Used to validate client identity in authentication or session tracking contexts.
 * Throws a VertexCacheValidationException if the client ID is invalid.
 */
public class ClientIdValidator implements Validator {
    private final String value;

    public ClientIdValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException("Client ID cannot be blank");
        }
        if (!value.matches("^[a-zA-Z0-9._-]+$")) {
            throw new VertexCacheValidationException("Client ID must be alphanumeric with -, _, . allowed");
        }
    }
}
