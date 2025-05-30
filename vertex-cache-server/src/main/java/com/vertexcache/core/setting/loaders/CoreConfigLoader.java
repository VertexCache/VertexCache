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
package com.vertexcache.core.setting.loaders;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

/**
 * Configuration loader responsible for validating fundamental runtime settings for VertexCache.
 *
 * Handles essential core options such as server port and network binding.
 *
 * These settings are critical for starting the core server infrastructure and must
 * be validated early during the boot process.
 *
 * All other modules depend on successful core configuration before initialization.
 */
public class CoreConfigLoader extends LoaderBase {

    private static final String APP_NAME = "VertexCache";
    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;
    private boolean enableVerbose = ConfigKey.ENABLE_VERBOSE_DEFAULT;

    // Note this read from the Cluster, cluster_node_id, if not set, give "standalone-node"
    private String localNodeId;

    @Override
    public void load() {

        this.localNodeId = this.getConfigLoader().getProperty(ConfigKey.CLUSTER_NODE_ID,"standalone-node");
        this.serverPort = this.getConfigLoader().getIntProperty(ConfigKey.SERVER_PORT,ConfigKey.SERVER_PORT_DEFAULT);
        this.enableVerbose = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_VERBOSE,ConfigKey.ENABLE_VERBOSE_DEFAULT);

    }

    public String getAppName() { return CoreConfigLoader.APP_NAME; }
    public String getLocalNodeId() {return localNodeId;}
    public void setLocalNodeId(String localNodeId) {this.localNodeId = localNodeId;}
    public int getServerPort() {
        return serverPort;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public boolean isEnableVerbose() {
        return enableVerbose;
    }
    public void setEnableVerbose(boolean enableVerbose) {
        this.enableVerbose = enableVerbose;
    }

}
