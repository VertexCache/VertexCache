package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.GetCommand;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

public class GetHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(GetCommand.COMMAND_KEY);

        if (!isReadOnly()) {
            respondForbiddenAccess(ResultCode.UNAUTHORIZED);
            return;
        }

        String key = this.getContext().pathParam(ApiParameter.KEY.value());

        try {
            new KeyValidator(ApiParameter.KEY.value(), key).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();
        String value = cache.get(this.getAuthEntry().getTenantId(), key);

        if (value == null) {
            respondNotFound(ResultCode.KEY_NOT_FOUND);
        } else {
            respondOk(ResultCode.CACHE_HIT, value);
        }
    }
}
