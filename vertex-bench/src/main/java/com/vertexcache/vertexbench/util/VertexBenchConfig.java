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

import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.EncryptionMode;

public class VertexBenchConfig {

    // TODO All needs to be removed, inject via CLI Command line

    private final static String CLIENT_ID = "sdk-client-java";
    private final static String CLIENT_TOKEN = "ea143c4a-1426-4d43-b5be-f0ecffe4a6c7";
    private final static String VERTEXCACHE_SERVER_HOST = "localhost";
    private final static int VERTEXCACHE_SERVER_PORT = 50505;
    private final static boolean ENABLE_TLS = true;
    private final static String TEST_TLS_CERT = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18Ij...<TRIMMED>";
    private final static EncryptionMode ENABLE_PUBLIC_PRIVATE_KEY_USE = EncryptionMode.ASYMMETRIC;
    private final static String TEST_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----";



    private boolean enablePreload;
    private int threads;
    private int duration;

    public VertexCacheSDK buildSdk() throws Exception {

        // For now, needs refactoring

        this.threads = 50;
        this.duration = 30;

        this.enablePreload = true;

        ClientOption clientOption = new ClientOption();
        clientOption.setClientId(CLIENT_ID);
        clientOption.setClientToken(CLIENT_TOKEN);
        clientOption.setServerHost(VERTEXCACHE_SERVER_HOST);
        clientOption.setServerPort(VERTEXCACHE_SERVER_PORT);
        clientOption.setEnableTlsEncryption(ENABLE_TLS);
        clientOption.setTlsCertificate(TEST_TLS_CERT);
        clientOption.setEncryptionMode(ENABLE_PUBLIC_PRIVATE_KEY_USE);
        clientOption.setPublicKey(TEST_PUBLIC_KEY);

        return new VertexCacheSDK(clientOption);
    }

    public boolean isEnablePreload() {
        return enablePreload;
    }

    public int getThreads() {
        return threads;
    }

    public int getDuration() {
        return duration;
    }
}
