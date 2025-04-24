package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class CoreConfigLoader extends LoaderBase {

    private static final String APP_NAME = "VertexCache";
    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;
    private boolean enableVerbose = ConfigKey.ENABLE_VERBOSE_DEFAULT;

    @Override
    public void load() {

        this.serverPort = this.getConfigLoader().getIntProperty(ConfigKey.SERVER_PORT,ConfigKey.SERVER_PORT_DEFAULT);
        this.enableVerbose = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_VERBOSE,ConfigKey.ENABLE_VERBOSE_DEFAULT);

    }

    public String getAppName() { return CoreConfigLoader.APP_NAME; }
    public int getServerPort() {
        return serverPort;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public boolean isEnableVerbose() {
        return enableVerbose;
    }
    public void setEnableVerbose(boolean enableVerbose) {
        this.enableVerbose = enableVerbose;
    }

}
