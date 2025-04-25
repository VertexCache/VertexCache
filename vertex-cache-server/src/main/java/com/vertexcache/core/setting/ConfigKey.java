package com.vertexcache.core.setting;

public class ConfigKey {

    public static final String LOG_FILEPATH = "log_filepath";

    public static final int SERVER_PORT_DEFAULT = 50505;
    public static final String SERVER_PORT = "server_port";

    public static final boolean ENABLE_VERBOSE_DEFAULT = true;
    public static final String ENABLE_VERBOSE = "enable_verbose";

    // Encrypt Message Layer, Private/Public Key RSA (no use for the public key that's for the client), Shared Key AES
    public static final String ENABLE_ENCRYPT_MESSAGE = "enable_encrypt_message";
    public static final String PRIVATE_KEY = "private_key";
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
    public static final String ENABLE_METRIC = "enable_metric";

    // REST API
    public static final String ENABLE_REST_API = "enable_rest_api";

    // Clustering - Not this follows the clustering convention prefix
    public static final String ENABLE_CLUSTERING = "cluster_enabled";

    // Admin Commands
    public static final boolean ENABLE_ADMIN_COMMANDS_DEFAULT = false;
    public static final String ENABLE_ADMIN_COMMANDS = "enable_admin_commands";

    // Alerting
    public static final boolean ENABLE_ALERTING_DEFAULT = false;
    public static final String ENABLE_ALERTING = "enable_alerting";

    // Intelligence
    public static final String ENABLE_INTELLIGENCE = "enable_intelligence";

    // Exporter
    public static final boolean ENABLE_EXPORTER_DEFAULT = false;
    public static final String ENABLE_EXPORTER = "enable_exporter";

}
