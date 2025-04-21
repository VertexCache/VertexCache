package com.vertexcache.core.validation.validators;


import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.module.ratelimiter.VertexCacheRateLimitModuleException;

import java.util.Map;

public class RateLimitValidator implements ValidatorHandler<Map<String, String>> {

    private static final int MIN_RATE_LIMIT = 1;
    private static final int MIN_BURST_LIMIT = 1;

    @Override
    public void validate(Map<String, String> config) {
        validatePositiveInteger(config, "rate_limit_tokens_per_second", MIN_RATE_LIMIT);
        validatePositiveInteger(config, "rate_limit_burst", MIN_BURST_LIMIT);

        // Add future parameters as needed
        // validatePositiveInteger(config, "rate_limit_window_ms", 10);
    }

    private void validatePositiveInteger(Map<String, String> config, String key, int minValue) {
        String valueStr = config.get(key);
        if (valueStr == null) {
            throw new VertexCacheRateLimitModuleException("Missing required config parameter: " + key);
        }

        try {
            int value = Integer.parseInt(valueStr);
            if (value < minValue) {
                throw new VertexCacheRateLimitModuleException("'" + key + "' must be >= " + minValue);
            }
        } catch (NumberFormatException e) {
            throw new VertexCacheRateLimitModuleException("Invalid integer for '" + key + "': " + valueStr);
        }
    }
}
