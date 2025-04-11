package com.vertexcache.module.auth;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class AuthSeedLoader {
    public static List<AuthEntry> loadFromJsonFile(String path) {
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AuthEntry>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load auth seed file: " + path, e);
        }
    }
}

