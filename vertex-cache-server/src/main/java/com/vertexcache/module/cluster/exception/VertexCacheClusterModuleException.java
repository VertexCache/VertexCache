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
package com.vertexcache.module.cluster.exception;

/**
 * VertexCacheClusterModuleException represents errors specific to the clustering
 * subsystem within VertexCache. It is used to signal failures related to node discovery,
 * cluster coordination, role transitions, or replication consistency.
 *
 * This exception type allows for clear separation of clustering-related issues,
 * aiding in targeted debugging, alerting, and fault isolation during runtime.
 */
public class VertexCacheClusterModuleException extends RuntimeException {
    public VertexCacheClusterModuleException(String message) {
            super(message);
    }
}
