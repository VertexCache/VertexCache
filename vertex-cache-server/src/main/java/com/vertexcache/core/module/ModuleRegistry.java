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

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class ModuleRegistry {

    private static final ModuleRegistry instance = new ModuleRegistry();
    public static ModuleRegistry getInstance() {
        return instance;
    }

    private static class ModuleInfo {
        final String name;
        ModuleStatus status;
        String message;
        Instant timestamp;

        ModuleInfo(String name, ModuleStatus status, String message) {
            this.name = name;
            this.status = status;
            this.message = message;
            this.timestamp = Instant.now();
        }
    }

    private static final List<ModuleInfo> moduleResults = new ArrayList<>();
    private static final Map<String, ModuleHandler> activeModules = new HashMap<>();

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
        if (!enabled) {
            moduleResults.add(new ModuleInfo(name, ModuleStatus.DISABLED, null));
            return;
        }

        try {
            ModuleHandler module = factory.get();
            module.start();
            activeModules.put(name, module);
            moduleResults.add(new ModuleInfo(name, ModuleStatus.ENABLED, null));
        } catch (Exception e) {
            reportError(name, ModuleStatus.ERROR_LOAD, e.getMessage());
        }
    }

    public void reportError(Class<? extends ModuleHandler> moduleClass, String message) {
        reportError(moduleClass.getSimpleName(), ModuleStatus.ERROR_LOAD, message);
    }

    public void reportRuntimeError(Class<? extends ModuleHandler> moduleClass, String message) {
        reportError(moduleClass.getSimpleName(), ModuleStatus.ERROR_RUNTIME, message);
    }

    private void reportError(String name, ModuleStatus status, String message) {
        moduleResults.add(new ModuleInfo(name, status, message));
        activeModules.remove(name);
        LogHelper.getInstance().logError("[MODULES] " + name + " reported " + status + ": " + message);
    }

    public void stopModules() {
        for (ModuleHandler module : activeModules.values()) {
            try {
                module.stop();
            } catch (Exception e) {
                LogHelper.getInstance().logError("[MODULES] Error stopping module: " + module.getClass().getSimpleName());
            }
        }
    }

    public String getLoadedModulesDisplay() {
        StringBuilder sb = new StringBuilder("  Modules Loaded:").append(System.lineSeparator());
        for (ModuleSnapshot snapshot : getModuleSnapshots()) {
            sb.append("    ")
                    .append(snapshot.name()).append(": ")
                    .append(snapshot.status());
            if (snapshot.runtimeStatus() != null && !snapshot.runtimeStatus().isBlank()) {
                sb.append(" | ").append(snapshot.runtimeStatus());
            }
            if (snapshot.message() != null && !snapshot.message().isBlank()) {
                sb.append(" | ").append(snapshot.message());
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public List<ModuleSnapshot> getModuleSnapshots() {
        List<ModuleSnapshot> snapshots = new ArrayList<>();

        for (ModuleInfo info : moduleResults) {
            ModuleHandler handler = activeModules.get(info.name);

            ModuleStatus currentStatus = info.status;
            String runtimeStatus = "N/A";
            String message = info.message;

            if (handler instanceof Module module) {
                currentStatus = module.getModuleStatus();
                runtimeStatus = module.getStatusSummary();
                message = module.getStatusMessage();
            }

            snapshots.add(new ModuleSnapshot(
                    info.name,
                    currentStatus,
                    runtimeStatus,
                    message,
                    info.timestamp
            ));
        }

        return snapshots;
    }

    public boolean isModuleLoaded(Class<? extends ModuleHandler> moduleClass) {
        return activeModules.values().stream().anyMatch(m -> m.getClass().equals(moduleClass));
    }

    public <T extends ModuleHandler> Optional<T> getModule(Class<T> moduleClass) {
        return activeModules.values().stream()
                .filter(m -> m.getClass().equals(moduleClass))
                .map(moduleClass::cast)
                .findFirst();
    }

    public List<String> getActiveModuleNames() {
        return new ArrayList<>(activeModules.keySet());
    }

    public Optional<ModuleHandler> getModuleByName(String name) {
        return Optional.ofNullable(activeModules.get(name));
    }
}
