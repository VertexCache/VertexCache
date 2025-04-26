package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

import java.util.Map;

public class ClusterCoordinationSettingsValidator implements Validator {
    private final Map<String, String> settings;

    public ClusterCoordinationSettingsValidator(Map<String, String> settings) {
        this.settings = settings;
    }

    @Override
    public void validate() {
        checkBoolean("cluster_failover_enabled");
        checkPositiveInt("cluster_failover_check_interval_ms");
        checkNonNegativeInt("cluster_failover_backoff_jitter_ms");
        checkNonNegativeInt("cluster_replication_retry_attempts");
        checkNonNegativeInt("cluster_replication_retry_interval_ms");
        checkNonNegativeInt("cluster_replication_queue_ttl_ms");
        checkNonNegativeInt("cluster_failover_priority");
        checkBoolean("cluster_config_strict");
        checkOptionalPort("cluster_advertise_port");
        checkNonNegativeInt("cluster_max_standbys");
        checkPositiveInt("cluster_heartbeat_timeout_ms");
        checkAutoRejoinRole("cluster_auto_rejoin_role");
    }

    private void checkBoolean(String key) {
        String value = settings.get(key);
        if (value != null && !(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
            throw new VertexCacheValidationException("Coordination setting '" + key + "' must be 'true' or 'false' but found: " + value);
        }
    }

    private void checkPositiveInt(String key) {
        String value = settings.get(key);
        if (value != null) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed <= 0) {
                    throw new VertexCacheValidationException("Coordination setting '" + key + "' must be a positive integer, but found: " + parsed);
                }
            } catch (NumberFormatException e) {
                throw new VertexCacheValidationException("Coordination setting '" + key + "' must be an integer, but found: " + value);
            }
        }
    }

    private void checkNonNegativeInt(String key) {
        String value = settings.get(key);
        if (value != null) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed < 0) {
                    throw new VertexCacheValidationException("Coordination setting '" + key + "' must be non-negative, but found: " + parsed);
                }
            } catch (NumberFormatException e) {
                throw new VertexCacheValidationException("Coordination setting '" + key + "' must be an integer, but found: " + value);
            }
        }
    }

    private void checkOptionalPort(String key) {
        String value = settings.get(key);
        if (value != null && !value.isBlank()) {
            try {
                int port = Integer.parseInt(value);
                if (port < 1024 || port > 65535) {
                    throw new VertexCacheValidationException("Coordination setting '" + key + "' must be between 1024-65535, but found: " + port);
                }
            } catch (NumberFormatException e) {
                throw new VertexCacheValidationException("Coordination setting '" + key + "' must be an integer, but found: " + value);
            }
        }
    }

    private void checkAutoRejoinRole(String key) {
        String value = settings.get(key);
        if (value != null && !value.equalsIgnoreCase("none")
                && !value.equalsIgnoreCase("active")
                && !value.equalsIgnoreCase("standby")) {
            throw new VertexCacheValidationException("Coordination setting '" + key + "' must be one of: none, active, standby — but found: " + value);
        }
    }
}
