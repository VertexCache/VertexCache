package com.vertexcache.core.status;

import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.version.VersionUtil;
import com.vertexcache.core.module.ModuleHandler;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.socket.SocketServer;
import com.vertexcache.core.module.Module;

import java.util.Map;

public class SystemStatusReport {

    public static String getSecuritySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("  Encryption Summary: ").append(System.lineSeparator())
                .append("    TLS Enabled (Transport): ").append(Config.getInstance().isEncryptTransport() ? "ENABLED" : "DISABLED").append(System.lineSeparator())
                .append("    Message Layer Encrypted: ").append(Config.getInstance().getEncryptionMode() != EncryptionMode.NONE ? "ENABLED" : "DISABLED").append(Config.getInstance().getEncryptNote()).append(System.lineSeparator())
                .append("      Private/Public Key (RSA): ").append(Config.getInstance().isEncryptWithPrivateKey() ? "ENABLED" : "DISABLED").append(System.lineSeparator())
                .append("      Shared Key (AES): ").append(Config.getInstance().isEncryptWithSharedKey() ? "ENABLED" : "DISABLED").append(System.lineSeparator());
        return sb.toString();
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

            if (module instanceof Module) {
                Module m = (Module) module;
                status = m.getModuleStatus();
                runtimeStatus = m.getStatusSummary();
                message = m.getStatusMessage();
            }

            sb.append("    ").append(name).append(": ").append(status);
            if (runtimeStatus != null && !runtimeStatus.isEmpty()) {
                sb.append(" | ").append(runtimeStatus);
            }
            if (message != null && !message.isEmpty()) {
                sb.append(" | ").append(message);
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }


    public static String getServerMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory() / (1024 * 1024);
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        StringBuilder sb = new StringBuilder();
        sb
                .append("  Memory Status: ").append(System.lineSeparator())
                .append("    Used Memory: ").append(usedMem).append(" MB").append(System.lineSeparator())
                .append("    Max Memory: ").append(maxMem).append(" MB").append(System.lineSeparator());
        return sb.toString();
    }

    public static String getServerStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(Config.getInstance().getAppName()).append(" Server Startup Report:").append(System.lineSeparator())
                .append("  Status: ").append(SocketServer.getStartupStatus()).append(System.lineSeparator())
                .append("  Version: ").append(VersionUtil.getAppVersion()).append(System.lineSeparator())
                .append("  Port: ").append(Config.getInstance().getServerPort()).append(System.lineSeparator())
                .append("  Verbose: ").append(Config.getInstance().isEnableVerbose() ? "ENABLED" : "DISABLED").append(System.lineSeparator())
                .append("  Cache Eviction Policy: ").append(Config.getInstance().getCacheEvictionPolicy().toString()).append(System.lineSeparator())
                .append("  Cache Size: ").append(Config.getInstance().getCacheSize()).append(System.lineSeparator())
                .append("  Config file set: ").append(Config.getInstance().isConfigLoaded() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file loaded with no errors: ").append(!Config.getInstance().isConfigError() ? "Yes" : "No").append(System.lineSeparator())
                .append("  Config file location: ").append(Config.getInstance().getConfigFilePath() != null ? Config.getInstance().getConfigFilePath() : "n/a").append(System.lineSeparator());
        return sb.toString();
    }

    public static String getFullSystemReport() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary(),
                getModuleStatus(),
                getServerMemoryStatus()
        );
    }


}
