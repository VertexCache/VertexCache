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
package com.vertexcache.module.alert.listeners;

/**
 * ClusterNodeEventListener defines a callback interface for responding to specific
 * cluster events in VertexCache. Currently, it provides a single method,
 * {@code onSecondaryNodePromotedToPrimary}, which is invoked when a secondary node
 * is promoted to the primary role.
 */
public interface ClusterNodeEventListener {

    void onSecondaryNodePromotedToPrimary(String nodeId);

}
