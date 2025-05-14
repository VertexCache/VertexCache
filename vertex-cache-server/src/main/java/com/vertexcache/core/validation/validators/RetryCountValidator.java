package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class RetryCountValidator implements Validator {

    private final int value;
    private final String label;
    private final int max;

    public RetryCountValidator(int value, String label) {
        this(value, label, 10); // Default upper limit
    }

    public RetryCountValidator(int value, String label, int max) {
        this.value = value;
        this.label = label != null ? label : "Retry count";
        this.max = max;
    }

    @Override
    public void validate() {
        if (value < 0) {
            throw new VertexCacheValidationException(label + " must be 0 or greater (was " + value + ")");
        }
        if (value > max) {
            throw new VertexCacheValidationException(
                    String.format("%s must not exceed %d (was %d)", label, max, value)
            );
        }
    }
}

