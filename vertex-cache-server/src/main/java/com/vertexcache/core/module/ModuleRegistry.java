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
        System.out.println("[DEBUG] ModuleRegistry initialized → " + System.identityHashCode(this));
    }

    public static ModuleRegistry getInstance() {
        return instance;
    }

    private final Map<String, ModuleHandler> allModules = new LinkedHashMap<>();

    public void loadModules() {
        Config config = Config.getInstance();

        register("AuthModule", config.isAuthEnabled(), AuthModule::new);
        register("RateLimiterModule", config.isRateLimitEnabled(), RateLimiterModule::new);
        register("MetricModule", config.isMetricEnabled(), MetricModule::new);
        register("RestApiModule", config.isRestApiEnabled(), RestApiModule::new);
        register("ClusterModule", config.isClusteringEnabled(), ClusterModule::new);
        register("AdminModule", config.isAdminCommandsEnabled(), AdminModule::new);
        register("AlertModule", config.isAlertingEnabled(), AlertModule::new);
        register("IntelligenceModule", config.isIntelligenceEnabled(), IntelligenceModule::new);
        register("MetricExporterModule", config.isExporterEnabled(), MetricExporterModule::new);
    }

    private void register(String name, boolean enabled, Supplier<ModuleHandler> factory) {
        ModuleHandler module = factory.get();

        if (!enabled && module instanceof Module m) {
            m.setModuleStatus(ModuleStatus.DISABLED);
        }

        try {
            if (enabled) {
                module.start();
                if (module instanceof Module m) {
                   // m.setModuleStatus(ModuleStatus.ENABLED);
                }
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

    public List<String> getAllModuleNames() {
        return new ArrayList<>(allModules.keySet());
    }

    public Optional<ModuleHandler> getModuleByName(String name) {
        return Optional.ofNullable(allModules.get(name));
    }

    public Map<String, ModuleHandler> getAllModules() {
        return allModules;
    }
}
