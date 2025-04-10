package com.vertexcache.server.domain.config;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.security.KeyPairHelper;
import com.vertexcache.server.domain.cache.impl.EvictionPolicy;
import com.vertexcache.common.config.VertexCacheConfigException;

import java.security.PrivateKey;

public class Config extends ConfigBase {

    private static final String APP_NAME = "VertexCache";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean enableVerbose = ConfigKey.ENABLE_VERBOSE_DEFAULT;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPrivateKey = false;
    private boolean encryptWithSharedKey = false;
    private PrivateKey privateKey;

    private String sharedEncryptionKey;
    private String encryptNote = "";

    private boolean encryptTransport = false;
    private String tlsCertificate;
    private String tlsPrivateKey;
    private String tlsKeyStorePassword;

    private EvictionPolicy cacheEvictionPolicy = EvictionPolicy.NONE;
    private static int DEFAULT_CACHE_SIZE=1000000;
    private int cacheSize;

    private static volatile Config instance;

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    @Override
    public void loadPropertiesFromArgs(CommandLineArgsParser commandLineArgsParser) {

        try {

            if(commandLineArgsParser.isExist("--config")) {
                this.configFilePath = commandLineArgsParser.getValue("--config");

                ConfigLoader configLoader = ConfigLoaderFactory.getLoader(this.configFilePath);
                if (configLoader.loadFromPath(this.configFilePath)) {
                    this.configLoaded = true;

                    // Port
                    if (configLoader.isExist(ConfigKey.SERVER_PORT)) {
                        this.serverPort = Integer.parseInt(configLoader.getProperty(ConfigKey.SERVER_PORT));
                    }

                    // Enable Verbose
                    if (configLoader.isExist(ConfigKey.ENABLE_VERBOSE)) {
                        this.enableVerbose = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_VERBOSE));
                    }

                    // Encrypt Message Layer
                    if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                        try {

                            String privateKeyString =  configLoader.getProperty(ConfigKey.PRIVATE_KEY);
                            this.sharedEncryptionKey = configLoader.getProperty(ConfigKey.SHARED_ENCRYPTION_KEY);

                            boolean hasPrivateKey = privateKeyString != null && !privateKeyString.isBlank();
                            boolean hasSharedKey = sharedEncryptionKey != null && !sharedEncryptionKey.isBlank();

                            if (hasPrivateKey && hasSharedKey) {
                                this.encryptNote = ", Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'";
                                LogHelper.getInstance().logWarn("Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                                throw new VertexCacheConfigException("Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                            }

                            if (!hasPrivateKey && !hasSharedKey) {
                                this.encryptNote = ", Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'";
                                LogHelper.getInstance().logWarn("Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                                throw new VertexCacheConfigException("Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                            }

                            if(hasPrivateKey) {
                                this.privateKey = KeyPairHelper.loadPrivateKey(privateKeyString);
                                this.sharedEncryptionKey = null;
                                this.encryptWithPrivateKey = true;
                                this.encryptionMode = EncryptionMode.ASYMMETRIC;
                            }

                            if(hasSharedKey) {
                                this.privateKey = null;
                                this.encryptWithSharedKey = true;
                                this.encryptionMode = EncryptionMode.SYMMETRIC;
                            }

                        } catch (Exception e) {
                            this.encryptionMode = EncryptionMode.NONE;
                        }
                    }

                    // Encrypt Transport Layer
                    if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                        if (configLoader.isExist(ConfigKey.TLS_CERTIFICATE) && configLoader.isExist(ConfigKey.TLS_PRIVATE_KEY)) {
                            this.tlsCertificate = configLoader.getProperty(ConfigKey.TLS_CERTIFICATE);
                            this.tlsPrivateKey = configLoader.getProperty(ConfigKey.TLS_PRIVATE_KEY);
                            this.tlsKeyStorePassword = configLoader.getProperty(ConfigKey.TLS_KEY_STORE_PASSWORD);
                            this.encryptTransport = true;
                        }
                    }

                    // Cache Eviction Policy
                    this.cacheEvictionPolicy = EvictionPolicy.NONE;
                    if (configLoader.isExist(ConfigKey.CACHE_EVICTION)) {
                        try {
                            this.cacheEvictionPolicy = EvictionPolicy.fromString(configLoader.getProperty(ConfigKey.CACHE_EVICTION));
                        } catch (IllegalArgumentException ie) {
                            LogHelper.getInstance().logWarn("Invalid eviction policy given, defaulting to NONE");
                            this.cacheEvictionPolicy = EvictionPolicy.NONE;
                        }
                    } else {
                        LogHelper.getInstance().logWarn("Non-existent eviction policy given, defaulting to NONE");
                    }

                    // Cache Size, applied when Eviction Policy is not set to NONE
                    this.cacheSize = DEFAULT_CACHE_SIZE;
                    if (configLoader.isExist(ConfigKey.CACHE_SIZE)) {
                       long cacheSize = Long.parseLong(configLoader.getProperty(ConfigKey.CACHE_SIZE));
                        if (cacheSize <= Integer.MAX_VALUE) {
                            this.cacheSize = (int) cacheSize;
                        } else {
                            LogHelper.getInstance().logWarn("Cache maximum size exceeded, defaulting to " + DEFAULT_CACHE_SIZE);
                        }
                    } else {
                        LogHelper.getInstance().logWarn("Non-existent cache size, defaulting to " + DEFAULT_CACHE_SIZE);
                    }

                } else {
                    LogHelper.getInstance().logFatal("Properties file failed to load");
                    System.exit(0);
                }
            } else {
                LogHelper.getInstance().logFatal("Parameter --config not set");
                System.exit(0);
            }

        } catch (Exception exception) {
            LogHelper.getInstance().logFatal(exception.getMessage());
            this.configLoaded = false;
            this.configError = true;
        }
    }

    public boolean isConfigLoaded() { return configLoaded; }

    public boolean isConfigError() { return configError; }

    public String getConfigFilePath() { return configFilePath; }

    public String getAppName() { return Config.APP_NAME; }

    public int getServerPort() { return serverPort; }

    public boolean isEnableVerbose() {
        return enableVerbose;
    }

    public boolean isEncryptTransport() { return encryptTransport; }

    public EncryptionMode getEncryptionMode() { return encryptionMode; }

    public boolean isEncryptWithPrivateKey() {
        return encryptWithPrivateKey;
    }

    public boolean isEncryptWithSharedKey() {
        return encryptWithSharedKey;
    }

    public String getEncryptNote() {
        return this.encryptNote;
    }

    public PrivateKey getPrivateKey() { return privateKey; }

    public String getSharedEncryptionKey() {
        return sharedEncryptionKey;
    }

    public String getTlsCertificate() { return tlsCertificate; }

    public String getTlsPrivateKey() {
        return tlsPrivateKey;
    }

    public String getTlsKeyStorePassword() {
        return tlsKeyStorePassword;
    }

    public EvictionPolicy getCacheEvictionPolicy() { return cacheEvictionPolicy;}

    public int getCacheSize() { return cacheSize; }
}
