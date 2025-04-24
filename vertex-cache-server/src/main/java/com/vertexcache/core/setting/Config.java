package com.vertexcache.core.setting;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.config.VertexCacheConfigException;
import com.vertexcache.core.setting.loader.*;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;

import java.util.List;
import java.util.Map;


public class Config extends ConfigBase {


    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private static volatile Config instance;
    private ConfigLoader configLoader;

    private CoreConfigLoader coreConfigLoader;
    private CacheConfigLoader cacheConfigLoader;
    private SecurityConfigLoader securityConfigLoader;

    private AdminConfigLoader adminConfigLoader;
    private AlertConfigLoader alertConfigLoader;
    private AuthWithTenantConfigLoader authWithTenantConfigLoader;
    private RateLimitingConfigLoader rateLimitingConfigLoader;
    private ClusterConfigLoader clusterConfigLoader;

    // Clustering
    private boolean enableClustering;

    // Metric
    private boolean enableMetric;
    private boolean enableRestApi;
    //private boolean enableAlerting;
    private boolean enableIntelligence;
    private boolean enableExporter;

    private Config() {
        this.coreConfigLoader = new CoreConfigLoader();
        this.alertConfigLoader = new AlertConfigLoader();
        this.cacheConfigLoader = new CacheConfigLoader();
        this.securityConfigLoader = new SecurityConfigLoader();

        // Modules
        this.adminConfigLoader = new AdminConfigLoader();
        this.authWithTenantConfigLoader = new AuthWithTenantConfigLoader();
        this.rateLimitingConfigLoader = new RateLimitingConfigLoader();
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

                    this.coreConfigLoader.setConfigLoader(this.configLoader);
                    this.securityConfigLoader.setConfigLoader(this.configLoader);
                    this.cacheConfigLoader.setConfigLoader(this.configLoader);

                    this.adminConfigLoader.setConfigLoader(this.configLoader);
                    this.alertConfigLoader.setConfigLoader(this.configLoader);
                    this.authWithTenantConfigLoader.setConfigLoader(this.configLoader);
                    this.rateLimitingConfigLoader.setConfigLoader(this.configLoader);

                    this.coreConfigLoader.load();
                    this.securityConfigLoader.load();
                    this.cacheConfigLoader.load();

                    this.adminConfigLoader.load();
                    this.alertConfigLoader.load();
                    this.authWithTenantConfigLoader.load();
                    this.rateLimitingConfigLoader.load();


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

    public void reloadFromDisk() {
        try {
            ConfigLoader loader = ConfigLoaderFactory.getLoader(this.configFilePath);
            if (loader.loadFromPath(this.configFilePath)) {

                this.configLoader = loader;
                this.coreConfigLoader.load();
                this.securityConfigLoader.loadEncryptionSettings();
                this.securityConfigLoader.loadTransportSettings();
                this.cacheConfigLoader.loadCacheSettings();

                this.adminConfigLoader.load();
                this.alertConfigLoader.load();
                this.authWithTenantConfigLoader.load();

                this.rateLimitingConfigLoader.load();

                loadModuleEnableFlags();

                LogHelper.getInstance().logInfo("Configuration reloaded from: " + this.configFilePath);
            } else {
                throw new VertexCacheConfigException("Failed to reload .env from: " + this.configFilePath);
            }
        } catch (Exception e) {
            throw new VertexCacheConfigException("Config reload failed: " + e.getMessage(), e);
        }
    }

    public boolean isConfigLoaded() { return configLoaded; }
    public boolean isConfigError() { return configError; }
    public String getConfigFilePath() { return configFilePath; }


    public boolean isMetricEnabled() { return enableMetric; }
    public boolean isRestApiEnabled() { return enableRestApi; }

    public boolean isClusteringEnabled() { return enableClustering; }
    public ClusterConfigLoader getClusterConfigLoader() {
        return clusterConfigLoader;
    }

    public boolean isIntelligenceEnabled() { return enableIntelligence; }
    public boolean isExporterEnabled() { return enableExporter; }


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

        if (configLoader.isExist(ConfigKey.ENABLE_INTELLIGENCE)) {
            this.enableIntelligence = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_INTELLIGENCE));
        }
        if (configLoader.isExist(ConfigKey.ENABLE_EXPORTER)) {
            this.enableExporter = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_EXPORTER));
        }
    }

    public Map<String, String> getClusterFlatSummary() {return clusterConfigLoader != null ? clusterConfigLoader.getFlatSummary() : Map.of();}
    public List<String> getClusterTextSummary() {return clusterConfigLoader != null ? clusterConfigLoader.getTextSummary() : List.of();}


    public CacheConfigLoader getCacheConfigLoader() {return cacheConfigLoader;}
    public SecurityConfigLoader getSecurityConfigLoader() {return securityConfigLoader;}
    public AdminConfigLoader getAdminConfigLoader() {return adminConfigLoader;}
    public AlertConfigLoader getAlertConfigLoader() {return alertConfigLoader;}
    public AuthWithTenantConfigLoader getAuthWithTenantConfigLoader() {return authWithTenantConfigLoader;}
    public RateLimitingConfigLoader getRateLimitingConfigLoader() {return rateLimitingConfigLoader;}
    public CoreConfigLoader getCoreConfigLoader() {return coreConfigLoader;}

}

