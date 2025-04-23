package com.vertexcache.core.setting;

import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.EnvLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigAuthWithTenant {

    private ConfigLoader configLoader;
    private boolean enableAuth;
    private boolean enableTenantKeyPrefix = ConfigKey.ENABLE_TENANT_KEY_PREFIX_DEFAULT;

    public void load() {
        this.enableAuth = false;
        this.enableTenantKeyPrefix = false;
        if (configLoader.isExist(ConfigKey.ENABLE_AUTH)) {
            this.enableAuth = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_AUTH));
            if (this.enableAuth && configLoader.isExist(ConfigKey.ENABLE_TENANT_KEY_PREFIX)) {
                this.enableTenantKeyPrefix = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_TENANT_KEY_PREFIX));
            }
        }
    }


    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

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

    public boolean isTenantKeyPrefixingEnabled() {
        return enableTenantKeyPrefix;
    }
}
