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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration loader responsible for parsing and validating alert-related settings.
 *
 * Handles options such as:
 * - Whether alerting is enabled
 * - The configured alert webhook URL
 *
 * Ensures that all required fields are present and valid before the AlertModule is initialized.
 * If alerting is disabled or misconfigured, no alerts will be dispatched at runtime.
 *
 */
public class AlertConfigLoader extends LoaderBase {

    private boolean enableAlerting;
    private String alertWebhookUrl;
    private boolean alertWebhookSigningEnabled;
    private String alertWebhookSigningSecret;
    private int alertWebhookTimeout;
    private int alertWebhookRetryCount;

    @Override
    public void load() {
        this.enableAlerting = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_ALERTING,ConfigKey.ENABLE_ALERTING_DEFAULT);
        this.alertWebhookUrl = this.getConfigLoader().getProperty(ConfigKey.ALERT_WEBHOOK_URL,"");
        this.alertWebhookSigningEnabled = this.getConfigLoader().getBooleanProperty(ConfigKey.ALERT_WEBHOOK_SIGNING_ENABLED,true);
        this.alertWebhookSigningSecret = this.getConfigLoader().getProperty(ConfigKey.ALERT_WEBHOOK_SIGNING_SECRET,"");
        this.alertWebhookTimeout = this.getConfigLoader().getIntProperty(ConfigKey.ALERT_WEBHOOK_TIMEOUT,ConfigKey.ALERT_WEBHOOK_TIMEOUT_DEFAULT);
        this.alertWebhookRetryCount = this.getConfigLoader().getIntProperty(ConfigKey.ALERT_WEBHOOK_RETRY_COUNT,ConfigKey.ALERT_WEBHOOK_RETRY_COUNT_DEFAULT);
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ConfigKey.ENABLE_ALERTING, String.valueOf(enableAlerting));
        map.put(ConfigKey.ALERT_WEBHOOK_URL, alertWebhookUrl != null ? alertWebhookUrl : "null");
        map.put(ConfigKey.ALERT_WEBHOOK_SIGNING_ENABLED, String.valueOf(alertWebhookSigningEnabled));
        map.put(ConfigKey.ALERT_WEBHOOK_SIGNING_SECRET, alertWebhookSigningSecret != null && !alertWebhookSigningSecret.isBlank() ? "**** (set)" : "(not set)");
        map.put(ConfigKey.ALERT_WEBHOOK_TIMEOUT, String.valueOf(alertWebhookTimeout));
        map.put(ConfigKey.ALERT_WEBHOOK_RETRY_COUNT, String.valueOf(alertWebhookRetryCount));
        return map;
    }

    public List<String> getTextSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Alerting Settings:");
        lines.add("  enabled:            " + enableAlerting);
        lines.add("  webhook URL:        " + (alertWebhookUrl != null ? alertWebhookUrl : "null"));
        lines.add("  signing enabled:    " + enableAlerting);
        lines.add("  signing secret:     " + (alertWebhookSigningSecret != null && !alertWebhookSigningSecret.isBlank() ? "**** (set)" : "(not set)"));
        lines.add("  webhook timeout:    " + alertWebhookTimeout + " ms");
        lines.add("  webhook retry count:" + alertWebhookRetryCount);
        return lines;
    }

    public boolean isEnableAlerting() {
        return enableAlerting;
    }
    public void setEnableAlerting(boolean enableAlerting) {
        this.enableAlerting = enableAlerting;
    }
    public String getAlertWebhookUrl() {return alertWebhookUrl;}
    public void setAlertWebhookUrl(String alertWebhookUrl) {this.alertWebhookUrl = alertWebhookUrl;}
    public boolean isAlertWebhookSigningEnabled() {return alertWebhookSigningEnabled;}
    public void setAlertWebhookSigningEnabled(boolean alertWebhookSigningEnabled) {this.alertWebhookSigningEnabled = alertWebhookSigningEnabled;}
    public String getAlertWebhookSigningSecret() {return alertWebhookSigningSecret;}
    public void setAlertWebhookSigningSecret(String alertWebhookSigningSecret) {this.alertWebhookSigningSecret = alertWebhookSigningSecret;}
    public int getAlertWebhookTimeout() {return alertWebhookTimeout;}
    public void setAlertWebhookTimeout(int alertWebhookTimeout) {this.alertWebhookTimeout = alertWebhookTimeout;}
    public int getAlertWebhookRetryCount() {return alertWebhookRetryCount;}
    public void setAlertWebhookRetryCount(int alertWebhookRetryCount) {this.alertWebhookRetryCount = alertWebhookRetryCount;}
}
