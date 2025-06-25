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
package com.vertexcache.vertexbench.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.EncryptionMode;

import com.google.gson.Gson;


public class VertexBenchConfig {

    // === Benchmark Defaults ===
    private int percentageScale = 100;
    private int maxValueSuffix = 10000;
    private int percentageReads = 50;
    private int percentageWrites = 50;
    private int totalKeyCount = 1000;
    private boolean enablePreload = false;
    private int threads = 50;
    private int duration = 30;

    // === VertexCache Defaults ===
    private String clientId;
    private String clientToken;
    private String serverHost;
    private int serverPort;
    private boolean enableTlsEncryption;

    private EncryptionMode encryptionMode;
    String sharedKey;
    String publicKey;


    // === Runtime Only ===
    private transient VertexCacheSDK vertexCacheSDK;

    private VertexBenchConfig() throws Exception {
       // this.buildSdk();
    }

    private void buildSdk() throws Exception {
        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(clientId);
        clientOption.setClientToken(clientToken);
        clientOption.setServerHost(serverHost);
        clientOption.setServerPort(serverPort);
        clientOption.setEnableTlsEncryption(enableTlsEncryption);
        clientOption.setEncryptionMode(encryptionMode);
        clientOption.setPublicKey(publicKey);
        this.vertexCacheSDK = new VertexCacheSDK(clientOption);
        this.vertexCacheSDK.openConnection();
    }

    public static VertexBenchConfig fromJsonPayload(String jsonPayload) throws Exception {
        Gson gson = new Gson();
        JsonObject json = JsonParser.parseString(jsonPayload).getAsJsonObject();

        VertexBenchConfig config = new VertexBenchConfig();


        if (json.has("threads")) {
            config.threads = json.get("threads").getAsInt();
        }
        if (json.has("duration")) {
            config.duration = json.get("duration").getAsInt();
        }
        if (json.has("percentageReads")) {
            config.percentageReads = json.get("percentageReads").getAsInt();
        }
        if (json.has("percentageWrites")) {
            config.percentageWrites = json.get("percentageWrites").getAsInt();
        }
        if (json.has("totalKeyCount")) {
            config.totalKeyCount = json.get("totalKeyCount").getAsInt();
        }


        if (json.has("maxValueSuffix")) {
            config.maxValueSuffix = json.get("maxValueSuffix").getAsInt();
        }
        if (json.has("percentageScale")) {
            config.percentageScale = json.get("percentageScale").getAsInt();
        }


        // General Config
        if (json.has("clientId")) {
            config.clientId = json.get("clientId").getAsString();
        }
        if (json.has("clientToken")) {
            config.clientToken = json.get("clientToken").getAsString();
        }
        if (json.has("serverHost")) {
            config.serverHost = json.get("serverHost").getAsString();
        }
        if (json.has("serverPort")) {
            config.serverPort = json.get("serverPort").getAsInt();
        }



        // Preload cache with some test data when 'true'
        if (json.has("enablePreload")) {
            config.enablePreload = json.get("enablePreload").getAsBoolean();
        }

        // Enable/Disable TLS
        if (json.has("enableTlsEncryption")) {
            config.enableTlsEncryption = json.get("enableTlsEncryption").getAsBoolean();
        }

        // Message Encryption
        if (json.has("encryptionMode")) {
            config.encryptionMode = EncryptionMode.valueOf(json.get("encryptionMode").getAsString());
        }
        if (json.has("sharedKey")) {
            config.publicKey = json.get("sharedKey").getAsString();
        }
        if (json.has("publicKey")) {
            config.publicKey = json.get("publicKey").getAsString();
        }

        config.buildSdk();

        return config;
    }

    public VertexCacheSDK getVertexCacheSDK() { return vertexCacheSDK; }
    public int getPercentageScale() { return percentageScale; }
    public int getMaxValueSuffix() { return maxValueSuffix; }
    public int getPercentageReads() { return percentageReads; }
    public int getPercentageWrites() { return percentageWrites; }
    public int getTotalKeyCount() { return totalKeyCount; }
    public boolean isEnablePreload() { return enablePreload; }
    public int getThreads() { return threads; }
    public int getDuration() { return duration; }
    public String getClientId() { return clientId; }
    public String getClientToken() { return clientToken; }
    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public boolean isEnableTlsEncryption() { return enableTlsEncryption; }
    public EncryptionMode getEncryptionMode() { return encryptionMode; }
    public String getPublicKey() { return publicKey; }
}
