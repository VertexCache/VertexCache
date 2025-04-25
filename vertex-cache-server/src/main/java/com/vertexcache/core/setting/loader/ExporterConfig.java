package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class ExporterConfig extends LoaderBase {

    private boolean enableExporter;

    @Override
    public void load() {
        this.enableExporter = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_EXPORTER,ConfigKey.ENABLE_EXPORTER_DEFAULT);
    }

    public boolean isEnableExporter() {
        return enableExporter;
    }

    public void setEnableExporter(boolean enableExporter) {
        this.enableExporter = enableExporter;
    }
}
