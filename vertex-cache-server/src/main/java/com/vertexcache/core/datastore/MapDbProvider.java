package com.vertexcache.core.datastore;

import org.mapdb.DB;
import com.vertexcache.core.setting.Config;

public class MapDbProvider implements DatastoreProvider {

    private DB db;
    private boolean connected = false;

    @Override
    public void connect() {
        if (!connected) {
            //String filePath = Config.getInstance().getAuthDbFile(); // Or other appropriate file path
           // db = MapDbManager.getOrOpen(filePath);
            connected = true;
        }
    }

    public DB getDb() {
        if (!connected) {
            connect();
        }
        return db;
    }

    @Override
    public void close() {
        if (db != null && connected) {
            db.close();
            connected = false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}

