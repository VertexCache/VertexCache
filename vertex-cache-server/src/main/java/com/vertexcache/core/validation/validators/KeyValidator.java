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
 * KeyValidator is responsible for validating the primary cache key provided by clients.
 * It ensures the key is non-null, non-empty, and conforms to the constraints expected
 * by the VertexCache system, such as length or character encoding rules.
 */
public class KeyValidator implements Validator {

    private final String fieldName;
    private final String value;

    public KeyValidator(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public void validate() {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException(fieldName + " must not be blank");
        }

        if (value.length() > 255) {
            throw new VertexCacheValidationException(fieldName + " exceeds maximum length (255)");
        }

        if (!value.matches("^[a-zA-Z0-9:_\\-\\.]+$")) {
            throw new VertexCacheValidationException(fieldName + " contains invalid characters");
        }
    }
}
