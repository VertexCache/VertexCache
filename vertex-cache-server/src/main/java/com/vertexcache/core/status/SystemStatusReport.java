package com.vertexcache.core.status;

import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.socket.SocketServer;

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
        for (var snapshot : ModuleRegistry.getInstance().getModuleSnapshots()) {
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

    public static String getFullSystemReport() {
        return String.join(System.lineSeparator(),
                getServerStatus(),
                getSecuritySummary(),
                getModuleStatus(),
                getServerMemoryStatus()
        );
    }
}
