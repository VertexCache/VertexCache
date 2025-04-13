package com.vertexcache.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Valiation Use
 *
 *   Non-Batch Use Single use is, simple use the Validator directly:
 *
 *     new UUIDValidator().validate("abc"); // throws immediately
 *
 *   Batch use when you want to process more then on validator:
 *
 *
 *     ValidationBatch batch = new ValidationBatch();
 *
 *     batch.check("clientId", new ClientIdValidator(), "console-client");
 *     batch.check("token", new UUIDValidator(), "bad-uuid");
 *     batch.check("role", new RoleValidator(), "READER");
 *
 *     if (batch.hasErrors()) {
 *       // Handle correctly here, System.out is just an example
 *       System.out.println("Validation errors: " + batch.getSummary());
 *     }
 *
 */
public class ValidationBatch {
    private final List<String> errors = new ArrayList<>();

    public <T> void check(String fieldName, ValidatorHandler<T> validator, T value) {
        try {
            validator.validate(value);
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
