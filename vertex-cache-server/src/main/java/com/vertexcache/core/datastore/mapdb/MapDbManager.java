package com.vertexcache.core.datastore.mapdb;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapDbManager {

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
