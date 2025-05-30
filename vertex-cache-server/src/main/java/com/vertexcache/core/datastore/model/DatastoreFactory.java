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
import com.vertexcache.core.module.model.ModuleName;

/**
 * Factory responsible for creating instances of the configured VertexCache data store.
 *
 * Uses the `DatastoreType` setting to determine which concrete implementation to instantiate,
 * such as in-memory, MapDB, or other custom storage backends.
 *
 * Centralizes data store initialization logic and abstracts away implementation details,
 * enabling flexible plug-in of different storage mechanisms.
 *
 * Throws VertexCacheDataStoreTypeException if the requested type is unsupported.
 */
public class DatastoreFactory {

    public static DatastoreProvider create(ModuleName moduleName) throws VertexCacheDataStoreTypeException {
       /*
        DatastoreType type = DatastoreType.fromString(Config.getInstance().getDataStoreType());

        return switch (type) {
            case MAPDB ->  new MapDbProvider(moduleType);
            // Future support:
            // case POSTGRES -> new PostgresProvider(moduleType);
            // case MONGO -> new MongoProvider(moduleType);
            // case MYSQL -> new MySqlProvider(moduleType);
            default -> throw new VertexCacheDataStoreTypeException("Unsupported datastore type: " + type);
        };

        */
        return null;
    }
}
