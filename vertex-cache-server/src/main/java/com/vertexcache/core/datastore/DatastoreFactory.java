package com.vertexcache.core.datastore;

import com.vertexcache.core.datastore.mapdb.MapDbProvider;
import com.vertexcache.core.module.ModuleType;
import com.vertexcache.core.setting.Config;

public class DatastoreFactory {

    public static DatastoreProvider create(ModuleType moduleType) throws VertexCacheDataStoreTypeException {
        DatastoreType type = DatastoreType.fromString(Config.getInstance().getDataStoreType());

        return switch (type) {
            case MAPDB ->  new MapDbProvider(moduleType);
            // Future support:
            // case POSTGRES -> new PostgresProvider(moduleType);
            // case MONGO -> new MongoProvider(moduleType);
            // case MYSQL -> new MySqlProvider(moduleType);
            default -> throw new VertexCacheDataStoreTypeException("Unsupported datastore type: " + type);
        };
    }
}
