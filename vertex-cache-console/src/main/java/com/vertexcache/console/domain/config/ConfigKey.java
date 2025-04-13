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
