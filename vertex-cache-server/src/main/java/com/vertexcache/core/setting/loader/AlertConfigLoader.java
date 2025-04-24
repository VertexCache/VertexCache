package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class AlertConfigLoader extends LoaderBase {

    private boolean enableAlerting;

    @Override
    public void load() {
        this.enableAlerting = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_ALERTING,ConfigKey.ENABLE_ALERTING_DEFAULT);
    }

    public boolean isEnableAlerting() {
        return enableAlerting;
    }

    public void setEnableAlerting(boolean enableAlerting) {
        this.enableAlerting = enableAlerting;
    }
}
