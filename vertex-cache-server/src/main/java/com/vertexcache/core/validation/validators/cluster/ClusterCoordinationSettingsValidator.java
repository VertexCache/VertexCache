package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.meta.ClusterCoordinationKeys;

import java.util.Map;

public class ClusterCoordinationSettingsValidator implements Validator {

    private final Map<String, String> settings;

    public ClusterCoordinationSettingsValidator(Map<String, String> settings) {
        this.settings = settings;
    }

    @Override
    public void validate() {
        for (String key : ClusterCoordinationKeys.ACTIVE_KEYS) {
            String value = settings.get(key);
            if (value == null) continue;

            if (key.endsWith("_enabled")) {
                checkBoolean(key);
            } else if (key.contains("interval") || key.contains("priority")) {
                checkPositiveInt(key);
            } else {
                System.out.println("[WARN] No validation rule defined for key: " + key);
            }
        }
    }

    private void checkBoolean(String key) {
        String value = settings.get(key);
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new VertexCacheValidationException("Invalid boolean value for " + key + ": " + value);
        }
    }

    private void checkPositiveInt(String key) {
        String value = settings.get(key);
        try {
            int intVal = Integer.parseInt(value);
            if (intVal <= 0) {
                throw new VertexCacheValidationException("Value for " + key + " must be > 0: " + value);
            }
        } catch (NumberFormatException e) {
            throw new VertexCacheValidationException("Invalid integer for " + key + ": " + value);
        }
    }
}