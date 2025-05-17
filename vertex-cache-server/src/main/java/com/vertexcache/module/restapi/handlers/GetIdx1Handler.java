package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.GetSecondaryIdxOneCommand;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

public class GetIdx1Handler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(GetSecondaryIdxOneCommand.COMMAND_KEY);

        if (!isReadOnly()) {
            respondForbiddenRequest("Access denied: read access required");
            return;
        }

        String idx1 = this.getPathParam(ApiParameter.IDX1.value());

        try {
            new KeyValidator(ApiParameter.IDX1.value(), idx1).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();
        String value = cache.getBySecondaryIdx1(this.getAuthEntry().getTenantId(), idx1);

        if (value == null) {
            respondSuccess("Secondary Key (idx1) not found");
        } else {
            respondSuccess("Value retrieved successfully by secondary key (idx1)",value);
        }
    }
}
