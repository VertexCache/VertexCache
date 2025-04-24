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

import java.util.*;
import java.util.function.Supplier;

public class ModuleRegistry {

    private static final ModuleRegistry instance = new ModuleRegistry();

    private ModuleRegistry() {
    }

    public static ModuleRegistry getInstance() {
        return instance;
    }

    private final Map<String, ModuleHandler> allModules = new LinkedHashMap<>();

    public void loadModules() {
        Config config = Config.getInstance();

        register(ModuleName.AUTH, config.getAuthWithTenantConfigLoader().isAuthEnabled(), AuthModule::new);
        register(ModuleName.RATELIMITER, config.getRateLimitingConfigLoader().isRateLimitEnabled(), RateLimiterModule::new);
        register(ModuleName.METRIC, config.isMetricEnabled(), MetricModule::new);
        register(ModuleName.REST_API, config.isRestApiEnabled(), RestApiModule::new);
        register(ModuleName.CLUSTER, config.isClusteringEnabled(), ClusterModule::new);
        register(ModuleName.ADMIN, config.getAdminConfigLoader().isAdminCommandsEnabled(), AdminModule::new);
        register(ModuleName.ALERT, config.getAlertConfigLoader().isEnableAlerting(), AlertModule::new);
        register(ModuleName.INTELLIGENCE, config.isIntelligenceEnabled(), IntelligenceModule::new);
        register(ModuleName.METRIC_EXPORTER, config.isExporterEnabled(), MetricExporterModule::new);
    }

    private void register(ModuleName moduleName, boolean enabled, Supplier<ModuleHandler> factory) {
        String name = moduleName.getValue();
        ModuleHandler module = factory.get();

        if (!enabled && module instanceof Module m) {
            m.setModuleStatus(ModuleStatus.DISABLED);
        }

        try {
            if (enabled) {
                module.start();
            }
        } catch (Exception e) {
            LogHelper.getInstance().logError("[MODULES] " + name + " failed to start: " + e.getMessage());
            if (module instanceof Module m) {
                m.reportHealth(ModuleStatus.ERROR_LOAD, e.getMessage());
            }
        }

        allModules.put(name, module);
    }

    public void stopModules() {
        for (ModuleHandler module : allModules.values()) {
            try {
                module.stop();
            } catch (Exception e) {
                LogHelper.getInstance().logError("[MODULES] Error stopping module: " + module.getClass().getSimpleName());
            }
        }
    }

    public String getLoadedModulesDisplay() {
        StringBuilder sb = new StringBuilder("  Modules Loaded:").append(System.lineSeparator());

        for (Map.Entry<String, ModuleHandler> entry : allModules.entrySet()) {
            String name = entry.getKey();
            ModuleHandler module = entry.getValue();

            ModuleStatus status = ModuleStatus.ENABLED;
            String runtimeStatus = "";
            String message = "";

            if (module instanceof Module m) {
                status = m.getModuleStatus();
                runtimeStatus = m.getStatusSummary();
                message = m.getStatusMessage();
            }

            sb.append("    ").append(name).append(": ").append(status);
            if (!runtimeStatus.isBlank()) sb.append(" | ").append(runtimeStatus);
            if (!message.isBlank()) sb.append(" | ").append(message);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public boolean isModuleLoaded(Class<? extends ModuleHandler> moduleClass) {
        return allModules.values().stream().anyMatch(m -> m.getClass().equals(moduleClass));
    }

    public <T extends ModuleHandler> Optional<T> getModule(Class<T> moduleClass) {
        return allModules.values().stream()
                .filter(m -> m.getClass().equals(moduleClass))
                .map(moduleClass::cast)
                .findFirst();
    }

    public Optional<ModuleHandler> getModuleByName(String name) {
        return Optional.ofNullable(allModules.get(name));
    }

    public Optional<ModuleHandler> getModuleByEnum(ModuleName moduleName) {
        return getModuleByName(moduleName.getValue());
    }

    public Map<String, ModuleHandler> getAllModules() {
        return allModules;
    }

    public List<String> getAllModuleNames() {
        return new ArrayList<>(allModules.keySet());
    }
}
