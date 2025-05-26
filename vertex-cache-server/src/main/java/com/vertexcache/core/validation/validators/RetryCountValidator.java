/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

