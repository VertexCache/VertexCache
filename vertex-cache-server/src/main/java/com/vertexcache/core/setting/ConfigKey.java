package com.vertexcache.core.setting;

public class ConfigKey {

    public static final String LOG_FILEPATH = "log_filepath";

    public static final int SERVER_PORT_DEFAULT = 50505;
    public static final String SERVER_PORT = "server_port";

    public static final boolean ENABLE_VERBOSE_DEFAULT = false;
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

    // MapDB - Embedded, hydration ie: Auth
    //public static final String AUTH_HYDRATE_FILE = "auth_hydrate_file";
    //public static final String AUTH_DB_FILE = "auth_db_file";

    public static final String DATA_STORE_TYPE_DEFAULT = "mapdb";
    public static final String DATA_STORE_TYPE = "data_store_type";

    // Auth Module Related
    public static final String ENABLE_AUTH = "enable_auth";
    public static final String AUTH_DATA_STORE = "auth_data_store";

    // Rate Limiting
    public static final String ENABLE_RATE_LIMIT = "enable_rate_limit";

    // Metrics
    public static final String ENABLE_METRIC = "enable_metric";

    // REST API
    public static final String ENABLE_REST_API = "enable_rest_api";

    // Clustering
    public static final String ENABLE_CLUSTERING = "enable_clustering";

    // Admin Commands
    public static final String ENABLE_ADMIN_COMMANDS = "enable_admin_commands";

    // Alerting
    public static final String ENABLE_ALERTING = "enable_alerting";

    // Intelligence
    public static final String ENABLE_INTELLIGENCE = "enable_intelligence";

    // Exporter
    public static final String ENABLE_EXPORTER = "enable_exporter";

}
