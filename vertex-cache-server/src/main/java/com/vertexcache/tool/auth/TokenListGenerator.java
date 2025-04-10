package com.vertexcache.tool.auth;

import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Tool to help generate the contents for: auth_seed.json
 *
 * Run: java -cp .:gson-<version>.jar vertexcache.tool.auth.TokenListGenerator 5 tenant-x ADMIN
 *
 * Output:
 *  [
 *    {
 *      "clientId": "client-1",
 *      "token": "UUID-1",
 *      "tenantId": "tenant-x",
 *      "role": "ADMIN"
 *    },
 *    ...
 *  ]
 *
 */
public class TokenListGenerator {

    static class AuthEntry {
        String clientId;
        String token;
        String tenantId;
        String role;

        AuthEntry(String clientId, String token, String tenantId, String role) {
            this.clientId = clientId;
            this.token = token;
            this.tenantId = tenantId;
            this.role = role;
        }
    }

    public static void main(String[] args) throws IOException {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        String tenant = args.length > 1 ? args[1] : "tenant-default";
        String role = args.length > 2 ? args[2] : "READ_WRITE";

        List<AuthEntry> entries = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String clientId = "client-" + i;
            String token = UUID.randomUUID().toString();
            entries.add(new AuthEntry(clientId, token, tenant, role));
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("auth_seed_generated.json")) {
            gson.toJson(entries, writer);
        }

        System.out.println("Generated " + count + " client tokens into auth_seed_generated.json");
    }
}
