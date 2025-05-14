package com.vertexcache.module.alert;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.RetryCountValidator;
import com.vertexcache.core.validation.validators.TimeoutMsValidator;
import com.vertexcache.core.validation.validators.UrlValidator;

public class AlertModule  extends Module {

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
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

}
