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
package com.vertexcache.console.domain.config;

public class ConfigKey {

    public static final String LOG_FILEPATH = "log_filepath";

    public static final String CLIENT_ID_DEFAULT = "client-console";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_TOKEN = "client_token";

    public static final String SERVER_HOST_DEFAULT = "localhost";
    public static final String SERVER_HOST = "server_host";

    public static final int SERVER_PORT_DEFAULT = 50505;
    public static final String SERVER_PORT = "server_port";

    // Encrypt Message Layer, Public/Private Key
    public static final String ENABLE_ENCRYPT_MESSAGE = "enable_encrypt_message";
    public static final String PUBLIC_KEY = "public_key";
    public static final String SHARED_ENCRYPTION_KEY = "shared_encryption_key";

    // Encrypt Transport Layer, SSL/TLS
    public static final String ENABLE_ENCRYPT_TRANSPORT = "enable_encrypt_transport";
    public static final String ENABLE_VERIFY_TLS_CERTIFICATE = "enable_verify_certificate";
    public static final String TLS_CERTIFICATE = "tls_certificate";

}
