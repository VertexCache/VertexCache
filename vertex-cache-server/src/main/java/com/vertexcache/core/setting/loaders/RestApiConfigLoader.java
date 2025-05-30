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
import com.vertexcache.module.restapi.model.TokenHeader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration loader responsible for validating REST API settings.
 *
 * Handles options such as:
 * - Whether the REST API module is enabled
 * - Network binding port and TLS settings for the REST endpoint
 * - Allowed roles for API access (e.g., READ, READ_WRITE)
 *
 * Ensures the REST API can be safely exposed with proper authentication and isolation.
 * If disabled or misconfigured, the REST module will not be started.
 *
 * This loader runs during startup to prepare the REST interface for external client usage.
 */
public class RestApiConfigLoader extends LoaderBase {

    private boolean enableRestApi;
    private int port;
    private int portTls;
    private boolean requireAuth;
    private boolean requireTls;
    private TokenHeader tokenHeader;
    private boolean allowCors;

    @Override
    public void load() {
        this.enableRestApi = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_REST_API,ConfigKey.ENABLE_REST_API_DEFAULT);
        this.port = this.getConfigLoader().getIntProperty(ConfigKey.REST_API_PORT,ConfigKey.REST_API_PORT_DEFAULT);
        this.portTls = this.getConfigLoader().getIntProperty(ConfigKey.REST_API_PORT_TLS,ConfigKey.REST_API_PORT_TLS_DEFAULT);
        this.requireAuth = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_REQUIRE_AUTH,true);
        this.requireTls = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_REQUIRE_TLS,true);
        this.tokenHeader = TokenHeader.from(this.getConfigLoader().getProperty(ConfigKey.REST_API_TOKEN_HEADER,TokenHeader.NONE.toString()));
        this.allowCors = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_ALLOW_CORS,false);
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ConfigKey.ENABLE_REST_API, String.valueOf(enableRestApi));
        map.put(ConfigKey.REST_API_PORT, String.valueOf(port));
        map.put(ConfigKey.REST_API_PORT_TLS, String.valueOf(portTls));
        map.put(ConfigKey.REST_API_REQUIRE_AUTH, String.valueOf(requireAuth));
        map.put(ConfigKey.REST_API_REQUIRE_TLS, String.valueOf(requireTls));
        map.put(ConfigKey.REST_API_TOKEN_HEADER, tokenHeader != null ? tokenHeader.toString() : "null");
        map.put(ConfigKey.REST_API_ALLOW_CORS, String.valueOf(allowCors));
        return map;
    }

    public List<String> getTextSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Settings:");
        lines.add("  enabled:       " + enableRestApi);
        lines.add("  port:          " + port);
        lines.add("  require TLS:   " + requireTls);
        lines.add("  port tls:      " + (requireTls ? portTls : "n/a") );
        lines.add("  token header:  " + (tokenHeader != null ? tokenHeader : "null"));
        lines.add("  allow CORS:    " + allowCors);
        return lines;
    }

    public boolean isEnableRestApi() {return enableRestApi;}
    public void setEnableRestApi(boolean enableRestApi) {this.enableRestApi = enableRestApi;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
    public int getPortTls() {return portTls;}
    public void setPortTls(int portTls) {this.portTls = portTls;}
    public boolean isRequireAuth() {return requireAuth;}
    public void setRequireAuth(boolean requireAuth) {this.requireAuth = requireAuth;}
    public boolean isRequireTls() {return requireTls;}
    public void setRequireTls(boolean requireTls) {this.requireTls = requireTls;}
    public TokenHeader getTokenHeader() {return tokenHeader;}
    public void setTokenHeader(TokenHeader tokenHeader) {this.tokenHeader = tokenHeader;}
    public boolean isAllowCors() {return allowCors;}
    public void setAllowCors(boolean allowCors) {this.allowCors = allowCors;}
}
