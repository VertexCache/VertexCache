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
}
