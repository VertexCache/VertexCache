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
package com.vertexcache.module.smart;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.module.smart.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * SmartModule coordinates a set of intelligent background services and alerts within VertexCache.
 *
 * It manages scheduled tasks such as:
 * - Hot key watcher alerts
 * - Reverse index cleanup
 * - Key churn alerts
 * - Unauthorized access alerts
 * - Hot key anomaly detection
 *
 * On startup, it validates that the MetricModule is enabled, then initializes and
 * starts configured alert and maintenance services based on user configuration.
 * Services run on a dedicated single-threaded scheduled executor.
 *
 * On shutdown, it gracefully stops all running services and shuts down the scheduler.
 */
public class SmartModule extends Module {

    private final ScheduledExecutorService smartScheduler =
            Executors.newSingleThreadScheduledExecutor(
                    new ThreadFactoryBuilder().setNameFormat("SmartModule-%d").setDaemon(true).build()
            );

    private ReverseIndexCleanupService reverseIndexCleanupService;
    private HotKeyWatcherAlertService hotKeyWatcherAlertService;
    private KeyChurnAlertService keyChurnAlertService;
    private UnauthorizedAccessAlertService unauthorizedAccessAlertService;
    private HotKeyAnomalyAlertService hotKeyAnomalyAlertService;

    List<BaseAlertService> enabledServices = new ArrayList<>();

    @Override
    protected void onValidate() {
        var config = Config.getInstance();

        try {
            if(!config.getMetricConfigLoader().isEnableMetric()) {
                throw new VertexCacheValidationException("SmartModule requires both MetricModule enabled, 'enable_metric'");
            }

        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }
    }

    @Override
    protected void onStart() {
        try {

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartHotkeyWatcherAlert()) {
                this.hotKeyWatcherAlertService = new HotKeyWatcherAlertService();
                enabledServices.add(hotKeyWatcherAlertService);
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartIndexCleanup()) {
                this.reverseIndexCleanupService = new ReverseIndexCleanupService();
                enabledServices.add(reverseIndexCleanupService);
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartKeyChurnAlert()) {
                this.keyChurnAlertService = new KeyChurnAlertService();
                enabledServices.add(keyChurnAlertService);
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartUnauthorizedAccessAlert()) {
                this.unauthorizedAccessAlertService = new UnauthorizedAccessAlertService();
                enabledServices.add(unauthorizedAccessAlertService);
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartHotkeyAnomalyAlert()) {
                this.hotKeyAnomalyAlertService = new HotKeyAnomalyAlertService();
                enabledServices.add(hotKeyAnomalyAlertService);
            }

            if(Config.getInstance().getSmartConfigLoader().isEnableSmart()) {
                this.startService();
                this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
            }
        } catch (VertexCacheException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }
    }

    public void startService() {
        for (BaseAlertService service : enabledServices) {
            service.start(smartScheduler);
        }
    }

    @Override
    protected void onStop() {
        if (hotKeyWatcherAlertService != null) {
            hotKeyWatcherAlertService.stop();
        }
        if (reverseIndexCleanupService != null) {
            reverseIndexCleanupService.stop();
        }
        if (keyChurnAlertService != null) {
            keyChurnAlertService.stop();
        }
        if (unauthorizedAccessAlertService != null) {
            unauthorizedAccessAlertService.stop();
        }
        if (hotKeyAnomalyAlertService != null) {
            hotKeyAnomalyAlertService.stop();
        }

        smartScheduler.shutdown();
    }

    public UnauthorizedAccessAlertService getUnauthorizedAccessAlertService() {
        return unauthorizedAccessAlertService;
    }
}
