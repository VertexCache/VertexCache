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
import com.vertexcache.module.alert.service.AlertExecutorService;
import com.vertexcache.module.alert.service.AlertWebhookDispatcher;

import java.util.Map;

public class AlertModule  extends Module implements ClusterNodeEventListener {

    private AlertExecutorService executorService;
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
        this.executorService = new AlertExecutorService(alertWebhookDispatcher);
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    public void onSecondaryNodePromotedToPrimary(String nodeId) {
        LogHelper.getInstance().logInfo("[AlertModule] Send off Alert Secondary Node Promoted to Primary");
        AlertEvent alertEvent = new AlertEvent(AlertEventType.PRIMARY_PROMOTED, nodeId);
        executorService.dispatchAsync(alertEvent);
    }

    public void dispatch(AlertEvent alertEvent) {
        executorService.dispatchAsync(alertEvent);
    }
}
