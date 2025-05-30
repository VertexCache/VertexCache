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
package com.vertexcache.module.alert;

import com.vertexcache.module.alert.listeners.ClusterNodeEventListener;

/**
 * AlertModuleNoOp is a disabled or stub implementation of the alerting system
 * in VertexCache. It is used when alerting is not enabled via configuration,
 * allowing the system to operate without initializing the full alerting pipeline.
 *
 * This no-op module satisfies the same interface as the active AlertModule but
 * performs no operations, effectively bypassing all alert dispatching logic.
 */
public class AlertModuleNoOp implements ClusterNodeEventListener {
    @Override
    public void onSecondaryNodePromotedToPrimary(String nodeId) {
        // NoOp
    }
}
