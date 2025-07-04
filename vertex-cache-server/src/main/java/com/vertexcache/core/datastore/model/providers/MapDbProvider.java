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
package com.vertexcache.core.datastore.model.providers;

import com.vertexcache.core.datastore.model.DatastoreProvider;
import com.vertexcache.core.module.model.ModuleName;
import org.mapdb.DB;

/**
 * DataStoreProvider implementation for the MapDB-backed storage engine.
 *
 * Initializes and provides a persistent data store using MapDB, which supports
 * on-disk storage with optional caching layers and efficient index structures.
 *
 * Suitable for use cases requiring durability, larger datasets, or crash recovery.
 *
 */
public class MapDbProvider implements DatastoreProvider {

    private final ModuleName moduleName;
    private DB db;
    private boolean connected = false;

    public MapDbProvider(ModuleName moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void connect() {
        if (!connected) {
           // String filePath = resolveFilePath(moduleType);
            //db = MapDbManager.getOrOpen(filePath);
            connected = true;
        }
    }

    /*
    private String resolveFilePath(ModuleType moduleType) {
        return switch (moduleType) {
            case AUTH -> Config.getInstance().getAuthDataStore();
            //case RATELIMIT -> Config.getInstance().getRateLimitDataStore();
           // case METRICS -> Config.getInstance().getMetricsDataStore();
            //case EXPORTER -> Config.getInstance().getExporterDataStore();
           // case ALERT -> Config.getInstance().getAlertDataStore();
           // case INTELLIGENCE -> Config.getInstance().getIntelligenceDataStore();
           // case ADMIN -> Config.getInstance().getAdminDataStore();
            case RATELIMIT -> null;
            case METRICS -> null;
            case EXPORTER -> null;
            case ALERT -> null;
            case INTELLIGENCE -> null;
            case ADMIN -> null;
        };
    }
    */

    public DB getDb() {
        return db;
    }

    @Override
    public void close() {
        if (db != null) {
            db.close();
            connected = false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
