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
package com.vertexcache.sdk.comm;

import com.vertexcache.sdk.model.ClientOption;
import com.vertexcache.sdk.model.EncryptionMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "VC_LIVE_TLS_SYMMETRIC_TEST", matches = "true")
public class ClientConnectorLiveTlsWithSymmetricKeyTest {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50505;
    private static final String CLIENT_ID = "sdk-client-java";
    private static final String CLIENT_TOKEN = "ea143c4a-1426-4d43-b5be-f0ecffe4a6c7";
    private static final String TLS_CERT = "-----BEGIN CERTIFICATE-----\\nMIIDgDCCAmigAwIBAgIJAPjdssRy18IjMA0GCSqGSIb3DQEBDAUAMG4xEDAOBgNV\\nBAYTB1...";
    private static final String SHARED_KEY = "neEvmCDMRdEgive402Taji9I/vrrpqrjJ+qeAF4QRNc=";

    @Test
    void testLiveConnectAndPing_shouldSucceed() {
        ClientOption opt = new ClientOption();
        opt.setClientId(CLIENT_ID);
        opt.setClientToken(CLIENT_TOKEN);
        opt.setServerHost(HOST);
        opt.setServerPort(PORT);
        opt.setEnableTlsEncryption(true);
        opt.setVerifyCertificate(false); // set to true if cert is verifiable, self signed will fail
        opt.setTlsCertificate(TLS_CERT);
        opt.setEncryptionMode(EncryptionMode.SYMMETRIC);
        opt.setSharedEncryptionKey(SHARED_KEY);

        ClientConnector client = new ClientConnector(opt);
        client.connect();
        assertTrue(client.isConnected());

        String reply = client.send("PING");
        assertNotNull(reply);
        assertTrue(reply.startsWith("+PONG"));

        client.close();
        assertFalse(client.isConnected());
    }
}
