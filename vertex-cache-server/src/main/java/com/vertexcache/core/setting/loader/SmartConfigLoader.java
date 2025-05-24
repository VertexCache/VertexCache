package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class SmartConfigLoader extends LoaderBase {

    private boolean enableSmart;
    private boolean enableSmartIndexCleanup;
    private boolean enableSmartHotkeyWatcherAlert;
    private boolean enableSmartKeyChurnAlert;
    private boolean enableSmartUnauthorizedAccessAlert;
    private boolean enableSmartHotkeyAnomalyAlert;

    @Override
    public void load() {
        this.enableSmart = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART,ConfigKey.ENABLE_SMART_DEFAULT);
        this.enableSmartHotkeyWatcherAlert = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_HOTKEY_WATCHER_ALERT,ConfigKey.ENABLE_SMART_HOTKEY_WATCHER_ALERT_DEFAULT);
        this.enableSmartIndexCleanup = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_INDEX_CLEANUP,ConfigKey.ENABLE_SMART_INDEX_CLEANUP_DEFAULT);
        this.enableSmartKeyChurnAlert = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_KEY_CHURN_ALERT,ConfigKey.ENABLE_SMART_KEY_CHURN_ALERT_DEFAULT);
        this.enableSmartUnauthorizedAccessAlert = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_UNAUTHORIZED_ACCESS_ALERT,ConfigKey.ENABLE_SMART_UNAUTHORIZED_ACCESS_ALERT_DEFAULT);
        this.enableSmartHotkeyAnomalyAlert = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_SMART_HOTKEY_ANOMALY_ALERT,ConfigKey.ENABLE_SMART_HOTKEY_ANOMALY_ALERT_DEFAULT);
    }

    public boolean isEnableSmart() {
        return enableSmart;
    }
    public boolean isEnableSmartHotkeyWatcherAlert() {return enableSmartHotkeyWatcherAlert;}
    public boolean isEnableSmartIndexCleanup() {return enableSmartIndexCleanup;}
    public boolean isEnableSmartKeyChurnAlert() {return enableSmartKeyChurnAlert;}
    public boolean isEnableSmartUnauthorizedAccessAlert() {return enableSmartUnauthorizedAccessAlert;}
    public boolean isEnableSmartHotkeyAnomalyAlert() {return enableSmartHotkeyAnomalyAlert;}

    public void setEnableSmart(boolean enableSmart) {
        this.enableSmart = enableSmart;
    }
    public void setEnableSmartHotkeyWatcherAlert(boolean enableSmartHotkeyWatcherAlert) {this.enableSmartHotkeyWatcherAlert = enableSmartHotkeyWatcherAlert;}
    public void setEnableSmartIndexCleanup(boolean enableSmartIndexCleanup) {this.enableSmartIndexCleanup = enableSmartIndexCleanup;}
    public void setEnableSmartKeyChurnAlert(boolean enableSmartKeyChurnAlert) {this.enableSmartKeyChurnAlert = enableSmartKeyChurnAlert;}
    public void setEnableSmartUnauthorizedAccessAlert(boolean enableSmartUnauthorizedAccessAlert) {this.enableSmartUnauthorizedAccessAlert = enableSmartUnauthorizedAccessAlert;}
    public void setEnableSmartHotkeyAnomalyAlert(boolean enableSmartHotkeyAnomalyAlert) {this.enableSmartHotkeyAnomalyAlert = enableSmartHotkeyAnomalyAlert;}
}
