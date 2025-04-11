package com.vertexcache.core.module;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.AuthModule;
import com.vertexcache.module.ratelimiter.RateLimiterModule;
import com.vertexcache.module.metric.MetricModule;
import com.vertexcache.module.rest.RestApiModule;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.admin.AdminModule;
import com.vertexcache.module.alert.AlertModule;
import com.vertexcache.module.intelligence.IntelligenceModule;
import com.vertexcache.module.exporter.MetricExporterModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleRegistry {

    private static final List<ModuleHandler> activeModules = new ArrayList<>();

    public void loadModules() {
        Config config = Config.getInstance();

        if (config.isAuthEnabled()) {
            register(new AuthModule());
        }

        if (config.isRateLimitEnabled()) {
            register(new RateLimiterModule());
        }

        if (config.isMetricEnabled()) {
            register(new MetricModule());
        }

        if (config.isRestApiEnabled()) {
            register(new RestApiModule());
        }

        if (config.isClusteringEnabled()) {
            register(new ClusterModule());
        }

        if (config.isAdminCommandsEnabled()) {
            register(new AdminModule());
        }

        if (config.isAlertingEnabled()) {
            register(new AlertModule());
        }

        if (config.isIntelligenceEnabled()) {
            register(new IntelligenceModule());
        }

        if (config.isExporterEnabled()) {
            register(new MetricExporterModule());
        }
    }

    private void register(ModuleHandler module) {
        module.start();
        activeModules.add(module);
    }

    public void stopModules() {
        for (ModuleHandler module : activeModules) {
            module.stop();
        }
    }

    public List<String> getLoadedModuleNames() {
        List<String> names = new ArrayList<>();
        for (ModuleHandler module : activeModules) {
            names.add(module.getClass().getSimpleName());
        }
        return names;
    }

    public boolean isModuleLoaded(Class<? extends ModuleHandler> moduleClass) {
        return activeModules.stream().anyMatch(m -> m.getClass().equals(moduleClass));
    }

    public <T extends ModuleHandler> Optional<T> getModule(Class<T> moduleClass) {
        return activeModules.stream()
                .filter(m -> m.getClass().equals(moduleClass))
                .map(moduleClass::cast)
                .findFirst();
    }

    public static String getLoadedModulesDisplay() {
        //LogHelper.getInstance().logInfo("[MODULES] Loaded modules:");
        StringBuilder sb = new StringBuilder("Modules Loaded:").append(System.lineSeparator());
        for (ModuleHandler module : activeModules) {
            //System.out.println(" - " + module.getClass().getSimpleName());
            sb.append("  - " + module.getClass().getSimpleName()).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
