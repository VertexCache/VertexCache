package com.vertexcache.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ValidationBatch Use
 *
 *   Non-Batch Use: just call .validate() directly on a Validator
 *     new UUIDValidator("abc").validate(); // throws immediately
 *
 *   Batch use:
 *     ValidationBatch batch = new ValidationBatch();
 *
 *     batch.check("clientId", new ClientIdValidator("console-client"));
 *     batch.check("token", new UUIDValidator("bad-uuid"));
 *     batch.check("role", new RoleValidator("READER"));
 *
 *     if (batch.hasErrors()) {
 *         System.out.println("Validation errors: " + batch.getSummary());
 *     }
 */
public class ValidationBatch {
    private final List<String> errors = new ArrayList<>();

    public void check(String fieldName, Validator validator) {
        try {
            validator.validate();
        } catch (VertexCacheValidationException e) {
            errors.add(fieldName + ": " + e.getMessage());
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getSummary() {
        return String.join("; ", errors);
    }
}
