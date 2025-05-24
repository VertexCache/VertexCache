package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class SmartConfigLoader extends LoaderBase {

    private boolean enableSmart;
    private boolean enableSmartHotkeyWatcher;
    private boolean enableSmartIndexCleanup;
    private boolean enableSmartAccessTracking;
    private boolean enableSmartAlertTriggers;

    @Override
    public void load() {
        this.enableSmart = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART,ConfigKey.ENABLE_SMART_DEFAULT);
        this.enableSmartHotkeyWatcher = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_HOTKEY_WATCHER,ConfigKey.ENABLE_SMART_HOTKEY_WATCHER_DEFAULT);
        this.enableSmartIndexCleanup = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_INDEX_CLEANUP,ConfigKey.ENABLE_SMART_INDEX_CLEANUP_DEFAULT);
        this.enableSmartAccessTracking = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_ACCESS_TRACKING,ConfigKey.ENABLE_SMART_ACCESS_TRACKING_DEFAULT);
        this.enableSmartAlertTriggers = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_ALERT_TRIGGERS,ConfigKey.ENABLE_SMART_ALERT_TRIGGERS_DEFAULT);
    }

    public boolean isEnableSmart() {
        return enableSmart;
    }
    public boolean isEnableSmartHotkeyWatcher() {return enableSmartHotkeyWatcher;}
    public boolean isEnableSmartIndexCleanup() {return enableSmartIndexCleanup;}
    public boolean isEnableSmartAccessTracking() {return enableSmartAccessTracking;}
    public boolean isEnableSmartAlertTriggers() {return enableSmartAlertTriggers;}

    public void setEnableSmart(boolean enableSmart) {
        this.enableSmart = enableSmart;
    }
    public void setEnableSmartHotkeyWatcher(boolean enableSmartHotkeyWatcher) {this.enableSmartHotkeyWatcher = enableSmartHotkeyWatcher;}
    public void setEnableSmartIndexCleanup(boolean enableSmartIndexCleanup) {this.enableSmartIndexCleanup = enableSmartIndexCleanup;}
    public void setEnableSmartAccessTracking(boolean enableSmartAccessTracking) {this.enableSmartAccessTracking = enableSmartAccessTracking;}
    public void setEnableSmartAlertTriggers(boolean enableSmartAlertTriggers) {this.enableSmartAlertTriggers = enableSmartAlertTriggers;}
}
