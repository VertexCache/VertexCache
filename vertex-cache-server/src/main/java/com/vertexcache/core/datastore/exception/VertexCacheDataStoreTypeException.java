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
package com.vertexcache.core.datastore.exception;

/**
 * Exception thrown when an unsupported or invalid data store type is encountered.
 *
 * This wrapper is used to signal configuration or runtime errors where the specified
 * VertexCache backend or storage type is not recognized, improperly set, or incompatible.
 *
 * Common scenarios include:
 * - Misconfigured or missing `DATA_STORE_TYPE` setting
 * - Attempting to initialize with an unknown or unsupported store implementation
 *
 * Typically raised during startup or initialization of cache components.
 */
public class VertexCacheDataStoreTypeException extends Exception {
    public VertexCacheDataStoreTypeException() {
        super();
    }

    public VertexCacheDataStoreTypeException(String message) {
        super(message);
    }

    public VertexCacheDataStoreTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertexCacheDataStoreTypeException(Throwable cause) {
        super(cause);
    }
}
