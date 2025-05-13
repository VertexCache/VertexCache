package com.vertexcache.core.validation.validators.restapi;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.restapi.model.TokenHeader;

public class TokenHeaderValidator implements Validator {

    private final TokenHeader header;

    public TokenHeaderValidator(TokenHeader header) {
        this.header = header;
    }

    @Override
    public void validate() {
        if (header == null || header == TokenHeader.NONE || header == TokenHeader.UNKNOWN) {
            throw new VertexCacheValidationException("Token header must be specified and valid for REST API");
        }
    }
}
