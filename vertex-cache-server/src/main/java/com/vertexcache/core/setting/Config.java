/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.setting;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.config.VertexCacheConfigException;
import com.vertexcache.core.setting.loader.*;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;

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
    //private ExporterConfigLoader exporterConfigLoader;
    private MetricConfigLoader metricConfigLoader;
    private final RestApiConfigLoader restApiConfigLoader;
    private final SmartConfigLoader smartConfigLoader;

    private Config() {
        this.coreConfigLoader = new CoreConfigLoader();
        this.alertConfigLoader = new AlertConfigLoader();
        this.cacheConfigLoader = new CacheConfigLoader();
        this.securityConfigLoader = new SecurityConfigLoader();
        this.adminConfigLoader = new AdminConfigLoader();
        this.authWithTenantConfigLoader = new AuthWithTenantConfigLoader();
        this.clusterConfigLoader = new ClusterConfigLoader();
        //this.exporterConfigLoader = new ExporterConfigLoader();
        this.metricConfigLoader = new MetricConfigLoader();
        this.rateLimitingConfigLoader = new RateLimitingConfigLoader();
        this.restApiConfigLoader = new RestApiConfigLoader();
        this.smartConfigLoader = new SmartConfigLoader();
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
                    this.coreConfigLoader.setConfigLoader(this.configLoader).load();
                    this.securityConfigLoader.setConfigLoader(this.configLoader).load();
                    this.cacheConfigLoader.setConfigLoader(this.configLoader).load();
                    this.adminConfigLoader.setConfigLoader(this.configLoader).load();
                    this.alertConfigLoader.setConfigLoader(this.configLoader).load();
                    this.authWithTenantConfigLoader.setConfigLoader(this.configLoader).load();
                    this.clusterConfigLoader.setConfigLoader(this.configLoader).load();
                    //this.exporterConfigLoader.setConfigLoader(this.configLoader).load();
                    this.metricConfigLoader.setConfigLoader(this.configLoader).load();
                    this.rateLimitingConfigLoader.setConfigLoader(this.configLoader).load();
                    this.restApiConfigLoader.setConfigLoader(this.configLoader).load();
                    this.smartConfigLoader.setConfigLoader(this.configLoader).load();
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
                this.clusterConfigLoader.load();
                this.adminConfigLoader.load();
                this.alertConfigLoader.load();
                this.authWithTenantConfigLoader.load();
                //this.exporterConfigLoader.load();
                this.metricConfigLoader.load();
                this.restApiConfigLoader.load();
                this.rateLimitingConfigLoader.load();
                this.smartConfigLoader.load();
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

    public CoreConfigLoader getCoreConfigLoader() {return coreConfigLoader;}
    public CacheConfigLoader getCacheConfigLoader() {return cacheConfigLoader;}
    public SecurityConfigLoader getSecurityConfigLoader() {return securityConfigLoader;}
    public AdminConfigLoader getAdminConfigLoader() {return adminConfigLoader;}
    public AlertConfigLoader getAlertConfigLoader() {return alertConfigLoader;}
    public AuthWithTenantConfigLoader getAuthWithTenantConfigLoader() {return authWithTenantConfigLoader;}
    public ClusterConfigLoader getClusterConfigLoader() {return clusterConfigLoader;}
    //public ExporterConfigLoader getExporterConfigLoader() {return exporterConfigLoader;}
    public MetricConfigLoader getMetricConfigLoader() {return metricConfigLoader;}
    public RateLimitingConfigLoader getRateLimitingConfigLoader() {return rateLimitingConfigLoader;}
    public RestApiConfigLoader getRestApiConfigLoader() {return restApiConfigLoader;}
    public SmartConfigLoader getSmartConfigLoader() {return smartConfigLoader;}
}

