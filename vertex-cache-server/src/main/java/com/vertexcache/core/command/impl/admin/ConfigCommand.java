package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;
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
        EncryptionMode mode = cfg.getSecurityConfigLoader().getEncryptionMode();

        boolean pretty = argumentParser.getPrimaryArgument().getArgs().size() == 1
                && argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY);

        CommandResponse response = new CommandResponse();

        if (pretty) {
            List<String> lines = new ArrayList<>();
            lines.add("Configuration Summary:");
            lines.add("----------------------");
            lines.add("Config Path:           " + cfg.getConfigFilePath());
            lines.add("Port:                  " + cfg.getCoreConfigLoader().getServerPort());
            lines.add("Verbose Logging:       " + cfg.getCoreConfigLoader().isEnableVerbose());
            lines.add("Encryption Mode:       " + mode);
            lines.add("  TLS Enabled:         " + cfg.getSecurityConfigLoader().isEncryptTransport());
            lines.add("  RSA Key Enabled:     " + (mode == EncryptionMode.ASYMMETRIC));
            lines.add("  AES Key Enabled:     " + (mode == EncryptionMode.SYMMETRIC));
            lines.add("Auth Enabled:          " + cfg.getAuthWithTenantConfigLoader().isAuthEnabled());
            lines.add("Tenant Key Prefixing:  " + cfg.getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled());
            lines.add("Rate Limiting:         " + cfg.getRateLimitingConfigLoader().isRateLimitEnabled());
            lines.add("  Tokens/sec:          " + cfg.getRateLimitingConfigLoader().getRateLimitTokensTerSecond());
            lines.add("  Burst Size:          " + cfg.getRateLimitingConfigLoader().getRateLimitBurst());
            lines.add("Modules:");
            lines.add("  Metric:              " + cfg.getMetricConfigLoader().isEnableMetric());
            lines.add("  Clustering:          " + cfg.getClusterConfigLoader().isEnableClustering());
            lines.add("  Exporter:            " + cfg.getExporterConfigLoader().isEnableExporter());
            lines.add("  Smart:        " + cfg.getSmartConfigLoader().isEnableSmart());
            lines.add("  Admin:               " + cfg.getAdminConfigLoader().isAdminCommandsEnabled());
            lines.add("  Alerting:            " + cfg.getAlertConfigLoader().isEnableAlerting());
            lines.add("  REST API:            " + cfg.getRestApiConfigLoader().isEnableRestApi());

            if (cfg.getClusterConfigLoader().isEnableClustering()) {
                lines.add("");
                lines.add("Cluster Config:");
                lines.add("---------------");
                cfg.getClusterConfigLoader().getFlatSummary().forEach((k, v) -> lines.add("  " + k + ": " + v));
            }

            response.setResponse(String.join(System.lineSeparator(), lines));
        } else {
            List<String> fields = new ArrayList<>(List.of(
                    "config_path=" + cfg.getConfigFilePath(),
                    "port=" + cfg.getCoreConfigLoader().getServerPort(),
                    "verbose=" + cfg.getCoreConfigLoader().isEnableVerbose(),
                    "encryption_mode=" + mode,
                    "tls_enabled=" + cfg.getSecurityConfigLoader().isEncryptTransport(),
                    "private_key=" + (mode == EncryptionMode.ASYMMETRIC ? "ENABLED" : "DISABLED"),
                    "shared_key=" + (mode == EncryptionMode.SYMMETRIC ? "ENABLED" : "DISABLED"),
                    "auth_enabled=" + cfg.getAuthWithTenantConfigLoader().isAuthEnabled(),
                    "tenant_key_prefixing=" + cfg.getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled(),
                    "rate_limit_enabled=" + cfg.getRateLimitingConfigLoader().isRateLimitEnabled(),
                    "rate_limit_tokens_per_sec=" + cfg.getRateLimitingConfigLoader().getRateLimitTokensTerSecond(),
                    "rate_limit_burst=" + cfg.getRateLimitingConfigLoader().getRateLimitBurst(),
                    "metric_module=" + cfg.getMetricConfigLoader().isEnableMetric(),
                    "clustering_module=" + cfg.getClusterConfigLoader().isEnableClustering(),
                    "exporter_module=" + cfg.getExporterConfigLoader().isEnableExporter(),
                    "smart_module=" + cfg.getSmartConfigLoader().isEnableSmart(),
                    "admin_module=" + cfg.getAdminConfigLoader().isAdminCommandsEnabled(),
                    "alerting_module=" + cfg.getAlertConfigLoader().isEnableAlerting(),
                    "rest_api_module=" + cfg.getRestApiConfigLoader().isEnableRestApi()
            ));

            if (cfg.getClusterConfigLoader().isEnableClustering()) {
                cfg.getClusterConfigLoader().getFlatSummary().forEach((k, v) -> fields.add(k + "=" + v));
            }

            response.setResponseFromArray(fields);
        }

        return response;
    }

}
