package com.vertexcache.core.validation.validators.restapi;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class RestApiTlsValidator implements Validator {

    @Override
    public void validate() {
        var restConfig = Config.getInstance().getRestApiConfigLoader();
        var tlsConfig = Config.getInstance().getSecurityConfigLoader();

        if (restConfig.isRequireTls()) {
            String cert = tlsConfig.getTlsCertificate();
            String key = tlsConfig.getTlsPrivateKey();

            if (cert == null || cert.isBlank()) {
                throw new VertexCacheValidationException("TLS is required for REST API, but no certificate is set.");
            }

            if (key == null || key.isBlank()) {
                throw new VertexCacheValidationException("TLS is required for REST API, but no private key is set.");
            }
        }
    }
}

