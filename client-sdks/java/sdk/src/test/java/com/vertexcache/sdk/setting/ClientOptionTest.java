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
package com.vertexcache.sdk.setting;

import com.vertexcache.sdk.setting.ClientOption;
import com.vertexcache.sdk.transport.crypto.EncryptionMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientOptionTest {

    @Test
    public void testDefaultValues() {
        ClientOption opt = new ClientOption();

        assertEquals(ClientOption.DEFAULT_CLIENT_ID, opt.getClientId());
        assertEquals(ClientOption.DEFAULT_HOST, opt.getServerHost());
        assertEquals(ClientOption.DEFAULT_PORT, opt.getServerPort());
        assertEquals(ClientOption.DEFAULT_READ_TIMEOUT, opt.getReadTimeout());
        assertEquals(ClientOption.DEFAULT_CONNECT_TIMEOUT, opt.getConnectTimeout());

        assertFalse(opt.isEnableTlsEncryption());
        assertFalse(opt.isVerifyCertificate());
        assertNull(opt.getClientToken());
        assertNull(opt.getTlsCertificate());
        assertNull(opt.getPublicKey());
        assertNull(opt.getSharedEncryptionKey());
        assertEquals(EncryptionMode.NONE, opt.getEncryptionMode());
    }

    @Test
    public void testSettersAndGetters() {
        ClientOption opt = new ClientOption();

        opt.setClientId("custom-client");
        opt.setClientToken("secure-token");
        opt.setServerHost("10.0.0.1");
        opt.setServerPort(9999);
        opt.setEnableTlsEncryption(true);
        opt.setVerifyCertificate(true);
        opt.setTlsCertificate("-----BEGIN CERTIFICATE-----abc-----END CERTIFICATE-----");
        opt.setPublicKey("-----BEGIN PUBLIC KEY-----xyz-----END PUBLIC KEY-----");
        opt.setSharedEncryptionKey("base64sharedkey==");
        opt.setReadTimeout(5000);
        opt.setConnectTimeout(6000);
        opt.setEncryptionMode(EncryptionMode.SYMMETRIC);

        assertEquals("custom-client", opt.getClientId());
        assertEquals("secure-token", opt.getClientToken());
        assertEquals("10.0.0.1", opt.getServerHost());
        assertEquals(9999, opt.getServerPort());
        assertTrue(opt.isEnableTlsEncryption());
        assertTrue(opt.isVerifyCertificate());
        assertEquals("-----BEGIN CERTIFICATE-----abc-----END CERTIFICATE-----", opt.getTlsCertificate());
        assertEquals("-----BEGIN PUBLIC KEY-----xyz-----END PUBLIC KEY-----", opt.getPublicKey());
        assertEquals("base64sharedkey==", opt.getSharedEncryptionKey());
        assertEquals(5000, opt.getReadTimeout());
        assertEquals(6000, opt.getConnectTimeout());
        assertEquals(EncryptionMode.SYMMETRIC, opt.getEncryptionMode());
    }
}

