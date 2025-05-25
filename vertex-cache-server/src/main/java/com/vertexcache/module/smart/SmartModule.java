package com.vertexcache.module.smart;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.smart.service.HotKeyWatcherAlertService;
import com.vertexcache.module.smart.service.KeyChurnAlertService;
import com.vertexcache.module.smart.service.ReverseIndexCleanupService;

public class SmartModule extends Module {

    private ReverseIndexCleanupService reverseIndexCleanupService;
    private HotKeyWatcherAlertService hotKeyWatcherAlertService;
    private KeyChurnAlertService keyChurnAlertService;
    
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
                this.reverseIndexCleanupService = new ReverseIndexCleanupService();
                this.reverseIndexCleanupService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] ReverseIndexSweeperService initialized");
            }

            if (Config.getInstance().getSmartConfigLoader().isEnableSmartKeyChurnAlert()) {
                this.keyChurnAlertService = new KeyChurnAlertService();
                this.keyChurnAlertService.start();
                //LogHelper.getInstance().logInfo("[SmartModule] KeyChurnAlertService initialized");
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
        if(this.keyChurnAlertService != null) {
            this.keyChurnAlertService.shutdown();
        }
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

}
