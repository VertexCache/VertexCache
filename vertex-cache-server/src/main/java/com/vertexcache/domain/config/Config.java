package com.vertexcache.domain.config;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.PropertiesLoader;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogUtil;
import com.vertexcache.common.security.KeyPairHelper;
import com.vertexcache.domain.cache.impl.EvictionPolicy;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Config extends ConfigBase {

    private static final LogUtil logger = new LogUtil(Config.class);

    private static final String APP_NAME = "VertexCache";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean logLoaded = false;
    private String logFilePath;

    private boolean encryptMessage = false;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private boolean encryptTransport = false;
    private String keystoreFilePath;
    private String keystorePassword;

    private EvictionPolicy cacheEvictionPolicy;
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

                    // Load Log4j2 property file path
                    if (propertiesLoader.isExist(ConfigKey.LOG_FILEPATH)) {
                        this.logFilePath = propertiesLoader.getProperty(ConfigKey.LOG_FILEPATH);
                        this.logLoaded = LogUtil.load(this.logFilePath);
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
                            logger.warn("Invalid eviction policy given, defaulting to NONE");
                            this.cacheEvictionPolicy = EvictionPolicy.NONE;
                        }
                    } else {
                        logger.warn("Non-existent eviction policy given, defaulting to NONE");
                    }

                    // Cache Size, applied when Eviction Policy is not set to NONE
                    this.cacheSize = DEFAULT_CACHE_SIZE;
                    if (propertiesLoader.isExist(ConfigKey.CACHE_SIZE)) {
                       long cacheSize = Long.parseLong(propertiesLoader.getProperty(ConfigKey.CACHE_SIZE));
                        if (cacheSize <= Integer.MAX_VALUE) {
                            this.cacheSize = (int) cacheSize;
                        } else {
                            logger.warn("Cache maximum size exceeded, defaulting to " + DEFAULT_CACHE_SIZE);
                        }
                    } else {
                        logger.warn("Non-existent cache size, defaulting to " + DEFAULT_CACHE_SIZE);
                    }

                }
            }

        } catch (Exception exception) {
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

    public boolean isLogLoaded() {
        return logLoaded;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public int getServerPort() {
        return serverPort;
    }

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
