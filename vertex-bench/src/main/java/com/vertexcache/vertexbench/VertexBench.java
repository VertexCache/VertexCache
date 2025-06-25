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
package com.vertexcache.vertexbench;

import com.vertexcache.vertexbench.load.LoadTestFactory;
import com.vertexcache.vertexbench.load.ThroughputLoad;
import com.vertexcache.vertexbench.setup.CacheDataPopulator;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

public class VertexBench {


    public static void main(String[] args) throws Exception {

        System.out.println("""
+--------------------------------------------------+
|   VertexBench - High Performance Load Generator  |
|                                                  |
|   (c) 2025 Jason Lam | VertexCache Project       |
|   https://github.com/vertexcache/vertexbench     |
|   Licensed under the Apache License, Version 2.0 |
+--------------------------------------------------+
""");

        String jsonPayloadx = """
        {
            "threads": 100,
            "duration": 60,
            "percentageReads": 70,
            "percentageWrites": 25,
            "totalKeyCount": 5000,
            

            "clientId": "sdk-client-java",
            "clientToken": "ea143c4a-1426-4d43-b5be-f0ecffe4a6c7",
            "serverHost": "127.0.0.1",
            "serverPort": 50505,
            "enableTlsEncryption": true,
            "enablePreload" : true,
            "encryptionMode": "ASYMMETRIC",
            "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnwwKN2M7niJj+Vd0+w9Q\nbw5gw5TzAWw2PUBl5rnepgn5QrLmvQ0s4aoDL6JGsnyx+GpSo6UmkrvXknObW+AI\nUzsHLc7bFe9qe/urSvgLKzThl9kb/KN4NueDVJ+s33sDA9z+rRA9+sjp8Pc2Ycmm\nGzN1lC22KM+oPSxHQvRcT5dQ7u6NGg7pX81DJ1ZsCXReE3vGoCQRyJoRPdLA54oR\nNwC82/xKm9cRfghjRKqvnkmpS3FfCj0sLPy4W7ARBWU+RbhU0UmdUutB3Ce1LfIo\n6DpmfhgHJ1P1yd/0ic8qfkqjvwUoxRUhR5+dWIakA8KZYQ95gP6oawmXiu2PcPeV\nEwIDAQAB\n-----END PUBLIC KEY-----"
        }
        """;


        if (args.length < 1) {
            System.out.println("Usage: java -jar vertexbench.jar '<json-payload>'");
            System.exit(1);
        }

        String jsonPayload = args[0];

        VertexBenchConfig vertexBenchConfig = null;
        try {
            vertexBenchConfig = VertexBenchConfig.fromJsonPayload(jsonPayload);

        } catch (Exception ex) {
            System.out.println("JSON Configuration Error: " + ex.getMessage());
            System.exit(0);
        }

        //VertexBenchConfig vertexBenchConfig = new VertexBenchConfig();

       // vertexBenchConfig.

        if(vertexBenchConfig.isEnablePreload()) {
            CacheDataPopulator populator = new CacheDataPopulator(vertexBenchConfig.getVertexCacheSDK());
            populator.populateBasic(1000);
            populator.populateWithIdx1(1000);
            populator.populateWithIdx2(1000);
       }

        ThroughputLoad loadTest = LoadTestFactory.createTest(
                "get-only",
                vertexBenchConfig
        );

        loadTest.execute();
    }
}
