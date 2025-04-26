package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class MetricConfigLoader extends LoaderBase {

    private boolean enableMetric;

    @Override
    public void load() {

        this.enableMetric = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_METRIC,ConfigKey.ENABLE_METRIC_DEFAULT);
    }

    public boolean isEnableMetric() {
        return enableMetric;
    }

    public void setEnableMetric(boolean enableMetric) {
        this.enableMetric = enableMetric;
    }
}
