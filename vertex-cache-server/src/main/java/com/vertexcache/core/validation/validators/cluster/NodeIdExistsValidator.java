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
package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.core.setting.Config;

/**
 * Validator that checks whether a given cluster node ID exists in the current configuration.
 *
 * Ensures that the provided node ID is present in the configured cluster node map.
 * Typically used for validating input to internal commands or configuration overrides
 * that reference specific peer nodes.
 *
 * Throws a VertexCacheValidationException if the node ID is not defined in the cluster topology.
 */
public class NodeIdExistsValidator implements Validator {
    private final String nodeId;

    public NodeIdExistsValidator(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void validate() {
        if (!Config.getInstance().getClusterConfigLoader().getAllClusterNodes().containsKey(nodeId)) {
            throw new VertexCacheValidationException("Unknown node ID: " + nodeId);
        }
    }
}
