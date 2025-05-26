/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.Map;

public class IdentValidator implements Validator {

    private final String clientId;

    public IdentValidator(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void validate() {
        new ClientIdValidator(clientId).validate();
        boolean authEnabled = Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled();
        boolean clusterEnabled = Config.getInstance().getClusterConfigLoader().isEnableClustering();
        if (clusterEnabled && authEnabled) {
            ClusterConfigLoader configLoader = Config.getInstance().getClusterConfigLoader();
            Map<String, ClusterNode> nodes = configLoader.getAllClusterNodes();

            boolean isInternalClusterClient = nodes.containsKey(clientId)
                    && Config.getInstance().getClusterConfigLoader().getLocalClusterNode().getId().equals(clientId);

            if (nodes.containsKey(clientId) && !isInternalClusterClient) {
                throw new VertexCacheValidationException("Client ID '" + clientId + "' is reserved for internal cluster use.");
            }
        }
    }
}
