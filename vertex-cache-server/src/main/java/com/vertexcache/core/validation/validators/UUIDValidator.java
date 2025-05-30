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

import java.util.UUID;

/**
 * UUIDValidator ensures that a given input string is a valid UUID in standard format.
 * It checks for proper length, character composition, and dash placement as per
 * RFC 4122 specifications.
 *
 * This validator is typically used to verify identifiers such as client IDs,
 * session tokens, or correlation IDs where UUIDs are expected for uniqueness
 * and traceability within the VertexCache system.
 */
public class UUIDValidator implements Validator {
    private final String value;

    public UUIDValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        try {
            UUID.fromString(value.trim());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid UUID format: " + value);
        }
    }
}
