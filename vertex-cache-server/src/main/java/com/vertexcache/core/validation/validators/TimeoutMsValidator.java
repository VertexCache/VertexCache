package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class TimeoutMsValidator implements Validator {

    private final int value;
    private final String label;
    private final int min;
    private final int max;

    public TimeoutMsValidator(int value, String label) {
        this(value, label, 10, 60000); // Default range: 10ms to 60,000ms (60s)
    }

    public TimeoutMsValidator(int value, String label, int min, int max) {
        this.value = value;
        this.label = label != null ? label : "Timeout (ms)";
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate() {
        if (value < min || value > max) {
            throw new VertexCacheValidationException(
                    String.format("%s must be between %d and %d ms (was %d)", label, min, max, value)
            );
        }
    }
}
