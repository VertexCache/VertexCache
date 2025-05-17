package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.service.CacheAccessService;
import com.vertexcache.core.command.impl.GetSecondaryIdxOneCommand;
import com.vertexcache.core.command.impl.GetSecondaryIdxTwoCommand;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;
import com.vertexcache.module.restapi.model.ApiResponse;
import io.javalin.http.Context;

public class GetIdx2Handler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(GetSecondaryIdxTwoCommand.COMMAND_KEY);

        if (!isReadOnly()) {
            respondForbiddenRequest("Access denied: read access required");
            return;
        }

        String idx2 = this.getContext().pathParam(ApiParameter.IDX2.value());

        try {
            new KeyValidator(ApiParameter.IDX2.value(), idx2).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();
        String value = cache.getBySecondaryIdx2(this.getAuthEntry().getTenantId(), idx2);

        if (value == null) {
            respondSuccess("Tertiary Key (idx2) not found");
        } else {
            respondSuccess("Value retrieved successfully by Tertiary Key (idx2)",value);
        }
    }
}
