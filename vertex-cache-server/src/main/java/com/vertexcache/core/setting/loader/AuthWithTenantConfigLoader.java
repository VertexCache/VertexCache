package com.vertexcache.core.setting.loader;

import com.vertexcache.common.config.reader.EnvLoader;
import com.vertexcache.core.setting.ConfigKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AuthWithTenantConfigLoader extends LoaderBase {

    private boolean enableAuth;
    private boolean enableTenantKeyPrefix = ConfigKey.ENABLE_TENANT_KEY_PREFIX_DEFAULT;

    @Override
    public void load() {
        this.enableAuth = false;
        this.enableTenantKeyPrefix = false;
        if (this.getConfigLoader().isExist(ConfigKey.ENABLE_AUTH)) {
            this.enableAuth = Boolean.parseBoolean(this.getConfigLoader().getProperty(ConfigKey.ENABLE_AUTH));
            if (this.enableAuth && this.getConfigLoader().isExist(ConfigKey.ENABLE_TENANT_KEY_PREFIX)) {
                this.enableTenantKeyPrefix = Boolean.parseBoolean(this.getConfigLoader().getProperty(ConfigKey.ENABLE_TENANT_KEY_PREFIX));
            }
        }
    }

    public boolean isAuthEnabled() { return enableAuth; }

    public List<String> getRawAuthClientEntries() {
        if (!(this.getConfigLoader() instanceof EnvLoader env)) return Collections.emptyList();

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
