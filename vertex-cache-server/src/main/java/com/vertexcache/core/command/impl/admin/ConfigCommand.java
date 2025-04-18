
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.server.session.ClientSessionContext;

public class ConfigCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "CONFIG";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        if (!isAdminAccessAllowed()) return rejectIfAdminAccessNotAllowed();

        Config config = Config.getInstance();
        CommandResponse response = new CommandResponse();
        StringBuilder sb = new StringBuilder();

        sb.append("CONFIG\nOK\n");

        sb.append("\nCore Settings:\n");
        sb.append("  Config Path: ").append(config.getConfigFilePath()).append("\n");
        sb.append("  Port: ").append(config.getServerPort()).append("\n");
        sb.append("  Verbose: ").append(config.isEnableVerbose()).append("\n");

        sb.append("\nEncryption:\n");
        EncryptionMode mode = config.getEncryptionMode();
        sb.append("  Mode: ").append(mode).append("\n");
        sb.append("  TLS Enabled: ").append(config.isEncryptTransport()).append("\n");
        sb.append("  Private Key: ").append(mode == EncryptionMode.ASYMMETRIC ? "ENABLED" : "DISABLED").append("\n");
        sb.append("  Shared Key: ").append(mode == EncryptionMode.SYMMETRIC ? "ENABLED" : "DISABLED").append("\n");

        sb.append("\nAuth:\n");
        sb.append("  Enabled: ").append(config.isAuthEnabled()).append("\n");
        sb.append("  Tenant Key Prefixing: ").append(config.isTenantKeyPrefixingEnabled()).append("\n");

        sb.append("\nRate Limiting:\n");
        sb.append("  Enabled: ").append(config.isRateLimitEnabled()).append("\n");
        if (config.isRateLimitEnabled()) {
            sb.append("  Tokens/sec: ").append(config.getRateLimitTokensTerSecond()).append("\n");
            sb.append("  Burst: ").append(config.getRateLimitBurst()).append("\n");
        }

        sb.append("\nModule Flags:\n");
        sb.append("  Metric: ").append(config.isMetricEnabled()).append("\n");
        sb.append("  Clustering: ").append(config.isClusteringEnabled()).append("\n");
        sb.append("  Exporter: ").append(config.isExporterEnabled()).append("\n");
        sb.append("  Intelligence: ").append(config.isIntelligenceEnabled()).append("\n");
        sb.append("  Admin Commands: ").append(config.isAdminCommandsEnabled()).append("\n");
        sb.append("  Alerting: ").append(config.isAlertingEnabled()).append("\n");
        sb.append("  REST API: ").append(config.isRestApiEnabled()).append("\n");

        response.setResponse(sb.toString().trim());
        return response;
    }
}
