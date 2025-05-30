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
package com.vertexcache.core.validation;

import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.core.validation.model.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for aggregating and executing multiple validators as a batch.
 *
 * Allows grouping of related validation steps and running them together,
 * throwing the first encountered VertexCacheValidationException if any check fails.
 *
 * Useful for scenarios where multiple fields or parameters must be validated in tandem,
 * such as command argument parsing or configuration block validation.
 *
 *
 * ValidationBatch Use
 *
 *   Non-Batch Use: just call .validate() directly on a Validator
 *     new UUIDValidator("abc").validate(); // throws immediately
 *
 *   Batch use:
 *     ValidationBatch batch = new ValidationBatch();
 *
 *     batch.check("clientId", new ClientIdValidator("console-client"));
 *     batch.check("token", new UUIDValidator("bad-uuid"));
 *     batch.check("role", new RoleValidator("READER"));
 *
 *     if (batch.hasErrors()) {
 *         System.out.println("Validation errors: " + batch.getSummary());
 *     }
 */
public class ValidationBatch {
    private final List<String> errors = new ArrayList<>();

    public void check(String fieldName, Validator validator) {
        try {
            validator.validate();
        } catch (VertexCacheValidationException e) {
            errors.add(fieldName + ": " + e.getMessage());
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getSummary() {
        return String.join("; ", errors);
    }
}
