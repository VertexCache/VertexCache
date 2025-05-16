package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.module.restapi.model.TokenHeader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestApiConfigLoader extends LoaderBase {

    private boolean enableRestApi;
    private int port;
    private boolean requireAuth;
    private boolean requireTls;
    private TokenHeader tokenHeader;
    private boolean allowCors;
    private boolean allowAdmin;

    @Override
    public void load() {
        this.enableRestApi = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_REST_API,ConfigKey.ENABLE_REST_API_DEFAULT);
        this.port = this.getConfigLoader().getIntProperty(ConfigKey.REST_API_PORT,ConfigKey.REST_API_PORT_DEFAULT);
        this.requireAuth = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_REQUIRE_AUTH,true);
        this.requireTls = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_REQUIRE_TLS,true);
        this.tokenHeader = TokenHeader.from(this.getConfigLoader().getProperty(ConfigKey.REST_API_TOKEN_HEADER,TokenHeader.NONE.toString()));
        this.allowCors = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_ALLOW_CORS,false);
        this.allowAdmin = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_ALLOW_ADMIN, false);
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ConfigKey.ENABLE_REST_API, String.valueOf(enableRestApi));
        map.put(ConfigKey.REST_API_PORT, String.valueOf(port));
        map.put(ConfigKey.REST_API_REQUIRE_AUTH, String.valueOf(requireAuth));
        map.put(ConfigKey.REST_API_REQUIRE_TLS, String.valueOf(requireTls));
        map.put(ConfigKey.REST_API_TOKEN_HEADER, tokenHeader != null ? tokenHeader.toString() : "null");
        map.put(ConfigKey.REST_API_ALLOW_CORS, String.valueOf(allowCors));
        map.put(ConfigKey.REST_API_ALLOW_ADMIN, String.valueOf(allowAdmin));
        return map;
    }

    public List<String> getTextSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Settings:");
        lines.add("  enabled:       " + enableRestApi);
        lines.add("  port:          " + port);
        lines.add("  require TLS:   " + requireTls);
        lines.add("  token header:  " + (tokenHeader != null ? tokenHeader : "null"));
        lines.add("  allow CORS:    " + allowCors);
        //lines.add("  allow ADMIN:   " + allowAdmin);
        return lines;
    }

    public boolean isEnableRestApi() {return enableRestApi;}
    public void setEnableRestApi(boolean enableRestApi) {this.enableRestApi = enableRestApi;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
    public boolean isRequireAuth() {return requireAuth;}
    public void setRequireAuth(boolean requireAuth) {this.requireAuth = requireAuth;}
    public boolean isRequireTls() {return requireTls;}
    public void setRequireTls(boolean requireTls) {this.requireTls = requireTls;}
    public TokenHeader getTokenHeader() {return tokenHeader;}
    public void setTokenHeader(TokenHeader tokenHeader) {this.tokenHeader = tokenHeader;}
    public boolean isAllowCors() {return allowCors;}
    public void setAllowCors(boolean allowCors) {this.allowCors = allowCors;}
    public boolean isAllowAdmin() {return allowAdmin;}
    public void setAllowAdmin(boolean allowAdmin) {this.allowAdmin = allowAdmin;}
}
