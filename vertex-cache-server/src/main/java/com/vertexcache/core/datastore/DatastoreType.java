package com.vertexcache.core.datastore;

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
