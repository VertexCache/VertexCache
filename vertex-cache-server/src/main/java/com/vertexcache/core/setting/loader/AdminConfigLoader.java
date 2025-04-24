package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class AdminConfigLoader extends LoaderBase {

    private boolean enableAdminCommands;

    @Override
    public void load() {
        this.enableAdminCommands = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_ADMIN_COMMANDS,ConfigKey.ENABLE_ADMIN_COMMANDS_DEFAULT);
    }

    public boolean isAdminCommandsEnabled() { return enableAdminCommands; }
    public void setEnableAdminCommands(boolean enableAdminCommands) { this.enableAdminCommands = enableAdminCommands;}
}
