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
package com.vertexcache.core.setting;

/**
 * Enum representing all supported configuration keys in VertexCache.
 *
 * Defines the canonical names, expected types, and default values for each
 * configuration property loaded at startup.
 *
 * Used by the Config loader to validate and normalize incoming settings from
 * the environment, .env files, or system properties.
 *
 * Ensures consistency, type safety, and discoverability of available config options.
 */
public class ConfigKey {

    public static final String LOG_FILEPATH = "log_filepath";

    public static final int SERVER_PORT_DEFAULT = 50505;
    public static final String SERVER_PORT = "server_port";

    public static final boolean ENABLE_VERBOSE_DEFAULT = true;
    public static final String ENABLE_VERBOSE = "enable_verbose";

    // Encrypt Message Layer, Private/Public Key RSA (no use for the public key that's for the client), Shared Key AES
    public static final String ENABLE_ENCRYPT_MESSAGE = "enable_encrypt_message";
    public static final String PRIVATE_KEY = "private_key";
    public static final String PUBLIC_KEY = "public_key";
    public static final String SHARED_ENCRYPTION_KEY = "shared_encryption_key";


    // Encrypt Transport Layer, SSL/TLS
    public static final String ENABLE_ENCRYPT_TRANSPORT = "enable_encrypt_transport";
    public static final String TLS_CERTIFICATE = "tls_certificate";
    public static final String TLS_PRIVATE_KEY = "tls_private_key";
    public static final String TLS_KEY_STORE_PASSWORD = "tls_keystore_password";

    public static final String CACHE_EVICTION = "cache_eviction";
    public static final String CACHE_SIZE = "cache_size";

    // Auth Module & Mutli-Tenant Related
    public static final String ENABLE_AUTH = "enable_auth";
    public static final String AUTH_CLIENTS_PREFIX = "auth_client_";
    public static final boolean ENABLE_TENANT_KEY_PREFIX_DEFAULT = true;
    public static final String ENABLE_TENANT_KEY_PREFIX = "enable_tenant_key_prefix";

    // Rate Limiting
    public static final boolean ENABLE_RATE_LIMIT_DEFAULT = true;
    public static final String ENABLE_RATE_LIMIT = "enable_rate_limit";
    public static final int RATE_LIMIT_TOKENS_PER_SECOND_DEFAULT = 1000;
    public static final String RATE_LIMIT_TOKENS_PER_SECOND = "rate_limit_tokens_per_second";
    public static final int RATE_LIMIT_BURST_DEFAULT = 2000;
    public static final String RATE_LIMIT_BURST = "rate_limit_burst";

    // Metrics
    public static final boolean ENABLE_METRIC_DEFAULT = true;
    public static final String ENABLE_METRIC = "enable_metric";

    // REST API
    public static final boolean ENABLE_REST_API_DEFAULT = false;
    public static final String ENABLE_REST_API = "enable_rest_api";
    public static final int REST_API_PORT_DEFAULT = 8080;
    public static final String REST_API_PORT = "rest_api_port";
    public static final int REST_API_PORT_TLS_DEFAULT = 8443;
    public static final String REST_API_PORT_TLS = "rest_api_port_tls";
    public static final String REST_API_REQUIRE_AUTH = "rest_api_require_auth";
    public static final String REST_API_REQUIRE_TLS = "rest_api_require_tls";
    public static final String REST_API_TOKEN_HEADER = "rest_api_token_header";
    public static final String REST_API_ALLOW_CORS = "rest_api_allow_cors";
    public static final String REST_API_ALLOW_ADMIN = "rest_api_allow_admin";

    // Clustering - Not this follows the clustering convention prefix
    public static final boolean ENABLE_CLUSTERING_DEFAULT = false;
    public static final String ENABLE_CLUSTERING = "cluster_enabled";
    public static final String CLUSTER_NODE_ID = "cluster_node_id";

    // Admin Commands
    public static final boolean ENABLE_ADMIN_COMMANDS_DEFAULT = false;
    public static final String ENABLE_ADMIN_COMMANDS = "enable_admin_commands";

    // Alerting
    public static final boolean ENABLE_ALERTING_DEFAULT = false;
    public static final String ENABLE_ALERTING = "enable_alerting";
    public static final String ALERT_WEBHOOK_URL = "alert_webhook_url";
    public static final String ALERT_WEBHOOK_SIGNING_ENABLED = "alert_webhook_signing_enabled";
    public static final String ALERT_WEBHOOK_SIGNING_SECRET = "alert_webhook_signing_secret";
    public static final int ALERT_WEBHOOK_TIMEOUT_DEFAULT = 2000;
    public static final String ALERT_WEBHOOK_TIMEOUT = "alert_webhook_timeout";
    public static final int ALERT_WEBHOOK_RETRY_COUNT_DEFAULT = 3;
    public static final String ALERT_WEBHOOK_RETRY_COUNT = "alert_webhook_retry_count";


    // Smart
    public static final boolean ENABLE_SMART_DEFAULT = false;
    public static final String ENABLE_SMART = "enable_smart";
    public static final boolean ENABLE_SMART_HOTKEY_WATCHER_ALERT_DEFAULT = true;
    public static final String ENABLE_SMART_HOTKEY_WATCHER_ALERT = "enable_smart_hotkey_watcher_alert";
    public static final boolean ENABLE_SMART_INDEX_CLEANUP_DEFAULT = true;
    public static final String ENABLE_SMART_INDEX_CLEANUP = "enable_smart_index_cleanup";
    public static final boolean ENABLE_SMART_KEY_CHURN_ALERT_DEFAULT = true;
    public static final String ENABLE_SMART_KEY_CHURN_ALERT = "enable_smart_key_churn_alert";
    public static final boolean ENABLE_SMART_UNAUTHORIZED_ACCESS_ALERT_DEFAULT = true;
    public static final String ENABLE_SMART_UNAUTHORIZED_ACCESS_ALERT = "enable_smart_unauthorized_access_alert";
    public static final boolean ENABLE_SMART_HOTKEY_ANOMALY_ALERT_DEFAULT = true;
    public static final String ENABLE_SMART_HOTKEY_ANOMALY_ALERT = "enable_smart_hotkey_anomaly_alert";

    // Exporter
    public static final boolean ENABLE_EXPORTER_DEFAULT = false;
    public static final String ENABLE_EXPORTER = "enable_exporter";

}
