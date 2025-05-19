package com.vertexcache.module.alert;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.RetryCountValidator;
import com.vertexcache.core.validation.validators.TimeoutMsValidator;
import com.vertexcache.core.validation.validators.UrlValidator;
import com.vertexcache.module.alert.listeners.ClusterNodeEventListener;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;
import com.vertexcache.module.alert.service.AlertWebhookDispatcher;

import java.util.Map;

public class AlertModule  extends Module implements ClusterNodeEventListener {

    private AlertWebhookDispatcher alertWebhookDispatcher;

    @Override
    protected void onValidate() {

        var config = Config.getInstance().getAlertConfigLoader();

        try {
            new UrlValidator(config.getAlertWebhookUrl(), "Alert Webhook URL").validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }

        try {
            new TimeoutMsValidator(config.getAlertWebhookTimeout(), "Alert webhook timeout").validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }

        try {
            new RetryCountValidator(config.getAlertWebhookRetryCount(), "Alert webhook retry count").validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }

    }

    @Override
    protected void onStart() {
        this.alertWebhookDispatcher = new AlertWebhookDispatcher(
                Config.getInstance().getAlertConfigLoader().getAlertWebhookUrl(),
                Config.getInstance().getAlertConfigLoader().isAlertWebhookSigningEnabled(),
                Config.getInstance().getAlertConfigLoader().getAlertWebhookSigningSecret(),
                Config.getInstance().getAlertConfigLoader().getAlertWebhookTimeout(),
                Config.getInstance().getAlertConfigLoader().getAlertWebhookRetryCount()
        );

        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    public void onSecondaryNodePromotedToPrimary(String nodeId) {
        LogHelper.getInstance().logInfo("[AlertModule] Send off Alert Secondary Node Promoted to Primary");
        //AlertEvent alertEvent = new AlertEvent(AlertEventType.PRIMARY_PROMOTED, nodeId);
        //this.alertWebhookDispatcher.dispatch(alertEvent);
    }
}
