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
package com.vertexcache.core.datastore;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing the lifecycle and operations of the underlying data store.
 *
 * This class now generalizes data store handling across different backend
 * implementations (e.g., MapDB, in-memory).
 *
 * Provides high-level operations such as:
 * - Initialization and shutdown
 * - Access to core data structures (primary store, index maps, metadata)
 * - Maintenance and integrity checks
 *
 * Acts as the central access point for all data store interactions within VertexCache.
 */
public class DataStoreService {

    private static final Map<String, DB> databases = new ConcurrentHashMap<>();

    public static DB getOrOpen(String filePath) {
        return databases.computeIfAbsent(filePath, path ->
                DBMaker.fileDB(path)
                        .fileMmapEnableIfSupported()
                        .transactionEnable()
                        .make()
        );
    }

    public static boolean isOpen(String filePath) {
        return databases.containsKey(filePath);
    }

    public static void closeAll() {
        for (DB db : databases.values()) {
            try {
                db.close();
            } catch (Exception e) {
                System.err.println("[MapDbManager] Failed to close DB: " + e.getMessage());
            }
        }
        databases.clear();
    }

    public static int getOpenDbCount() {
        return databases.size();
    }
}
