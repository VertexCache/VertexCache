package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class AlertConfigLoader extends LoaderBase {

    private boolean enableAlerting;
    private String alertWebhookUrl;
    private String alertWebhookSigningUrl;
    private int alertWebhookTimeout;
    private int alertWebhookRetryCount;

    @Override
    public void load() {
        this.enableAlerting = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_ALERTING,ConfigKey.ENABLE_ALERTING_DEFAULT);
        this.alertWebhookUrl = this.getConfigLoader().getProperty(ConfigKey.ALERT_WEBHOOK_URL,"");
        this.alertWebhookSigningUrl = this.getConfigLoader().getProperty(ConfigKey.ALERT_WEBHOOK_SIGNING_SECRET,"");
        this.alertWebhookTimeout = this.getConfigLoader().getIntProperty(ConfigKey.ALERT_WEBHOOK_TIMEOUT,ConfigKey.ALERT_WEBHOOK_TIMEOUT_DEFAULT);
        this.alertWebhookRetryCount = this.getConfigLoader().getIntProperty(ConfigKey.ALERT_WEBHOOK_RETRY_COUNT,ConfigKey.ALERT_WEBHOOK_RETRY_COUNT_DEFAULT);
    }

    public boolean isEnableAlerting() {
        return enableAlerting;
    }
    public void setEnableAlerting(boolean enableAlerting) {
        this.enableAlerting = enableAlerting;
    }
    public String getAlertWebhookUrl() {return alertWebhookUrl;}
    public void setAlertWebhookUrl(String alertWebhookUrl) {this.alertWebhookUrl = alertWebhookUrl;}
    public String getAlertWebhookSigningUrl() {return alertWebhookSigningUrl;}
    public void setAlertWebhookSigningUrl(String alertWebhookSigningUrl) {this.alertWebhookSigningUrl = alertWebhookSigningUrl;}
    public int getAlertWebhookTimeout() {return alertWebhookTimeout;}
    public void setAlertWebhookTimeout(int alertWebhookTimeout) {this.alertWebhookTimeout = alertWebhookTimeout;}
    public int getAlertWebhookRetryCount() {return alertWebhookRetryCount;}
    public void setAlertWebhookRetryCount(int alertWebhookRetryCount) {this.alertWebhookRetryCount = alertWebhookRetryCount;}
}
