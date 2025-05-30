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
package com.vertexcache.core.datastore.model;

/**
 * Interface for providing concrete VertexCache data store implementations.
 *
 * Implementations of this interface encapsulate the logic for initializing and
 * exposing a specific storage backend (e.g., in-memory, MapDB, custom).
 *
 * Used by DataStoreFactory to delegate the creation of a configured and ready-to-use
 * data store instance.
 *
 * Enables a pluggable architecture for adding or swapping out storage backends.
 */
public interface DatastoreProvider {
    void connect();
    void close();
    boolean isConnected();
}
