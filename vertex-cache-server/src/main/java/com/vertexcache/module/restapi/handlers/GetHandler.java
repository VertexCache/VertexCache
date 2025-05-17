package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.GetCommand;
import com.vertexcache.core.validation.validators.KeyValidator;

public class GetHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(GetCommand.COMMAND_KEY);

        if (!isReadOnly()) {
            respondForbiddenRequest("Access denied: read access required");
            return;
        }

        String key = this.getContext().pathParam("key");

        try {
            new KeyValidator("key", key).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();
        String value = cache.get(this.getAuthEntry().getTenantId(), key);

        if (value == null) {
            respondSuccess("Key not found");
        } else {
            respondSuccess(value);
        }
    }
}
