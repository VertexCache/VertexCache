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
package com.vertexcache.core.validation.model;

import com.vertexcache.core.validation.exception.VertexCacheValidationException;

/**
 * Interface for defining reusable validation logic within VertexCache.
 *
 * Implementations of this interface perform checks on input values,
 * such as command arguments, configuration fields, or request parameters.
 *
 * A validator should:
 * - Return normally if validation passes
 * - Throw a VertexCacheValidationException if validation fails
 *
 * This promotes modular and consistent validation across the system.
 */

@FunctionalInterface
public interface Validator {
    void validate() throws VertexCacheValidationException;
}
