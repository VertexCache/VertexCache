package com.vertexcache.core.setting;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.config.reader.EnvLoader;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.security.KeyPairHelper;
import com.vertexcache.core.cache.impl.EvictionPolicy;
import com.vertexcache.common.config.VertexCacheConfigException;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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

    private String dataStoreType;

    // Auth & Multi-Tenant
    private boolean enableAuth;
    private String authDataStore;
    private boolean enableTenantKeyPrefix = ConfigKey.ENABLE_TENANT_KEY_PREFIX_DEFAULT;

    // Rate Limiting
    private boolean enableRateLimit;
    private String rateLimitTokensTerSecond;
    private String rateLimitBurst;

    // Metric
    private boolean enableMetric;
    private boolean enableRestApi;
    private boolean enableClustering;
    private boolean enableAdminCommands;
    private boolean enableAlerting;
    private boolean enableIntelligence;
    private boolean enableExporter;

    private ConfigLoader configLoader;

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

                this.configLoader = ConfigLoaderFactory.getLoader(this.configFilePath);
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

                    // Auth
                    this.enableAuth = false;
                    this.enableTenantKeyPrefix = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_AUTH)) {
                        this.enableAuth = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_AUTH));

                        if(this.enableAuth) {
                            this.enableTenantKeyPrefix = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_TENANT_KEY_PREFIX));
                        }
                    }

                    // Rate Limiting
                    this.enableRateLimit = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_RATE_LIMIT)) {
                        this.enableRateLimit = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_RATE_LIMIT));
                        if (configLoader.isExist(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND)) {
                            this.rateLimitTokensTerSecond = configLoader.getProperty(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND);
                        }
                        if (configLoader.isExist(ConfigKey.RATE_LIMIT_BURST)) {
                            this.rateLimitBurst = configLoader.getProperty(ConfigKey.RATE_LIMIT_BURST);
                        }
                    }

                    // Metric
                    this.enableMetric = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_METRIC)) {
                        this.enableMetric = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_METRIC));
                    }

                    // REST API
                    this.enableRestApi = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_REST_API)) {
                        this.enableRestApi = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_REST_API));
                    }

                    // Clustering
                    this.enableClustering = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_CLUSTERING)) {
                        this.enableClustering = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_CLUSTERING));
                    }

                    // Admin Commands
                    this.enableAdminCommands = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_ADMIN_COMMANDS)) {
                        this.enableAdminCommands = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ADMIN_COMMANDS));
                    }

                    // Alerting
                    this.enableAlerting = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_ALERTING)) {
                        this.enableAlerting = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ALERTING));
                    }

                    // Intelligence
                    this.enableIntelligence = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_INTELLIGENCE)) {
                        this.enableIntelligence = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_INTELLIGENCE));
                    }

                    // Exporter
                    this.enableExporter = false;
                    if (configLoader.isExist(ConfigKey.ENABLE_EXPORTER)) {
                        this.enableExporter = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_EXPORTER));
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

    public boolean isEnableVerbose() { return enableVerbose; }

    public boolean isEncryptTransport() { return encryptTransport; }

    public EncryptionMode getEncryptionMode() { return encryptionMode; }

    public boolean isEncryptWithPrivateKey() { return encryptWithPrivateKey; }

    public boolean isEncryptWithSharedKey() { return encryptWithSharedKey; }

    public String getEncryptNote() { return this.encryptNote; }

    public PrivateKey getPrivateKey() { return privateKey; }

    public String getSharedEncryptionKey() { return sharedEncryptionKey; }

    public String getTlsCertificate() { return tlsCertificate; }

    public String getTlsPrivateKey() { return tlsPrivateKey; }

    public String getTlsKeyStorePassword() { return tlsKeyStorePassword; }

    public EvictionPolicy getCacheEvictionPolicy() { return cacheEvictionPolicy;}

    public int getCacheSize() { return cacheSize; }

    public String getDataStoreType()  { return dataStoreType; }

    public String getAuthDataStore() { return authDataStore; }


    // Auth and Multi-Tenant
    public boolean isAuthEnabled() { return enableAuth; }
    public List<String> getRawAuthClientEntries() {
        // TODO - Update PropertiesLoader, if want this supported in PropertiesLoader
        if (!(configLoader instanceof EnvLoader env)) return Collections.emptyList();

        return env.getEnvVariables().entrySet().stream()
                .filter(e -> e.getKey().startsWith(ConfigKey.AUTH_CLIENTS_PREFIX))
                .map(Map.Entry::getValue)
                .filter(val -> val != null && !val.isBlank())
                .toList();
    }
    public boolean isTenantKeyPrefixingEnabled() { return enableTenantKeyPrefix; }

    // Rate Limiting
    public boolean isRateLimitEnabled() { return enableRateLimit; }
    public String getRateLimitTokensTerSecond() { return rateLimitTokensTerSecond; }
    public String getRateLimitBurst() { return rateLimitBurst; }

    public boolean isMetricEnabled() { return enableMetric; }

    public boolean isRestApiEnabled() { return enableRestApi; }

    public boolean isClusteringEnabled() { return enableClustering; }

    public boolean isAdminCommandsEnabled() { return enableAdminCommands; }

    public boolean isAlertingEnabled() { return enableAlerting; }

    public boolean isIntelligenceEnabled() { return enableIntelligence; }

    public boolean isExporterEnabled() { return enableExporter; }
}
