package com.vertexcache.core.setting;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.config.VertexCacheConfigException;
import com.vertexcache.module.cluster.ClusterConfigLoader;

import java.util.List;
import java.util.Map;


public class Config extends ConfigBase {

    private static final String APP_NAME = "VertexCache";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean enableVerbose = ConfigKey.ENABLE_VERBOSE_DEFAULT;

    private ConfigCache configCache;
    private ConfigSecurity configSecurity;
    private ConfigAuthWithTenant configAuthWithTenant;
    private ConfigRateLimiting configRateLimiting;

    private String authDataStore;
    private String dataStoreType;

    // Clustering
    private boolean enableClustering;
    private ClusterConfigLoader clusterConfigLoader;

    // Metric
    private boolean enableMetric;
    private boolean enableRestApi;
    private boolean enableAdminCommands;
    private boolean enableAlerting;
    private boolean enableIntelligence;
    private boolean enableExporter;

    private ConfigLoader configLoader;

    private static volatile Config instance;

    private Config() {
        this.configCache = new ConfigCache();
        this.configSecurity = new ConfigSecurity();
        this.configAuthWithTenant = new ConfigAuthWithTenant();
        this.configRateLimiting = new ConfigRateLimiting();
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

                    this.configSecurity.setConfigLoader(this.configLoader);
                    this.configCache.setConfigLoader(this.configLoader);
                    this.configAuthWithTenant.setConfigLoader(this.configLoader);
                    this.configRateLimiting.setConfigLoader(this.configLoader);

                    // Port
                    if (configLoader.isExist(ConfigKey.SERVER_PORT)) {
                        this.serverPort = configLoader.getIntProperty(ConfigKey.SERVER_PORT,ConfigKey.SERVER_PORT_DEFAULT);
                    }

                    // Enable Verbose
                    if (configLoader.isExist(ConfigKey.ENABLE_VERBOSE)) {
                        this.enableVerbose = configLoader.getBooleanProperty(ConfigKey.ENABLE_VERBOSE,ConfigKey.ENABLE_VERBOSE_DEFAULT);
                    }
                    
                    configSecurity.loadFromConfigLoader();
                    configCache.loadFromConfigLoader();
                    configAuthWithTenant.load();
                    configRateLimiting.load();


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

                        if (this.enableClustering) {
                            this.clusterConfigLoader = new ClusterConfigLoader(configLoader);
                        }
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


    public boolean isMetricEnabled() { return enableMetric; }

    public boolean isRestApiEnabled() { return enableRestApi; }

    // Clustering
    public boolean isClusteringEnabled() { return enableClustering; }
    public ClusterConfigLoader getClusterConfigLoader() { return clusterConfigLoader; }

    public boolean isAdminCommandsEnabled() { return enableAdminCommands; }

    public boolean isAlertingEnabled() { return enableAlerting; }

    public boolean isIntelligenceEnabled() { return enableIntelligence; }

    public boolean isExporterEnabled() { return enableExporter; }

    public void reloadFromDisk() {
        try {
            ConfigLoader loader = ConfigLoaderFactory.getLoader(this.configFilePath);
            if (loader.loadFromPath(this.configFilePath)) {
                this.configLoader = loader;
                loadPropertiesFromLoader();
                LogHelper.getInstance().logInfo("Configuration reloaded from: " + this.configFilePath);
            } else {
                throw new VertexCacheConfigException("Failed to reload .env from: " + this.configFilePath);
            }
        } catch (Exception e) {
            throw new VertexCacheConfigException("Config reload failed: " + e.getMessage(), e);
        }
    }

    private void loadPropertiesFromLoader() {
        loadCoreSettings();
        configSecurity.loadEncryptionSettings();
        configSecurity.loadTransportSettings();
        configCache.loadCacheSettings();
        configAuthWithTenant.load();
        configRateLimiting.load();
        loadModuleEnableFlags();
    }

    private void loadCoreSettings() {
        if (configLoader.isExist(ConfigKey.SERVER_PORT)) {
            this.serverPort = Integer.parseInt(configLoader.getProperty(ConfigKey.SERVER_PORT));
        }

        if (configLoader.isExist(ConfigKey.ENABLE_VERBOSE)) {
            this.enableVerbose = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_VERBOSE));
        }
    }

    private void loadModuleEnableFlags() {
        if (configLoader.isExist(ConfigKey.ENABLE_METRIC)) {
            this.enableMetric = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_METRIC));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_REST_API)) {
            this.enableRestApi = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_REST_API));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_CLUSTERING)) {
            this.enableClustering = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_CLUSTERING));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_ADMIN_COMMANDS)) {
            this.enableAdminCommands = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ADMIN_COMMANDS));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_ALERTING)) {
            this.enableAlerting = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ALERTING));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_INTELLIGENCE)) {
            this.enableIntelligence = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_INTELLIGENCE));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_EXPORTER)) {
            this.enableExporter = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_EXPORTER));
        }
    }

    public Map<String, String> getClusterFlatSummary() {
        return clusterConfigLoader != null ? clusterConfigLoader.getFlatSummary() : Map.of();
    }

    public List<String> getClusterTextSummary() {
        return clusterConfigLoader != null ? clusterConfigLoader.getTextSummary() : List.of();
    }

    public String getDataStoreType()  { return dataStoreType; }

    public String getAuthDataStore() { return authDataStore; }

    public ConfigCache getConfigCache() {
        return configCache;
    }

    public ConfigSecurity getConfigSecurity() {
        return configSecurity;
    }

    public ConfigAuthWithTenant getConfigAuthWithTenant() {
        return configAuthWithTenant;
    }

    public ConfigRateLimiting getConfigRateLimiting() {
        return configRateLimiting;
    }
}

