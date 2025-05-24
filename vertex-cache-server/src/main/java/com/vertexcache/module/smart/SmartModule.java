package com.vertexcache.module.smart;

import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.smart.service.HotKeyWatcherAlertService;
import com.vertexcache.module.smart.service.ReverseIndexCleanupService;

public class SmartModule extends Module {

    private HotKeyWatcherAlertService hotKeyWatcherAlertService;
    private ReverseIndexCleanupService reverseIndexCleanupService;

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
            if (Config.getInstance().getSmartConfigLoader().isEnableSmartHotkeyWatcherAlert()) {
                this.hotKeyWatcherAlertService = new HotKeyWatcherAlertService();
                this.hotKeyWatcherAlertService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] HotKeyWatcherService initialized");
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartIndexCleanup()) {
                // Run the index sweeper every hour, move this to a config option later
                this.reverseIndexCleanupService = new ReverseIndexCleanupService(3_600_000);
                this.reverseIndexCleanupService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] ReverseIndexSweeperService initialized");
            }

            this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
        } catch (VertexCacheException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }

    }

    @Override
    protected void onStop() {
        if (this.hotKeyWatcherAlertService != null) {
            this.hotKeyWatcherAlertService.shutdown();
        }
        if(this.reverseIndexCleanupService != null) {
            this.reverseIndexCleanupService.shutdown();
        }
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

}
