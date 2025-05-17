package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.SetCommand;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.core.validation.validators.ValueValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

public class SetHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(SetCommand.COMMAND_KEY);

        if (!isWritable()) {
            respondForbiddenRequest("Access denied: write access required");
            return;
        }

        String key = getStringField(this.getBody(), ApiParameter.KEY.value());
        String value = getStringField(this.getBody(), ApiParameter.VALUE.value());
        String idx1 = getStringField(this.getBody(), ApiParameter.IDX1.value());
        String idx2 = getStringField(this.getBody(), ApiParameter.IDX2.value());
        String formatStr = getStringField(this.getBody(), ApiParameter.FORMAT.value());

        if (key == null || value == null) {
            respondBadRequest("Missing required fields: key and value");
            return;
        }

        if (idx2 != null && idx1 == null) {
            respondBadRequest("idx2 requires idx1 to be provided");
            return;
        }

        try {
            new KeyValidator(ApiParameter.KEY.value(), key).validate();
            new ValueValidator(ApiParameter.VALUE.value(), value, parseDataType(formatStr)).validate();
            if (idx1 != null) new KeyValidator(ApiParameter.IDX1.value(), idx1).validate();
            if (idx2 != null) new KeyValidator(ApiParameter.IDX2.value(), idx2).validate();
        } catch (VertexCacheValidationException ex) {
            respondBadRequest(ex.getMessage());
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
        respondSuccess("Key set successfully");
    }
}