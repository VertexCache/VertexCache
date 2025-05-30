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
package com.vertexcache.module.auth.exception;

/**
 * VertexCacheAuthModuleException represents exceptions specific to the authentication
 * module within VertexCache. It is used to signal errors related to credential validation,
 * token parsing, role mismatches, and other authentication failures.
 *
 * This exception type helps isolate auth-related errors from other system exceptions,
 * allowing for more precise handling and logging in security-sensitive contexts.
 */
public class VertexCacheAuthModuleException extends RuntimeException {
    public VertexCacheAuthModuleException(String message) {
            super(message);
    }
}
