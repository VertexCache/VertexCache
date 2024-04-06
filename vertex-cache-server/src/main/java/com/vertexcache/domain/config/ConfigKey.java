package com.vertexcache.domain.config;

public class ConfigKey {

    public static final String LOG_FILEPATH = "log_filepath";

    public static final int SERVER_PORT_DEFAULT = 50505;
    public static final String SERVER_PORT = "server_port";

    // Encrypt Message Layer, Public/Private Key
    public static final String ENABLE_ENCRYPT_MESSAGE = "enable_encrypt_message";
    public static final String PUBLIC_KEY = "public_key";
    public static final String PRIVATE_KEY = "private_key";

    // Encrypt Transport Layer, SSL/TLS
    public static final String ENABLE_ENCRYPT_TRANSPORT = "enable_encrypt_transport";
    public static final String KEYSTORE_FILEPATH = "keystore_filepath";
    public static final String KEYSTORE_PASSWORD = "keystore_password";

    public static final String CACHE_EVICTION = "cache_eviction";
    public static final String CACHE_SIZE = "cache_size";
}
