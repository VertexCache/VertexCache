package com.vertexcache.module.smart;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.smart.service.HotKeyWatcherService;
import com.vertexcache.module.smart.service.ReverseIndexSweeperService;

public class SmartModule extends Module {

    private HotKeyWatcherService hotKeyWatcherService;
    private ReverseIndexSweeperService reverseIndexSweeperService;

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
            if (Config.getInstance().getSmartConfigLoader().isEnableSmartHotkeyWatcher()) {
                this.hotKeyWatcherService = new HotKeyWatcherService();
                this.hotKeyWatcherService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] HotKeyWatcherService initialized");
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartIndexCleanup()) {
                // Run the index sweeper every hour, move this to a config option later
                this.reverseIndexSweeperService = new ReverseIndexSweeperService(3_600_000);
                this.reverseIndexSweeperService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] ReverseIndexSweeperService initialized");
            }

            this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
        } catch (VertexCacheException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }

    }

    @Override
    protected void onStop() {
        if (this.hotKeyWatcherService != null) {
            this.hotKeyWatcherService.shutdown();
        }
        if(this.reverseIndexSweeperService != null) {
            this.reverseIndexSweeperService.shutdown();
        }
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

}
