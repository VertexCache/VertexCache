package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.List;

public class ConfigCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "CONFIG";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        Config cfg = Config.getInstance();
        EncryptionMode mode = cfg.getEncryptionMode();

        List<String> fields = List.of(
                "config_path=" + cfg.getConfigFilePath(),
                "port=" + cfg.getServerPort(),
                "verbose=" + cfg.isEnableVerbose(),
                "encryption_mode=" + mode,
                "tls_enabled=" + cfg.isEncryptTransport(),
                "private_key=" + (mode == EncryptionMode.ASYMMETRIC ? "ENABLED" : "DISABLED"),
                "shared_key=" + (mode == EncryptionMode.SYMMETRIC ? "ENABLED" : "DISABLED"),
                "auth_enabled=" + cfg.isAuthEnabled(),
                "tenant_key_prefixing=" + cfg.isTenantKeyPrefixingEnabled(),
                "rate_limit_enabled=" + cfg.isRateLimitEnabled(),
                "rate_limit_tokens_per_sec=" + cfg.getRateLimitTokensTerSecond(),
                "rate_limit_burst=" + cfg.getRateLimitBurst(),
                "module_metric=" + cfg.isMetricEnabled(),
                "module_clustering=" + cfg.isClusteringEnabled(),
                "module_exporter=" + cfg.isExporterEnabled(),
                "module_intelligence=" + cfg.isIntelligenceEnabled(),
                "module_admin=" + cfg.isAdminCommandsEnabled(),
                "module_alerting=" + cfg.isAlertingEnabled(),
                "module_rest_api=" + cfg.isRestApiEnabled()
        );

        CommandResponse response = new CommandResponse();
        response.setResponseFromArray(fields);
        return response;
    }
}
