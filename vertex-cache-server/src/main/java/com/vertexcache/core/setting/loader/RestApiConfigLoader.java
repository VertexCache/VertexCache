package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class RestApiConfigLoader extends LoaderBase {

    private boolean enableRestApi;

    @Override
    public void load() {
        this.enableRestApi = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_REST_API,ConfigKey.ENABLE_REST_API_DEFAULT);
    }

    public boolean isEnableRestApi() {
        return enableRestApi;
    }

    public void setEnableRestApi(boolean enableRestApi) {
        this.enableRestApi = enableRestApi;
    }
}
