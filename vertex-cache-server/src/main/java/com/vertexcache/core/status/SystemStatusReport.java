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
package com.vertexcache.core.status;

import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.core.module.ModuleHandler;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.socket.SocketServer;
import com.vertexcache.core.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SystemStatusReport {

    public static List<String> getFullSystemReportAsFlat() {
        List<String> result = new ArrayList<>();
        result.addAll(getStatusSummaryAsFlat());
        result.addAll(getSecuritySummaryAsFlat());
        result.addAll(getModuleStatusAsFlat());
        result.addAll(getClusterSummaryAsFlat());
        result.addAll(getRestApiAsFlat());
        result.addAll(getAlertAsFlat());
        result.addAll(getMemoryStatusSummaryAsFlat());
        return result;
    }

    public static String getFullSystemReport() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary(),
                getModuleStatus(),
                getClusterSummary(),
                getRestApiSummary(),
                getAlertSummary(),
                getServerMemoryStatus()
        );
    }

    public static String getStartupSystemReport() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary()
        );
    }

    public static List<String> getStatusSummaryAsFlat() {
        Config config = Config.getInstance();
        List<String> flat = new ArrayList<>();
        flat.add("status=" + SocketServer.getStartupStatus());
        flat.add("version=" + VersionUtil.getAppVersion());
        flat.add("port=" + config.getCoreConfigLoader().getServerPort());
        flat.add("verbose=" + config.getCoreConfigLoader().isEnableVerbose());
        flat.add("cache_eviction_policy=" + config.getCacheConfigLoader().getCacheEvictionPolicy());
        flat.add("cache_size=" + config.getCacheConfigLoader().getCacheSize());
        flat.add("config_file_set=" + config.isConfigLoaded());
        flat.add("config_file_error=" + config.isConfigError());
        flat.add("config_file_path=" + (config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a"));
        return flat;
    }

    public static List<String> getMemoryStatusSummaryAsFlat() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() / (1024 * 1024);
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        List<String> flat = new ArrayList<>();
        flat.add("memory_used_mb=" + usedMem);
        flat.add("memory_max_mb=" + maxMem);
        return flat;
    }

    public static List<String> getSecuritySummaryAsFlat() {
        Config config = Config.getInstance();
        List<String> flat = new ArrayList<>();
        flat.add("tls_enabled=" + config.getSecurityConfigLoader().isEncryptTransport());
        flat.add("message_encryption=" + (config.getSecurityConfigLoader().getEncryptionMode() != EncryptionMode.NONE));
        flat.add("private_key=" + config.getSecurityConfigLoader().isEncryptWithPrivateKey());
        flat.add("shared_key=" + config.getSecurityConfigLoader().isEncryptWithSharedKey());
        return flat;
    }

    public static List<String> getModuleStatusAsFlat() {
        List<String> flat = new ArrayList<>();
        Map<String, ModuleHandler> modules = ModuleRegistry.getInstance().getAllModules();

        for (Map.Entry<String, ModuleHandler> entry : modules.entrySet()) {
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

            flat.add("module_" + name + "=" + status.name());
            if (!runtimeStatus.isEmpty()) {
                flat.add("module_" + name + "_runtime=" + runtimeStatus);
            }
            if (!message.isEmpty()) {
                flat.add("module_" + name + "_message=" + message);
            }
        }

        return flat;
    }

    public static List<String> getClusterSummaryAsFlat() {
        List<String> flat = new ArrayList<>();
        Config config = Config.getInstance();
        if (config.getClusterConfigLoader().isEnableClustering()) {
            flat.addAll(config.getClusterConfigLoader().getFlatSummary().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toList());
        }

        return flat;
    }

    public static List<String> getRestApiAsFlat() {
        List<String> flat = new ArrayList<>();
        Config config = Config.getInstance();
        if (config.getRestApiConfigLoader().isEnableRestApi()) {
            flat.addAll(config.getRestApiConfigLoader().getFlatSummary().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toList());
        }
        return flat;
    }

    public static List<String> getAlertAsFlat() {
        List<String> flat = new ArrayList<>();
        Config config = Config.getInstance();
        if (config.getAlertConfigLoader().isEnableAlerting()) {
            flat.addAll(config.getAlertConfigLoader().getFlatSummary().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toList());
        }
        return flat;
    }

    public static String getServerStatus() {
        Config config = Config.getInstance();

        String port = String.valueOf(config.getCoreConfigLoader().getServerPort());
        if(Config.getInstance().getClusterConfigLoader().isEnableClustering()) {
            port = Config.getInstance().getClusterConfigLoader().getLocalClusterNode().getPort();
        }

        return config.getCoreConfigLoader().getAppName() + " Server Startup Report:" + System.lineSeparator() +
                "  Status: " + SocketServer.getStartupStatus() + (!SocketServer.getStartupMessage().isEmpty() ? " | " + SocketServer.getStartupMessage() : "")+ System.lineSeparator() +
                "  Version: " + VersionUtil.getAppVersion() + System.lineSeparator() +
                "  Port: " + port + System.lineSeparator() +
                "  Verbose: " + (config.getCoreConfigLoader().isEnableVerbose() ? "ENABLED" : "DISABLED") + System.lineSeparator() +
                "  Cache Eviction Policy: " + config.getCacheConfigLoader().getCacheEvictionPolicy() + System.lineSeparator() +
                "  Cache Size: " + config.getCacheConfigLoader().getCacheSize() + System.lineSeparator() +
                "  Config file set: " + (config.isConfigLoaded() ? "Yes" : "No") + System.lineSeparator() +
                "  Config file loaded with no errors: " + (!config.isConfigError() ? "Yes" : "No") + System.lineSeparator() +
                "  Config file location: " + (config.getConfigFilePath() != null ? config.getConfigFilePath() : "n/a") + System.lineSeparator();
    }

    public static String getSecuritySummary() {
        Config config = Config.getInstance();
        return "  Encryption Summary: " + System.lineSeparator() +
                "    TLS Enabled (Transport): " + (config.getSecurityConfigLoader().isEncryptTransport() ? "ENABLED" : "DISABLED") + System.lineSeparator() +
                "    Message Layer Encrypted: " + (config.getSecurityConfigLoader().getEncryptionMode() != EncryptionMode.NONE ? "ENABLED" : "DISABLED") + config.getSecurityConfigLoader().getEncryptNote() + System.lineSeparator() +
                "      Private/Public Key (RSA): " + (config.getSecurityConfigLoader().isEncryptWithPrivateKey() ? "ENABLED" : "DISABLED") + System.lineSeparator() +
                "      Shared Key (AES): " + (config.getSecurityConfigLoader().isEncryptWithSharedKey() ? "ENABLED" : "DISABLED") + System.lineSeparator();
    }

    public static String getModuleStatus() {
        StringBuilder sb = new StringBuilder("  Modules Loaded:").append(System.lineSeparator());
        Map<String, ModuleHandler> modules = ModuleRegistry.getInstance().getAllModules();
        for (Map.Entry<String, ModuleHandler> entry : modules.entrySet()) {
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
            if (!runtimeStatus.isEmpty()) sb.append(" | ").append(runtimeStatus);
            if (!message.isEmpty()) sb.append(" | ").append(message);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public static String getClusterSummary() {
        Config config = Config.getInstance();
        StringBuilder sb = new StringBuilder();

        if (config.getClusterConfigLoader().isEnableClustering()) {
            List<String> clusterLines = config.getClusterConfigLoader().getTextSummary();
            if (!clusterLines.isEmpty()) {
                sb.append("  Cluster Summary:").append(System.lineSeparator());
                for (String line : clusterLines) {
                    sb.append("    ").append(line).append(System.lineSeparator());
                }
            }
        } else {
            sb.append("  Cluster Summary: N/A").append(System.lineSeparator());
        }

        return sb.toString();
    }

    public static String getRestApiSummary() {
        Config config = Config.getInstance();
        StringBuilder sb = new StringBuilder();

        if (config.getRestApiConfigLoader().isEnableRestApi()) {
            List<String> restApiLines = config.getRestApiConfigLoader().getTextSummary();
            if (!restApiLines.isEmpty()) {
                sb.append("  REST API Summary:").append(System.lineSeparator());
                for (String line : restApiLines) {
                    sb.append("    ").append(line).append(System.lineSeparator());
                }
            }
        } else {
            sb.append("  REST API Summary: N/A").append(System.lineSeparator());
        }

        return sb.toString();
    }

    public static String getAlertSummary() {
        Config config = Config.getInstance();
        StringBuilder sb = new StringBuilder();

        if (config.getAlertConfigLoader().isEnableAlerting()) {
            List<String> alertLines = config.getAlertConfigLoader().getTextSummary();
            if (!alertLines.isEmpty()) {
                sb.append("  Alert Summary:").append(System.lineSeparator());
                for (String line : alertLines) {
                    sb.append("    ").append(line).append(System.lineSeparator());
                }
            }
        } else {
            sb.append("  Alert Summary: N/A").append(System.lineSeparator());
        }

        return sb.toString();
    }


    public static String getServerMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() / (1024 * 1024);
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        return "  Memory Status: " + System.lineSeparator() +
                "    Used Memory: " + usedMem + " MB" + System.lineSeparator() +
                "    Max Memory: " + maxMem + " MB" + System.lineSeparator();
    }

    public static String getStatusSummaryAsPretty() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary(),
                getModuleStatus(),
                getClusterSummary(),
                getServerMemoryStatus()
        );
    }

}
