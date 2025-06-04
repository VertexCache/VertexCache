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

package com.vertexcache.sdk.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClientOptionTest {

    @Test
    public void testDefaults() {
        ClientOption option = new ClientOption();
        assertEquals("sdk-client", option.getClientId());
        assertEquals("", option.getClientToken());
        assertEquals("127.0.0.1", option.getServerHost());
        assertEquals(50505, option.getServerPort());
        assertFalse(option.isEnableTlsEncryption());
        assertFalse(option.isVerifyCertificate());
        assertEquals(3000, option.getReadTimeout());
        assertEquals(3000, option.getConnectTimeout());
        assertEquals(EncryptionMode.NONE, option.getEncryptionMode());
        assertNotNull(option.getIdentCommand());
    }

    @Test
    public void testSetValues() {
        ClientOption option = new ClientOption();
        option.setClientId("test-client");
        option.setClientToken("token123");
        option.setServerHost("192.168.1.100");
        option.setServerPort(9999);
        option.setEnableTlsEncryption(true);
        option.setVerifyCertificate(true);
        option.setTlsCertificate("cert");
        option.setConnectTimeout(1234);
        option.setReadTimeout(5678);
        option.setEncryptionMode(EncryptionMode.SYMMETRIC);

        assertEquals("test-client", option.getClientId());
        assertEquals("token123", option.getClientToken());
        assertEquals("192.168.1.100", option.getServerHost());
        assertEquals(9999, option.getServerPort());
        assertTrue(option.isEnableTlsEncryption());
        assertTrue(option.isVerifyCertificate());
        assertEquals("cert", option.getTlsCertificate());
        assertEquals(1234, option.getConnectTimeout());
        assertEquals(5678, option.getReadTimeout());
        assertEquals(EncryptionMode.SYMMETRIC, option.getEncryptionMode());
    }

    @Test
    public void testIdentCommandGeneration() {
        ClientOption option = new ClientOption();
        option.setClientId("my-id");
        option.setClientToken("my-token");
        String expected = "IDENT {\"client_id\":\"my-id\", \"token\":\"my-token\"}";
        assertEquals(expected, option.getIdentCommand());
    }

    @Test
    public void testNullTokenAndIdFallback() {
        ClientOption option = new ClientOption();
        option.setClientId(null);
        option.setClientToken(null);
        String ident = option.getIdentCommand();
        assertTrue(ident.contains("\"client_id\":\"\""));
        assertTrue(ident.contains("\"token\":\"\""));
    }
}
