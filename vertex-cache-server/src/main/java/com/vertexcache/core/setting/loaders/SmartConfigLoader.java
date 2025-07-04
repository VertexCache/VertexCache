/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.setting.loaders;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

/**
 * Configuration loader responsible for validating settings related to the SmartModule.
 *
 * Handles options such as:
 * - Whether the SmartModule is enabled
 * - Background sweeper interval for cleaning up reverse indexes and expired references
 * - Thresholds or tuning parameters for SmartModule optimizations (if applicable)
 *
 * Ensures that intelligent index management and maintenance routines are properly configured
 * before the SmartModule is initialized.
 *
 * This loader is required for enabling automated index consistency and cleanup logic
 * in multi-index caching scenarios.
 */
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
