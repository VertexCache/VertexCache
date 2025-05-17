package com.vertexcache.module.restapi.handlers;


import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.DelCommand;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

public class DelHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(DelCommand.COMMAND_KEY);

        if (!isWritable()) {
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

        CacheAccessService cacheAccessService = new CacheAccessService();
        cacheAccessService.remove(this.getAuthEntry().getTenantId(),key);
        respondOk(ResultCode.VALUE_DELETED);
    }
}
