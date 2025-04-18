package com.vertexcache.core.status;

import com.vertexcache.common.protocol.EncryptionMode;
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

    public static String getServerStatus() {
        return SocketServer.getStatusSummary();
    }

    public static String getServerMemoryStatus() { return SocketServer.getMemoryStatusSummary(); }

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


    public static String getFullSystemReport() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary(),
                getModuleStatus(),
                getServerMemoryStatus()
        );
    }


}
