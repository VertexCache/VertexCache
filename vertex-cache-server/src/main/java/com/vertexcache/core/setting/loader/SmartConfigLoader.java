package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class SmartConfigLoader extends LoaderBase {

    private boolean enableSmart;

    @Override
    public void load() {
        this.enableSmart = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART,ConfigKey.ENABLE_SMART_DEFAULT);
    }

    public boolean isEnableSmart() {
        return enableSmart;
    }

    public void setEnableSmart(boolean enableSmart) {
        this.enableSmart = enableSmart;
    }
}
