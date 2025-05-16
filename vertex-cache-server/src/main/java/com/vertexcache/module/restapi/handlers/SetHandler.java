package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.SetCommand;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.core.validation.validators.ValueValidator;

public class SetHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {

        logRequest(SetCommand.COMMAND_KEY);

        String key = getStringField(this.getBody(), "key");
        String value = getStringField(this.getBody(), "value");
        String idx1 = getStringField(this.getBody(), "idx1");
        String idx2 = getStringField(this.getBody(), "idx2");
        String formatStr = getStringField(this.getBody(), "format");

        if (key == null || value == null) {
            respondError(this.getContext(), 400, "Missing required fields: key and value");
            return;
        }

        if (idx2 != null && idx1 == null) {
            respondError(this.getContext(), 400, "idx2 requires idx1 to be provided");
            return;
        }

        try {
            new KeyValidator("key", key).validate();
            new ValueValidator("value", value, parseDataType(formatStr)).validate();
            if (idx1 != null) new KeyValidator("idx1", idx1).validate();
            if (idx2 != null) new KeyValidator("idx2", idx2).validate();
        } catch (VertexCacheValidationException ex) {
            respondError(this.getContext(), 400, ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();

        if (idx1 != null && idx2 != null) {
            cache.put(this.getAuthEntry().getTenantId(), key, value, idx1, idx2);
        } else if (idx1 != null) {
            cache.put(this.getAuthEntry().getTenantId(), key, value, idx1);
        } else {
            cache.put(this.getAuthEntry().getTenantId(), key, value);
        }
        respondSuccess(this.getContext(), "Key set successfully");
    }


}