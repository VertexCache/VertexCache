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
package com.vertexcache.core.module;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleHandler;
import com.vertexcache.core.module.model.ModuleName;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.AuthModule;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.module.ratelimiter.RateLimiterModule;
import com.vertexcache.module.metric.MetricModule;
import com.vertexcache.module.restapi.RestApiModule;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.admin.AdminModule;
import com.vertexcache.module.alert.AlertModule;
import com.vertexcache.module.smart.SmartModule;

import java.util.*;
import java.util.function.Supplier;

/**
 * Central registry for all available modules in the VertexCache system.
 *
 * Maintains mappings between ModuleName enums and their corresponding module instances.
 * Used by the ModuleHandler to initialize, start, shut down, and track status of each module.
 *
 * Also supports lookup operations, status inspection, and dependency resolution.
 *
 * Acts as the authoritative source for module discovery and lifecycle management.
 */
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
        register(ModuleName.ADMIN, config.getAdminConfigLoader().isAdminCommandsEnabled(), AdminModule::new);
        register(ModuleName.ALERT, config.getAlertConfigLoader().isEnableAlerting(), AlertModule::new);
        register(ModuleName.RATELIMITER, config.getRateLimitingConfigLoader().isRateLimitEnabled(), RateLimiterModule::new);
        register(ModuleName.METRIC, config.getMetricConfigLoader().isEnableMetric(), MetricModule::new);
        register(ModuleName.REST_API, config.getRestApiConfigLoader().isEnableRestApi(), RestApiModule::new);
        register(ModuleName.CLUSTER, config.getClusterConfigLoader().isEnableClustering(), ClusterModule::new);
        register(ModuleName.SMART, config.getSmartConfigLoader().isEnableSmart(), SmartModule::new);
        // Exporter Disabled for Now
        //register(ModuleName.EXPORTER, config.getExporterConfigLoader().isEnableExporter(), MetricExporterModule::new);
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

    public static Optional<MetricAccess> getMetricAccessIfEnabled() {
        boolean enabled = Config.getInstance().getMetricConfigLoader().isEnableMetric();

        if (!enabled) {
            return Optional.empty();
        }

        return getInstance()
                .getModule(MetricModule.class)
                .map(MetricModule::getMetricAccess);
    }

    public static void startRestApiModule() {
        if(Config.getInstance().getRestApiConfigLoader().isEnableRestApi()) {
            getInstance().getModule(RestApiModule.class).get().startService();
        } else {
            LogHelper.getInstance().logInfo("RestApiModule not enabled, will not start.");
        }
    }

    public static void startSmartModule() {
        if(Config.getInstance().getSmartConfigLoader().isEnableSmart()) {
            getInstance().getModule(SmartModule.class).get().startService();
        } else {
            LogHelper.getInstance().logInfo("SmartModule not enabled, will not start.");
        }
    }
}
