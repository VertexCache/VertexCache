package com.vertexcache.core.datastore.mapdb;

import com.vertexcache.core.datastore.DatastoreProvider;
import com.vertexcache.core.module.ModuleType;
import com.vertexcache.core.setting.Config;
import org.mapdb.DB;

public class MapDbProvider implements DatastoreProvider {

    private final ModuleType moduleType;
    private DB db;
    private boolean connected = false;

    public MapDbProvider(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public void connect() {
        if (!connected) {
            String filePath = resolveFilePath(moduleType);
            db = MapDbManager.getOrOpen(filePath);
            connected = true;
        }
    }

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
