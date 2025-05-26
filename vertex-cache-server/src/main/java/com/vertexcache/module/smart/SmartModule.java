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
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.smart.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    @Override
    protected void onValidate() {
        var config = Config.getInstance();

        try {
            if(!config.getMetricConfigLoader().isEnableMetric() || !config.getAlertConfigLoader().isEnableAlerting()) {
                throw new VertexCacheValidationException("SmartModule requires both MetricModule and AlertModule enabled, 'enable_metric' and 'enable_alerting=true'");
            }

        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }
    }

    @Override
    protected void onStart() {
        try {
            List<BaseAlertService> enabledServices = new ArrayList<>();

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

            // Register only enabled services
            for (BaseAlertService service : enabledServices) {
                service.start(smartScheduler);
            }

            this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
        } catch (VertexCacheException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
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
