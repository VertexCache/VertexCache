package com.vertexcache.server.domain.config;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.PropertiesLoader;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.KeyPairHelper;
import com.vertexcache.server.domain.cache.impl.EvictionPolicy;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Config extends ConfigBase {

    private static final String APP_NAME = "VertexCache";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean enableVerbose = ConfigKey.ENABLE_VERBOSE_DEFAULT;

    private boolean encryptMessage = false;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private boolean encryptTransport = false;
    private String keystoreFilePath;
    private String keystorePassword;

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

                PropertiesLoader propertiesLoader = new PropertiesLoader();
                if (propertiesLoader.loadFromPath(this.configFilePath)) {
                    this.configLoaded = true;

                    // Port
                    if (propertiesLoader.isExist(ConfigKey.SERVER_PORT)) {
                        this.serverPort = Integer.parseInt(propertiesLoader.getProperty(ConfigKey.SERVER_PORT));
                    }

                    // Enable Verbose
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_VERBOSE)) {
                        this.enableVerbose = Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_VERBOSE));
                    }

                    // Encrypt Message Layer
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                        this.encryptMessage = true;
                        this.publicKey = KeyPairHelper.decodePublicKey(propertiesLoader.getProperty(ConfigKey.PUBLIC_KEY));
                        this.privateKey = KeyPairHelper.decodePrivateKey(propertiesLoader.getProperty(ConfigKey.PRIVATE_KEY));
                    }

                    // Encrypt Transport Layer
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                        this.encryptTransport = true;
                        this.keystoreFilePath = propertiesLoader.getProperty(ConfigKey.KEYSTORE_FILEPATH);
                        this.keystorePassword = propertiesLoader.getProperty(ConfigKey.KEYSTORE_PASSWORD);
                    }

                    // Cache Eviction Policy
                    this.cacheEvictionPolicy = EvictionPolicy.NONE;
                    if (propertiesLoader.isExist(ConfigKey.CACHE_EVICTION)) {
                        try {
                            this.cacheEvictionPolicy = EvictionPolicy.fromString(propertiesLoader.getProperty(ConfigKey.CACHE_EVICTION));
                        } catch (IllegalArgumentException ie) {
                            LogHelper.getInstance().logWarn("Invalid eviction policy given, defaulting to NONE");
                            this.cacheEvictionPolicy = EvictionPolicy.NONE;
                        }
                    } else {
                        LogHelper.getInstance().logWarn("Non-existent eviction policy given, defaulting to NONE");
                    }

                    // Cache Size, applied when Eviction Policy is not set to NONE
                    this.cacheSize = DEFAULT_CACHE_SIZE;
                    if (propertiesLoader.isExist(ConfigKey.CACHE_SIZE)) {
                       long cacheSize = Long.parseLong(propertiesLoader.getProperty(ConfigKey.CACHE_SIZE));
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
            System.out.println(exception.getMessage());
            this.configLoaded = false;
            this.configError = true;
        }
    }

    public boolean isConfigLoaded() {
        return configLoaded;
    }

    public boolean isConfigError() {
        return configError;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public String getAppName() {
        return Config.APP_NAME;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isEnableVerbose() { return enableVerbose; }

    public boolean isEncryptTransport() {
        return encryptTransport;
    }

    public boolean isEncryptMessage() {
        return encryptMessage;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getKeystoreFilePath() {
        return keystoreFilePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public EvictionPolicy getCacheEvictionPolicy() {
        return cacheEvictionPolicy;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
