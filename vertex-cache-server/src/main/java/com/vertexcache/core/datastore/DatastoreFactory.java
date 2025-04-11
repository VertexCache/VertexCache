package com.vertexcache.core.datastore;

import com.vertexcache.core.setting.Config;

public class DatastoreFactory {

    public static DatastoreProvider create() {
        String type = Config.getInstance().getDataStoreType().toLowerCase();

        switch (type) {
            case "mapdb":
                return new MapDbProvider();
            // Future support for other providers:
            // case "postgres": return new PostgresProvider();
            // case "mongo": return new MongoProvider();
            // case "mysql": return new MySqlProvider();
            default:
                throw new IllegalArgumentException("Unsupported datastore type: " + type);
        }
    }
}
