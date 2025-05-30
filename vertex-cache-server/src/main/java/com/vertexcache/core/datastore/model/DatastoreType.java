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

import com.vertexcache.core.datastore.exception.VertexCacheDataStoreTypeException;

/**
 * Enum representing the supported data store types in VertexCache.
 *
 * Used to configure and select the appropriate storage backend at runtime.
 * Each enum value maps to a specific implementation of the DataStoreProvider interface.
 *
 * Common types may include:
 * - MEMORY: In-memory only (non-persistent, fast)
 * - MAPDB: Embedded persistent storage with disk backing
 *
 * This enum is parsed from the `DatastoreType` configuration setting.
 */
public enum DatastoreType {
    MAPDB;
    //POSTGRES,
    //MONGO,
    //MYSQL;

    public static DatastoreType fromString(String value) throws VertexCacheDataStoreTypeException {
        return switch (value.toLowerCase()) {
            case "mapdb" -> MAPDB;
            //case "postgres" -> POSTGRES;
            //case "mongo" -> MONGO;
            //case "mysql" -> MYSQL;
            default -> throw new VertexCacheDataStoreTypeException("Unsupported datastore type: " + value);
        };
    }
}
