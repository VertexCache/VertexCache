package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.module.rest.model.TokenHeader;

public class RestApiConfigLoader extends LoaderBase {

    private boolean enableRestApi;
    private int port;
    private boolean requireTls;
    private TokenHeader tokenHeader;
    private boolean allowCors;
    private boolean allowAdmin;

    @Override
    public void load() {
        this.enableRestApi = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_REST_API,ConfigKey.ENABLE_REST_API_DEFAULT);
        this.port = this.getConfigLoader().getIntProperty(ConfigKey.REST_API_PORT,ConfigKey.REST_API_PORT_DEFAULT);
        this.requireTls = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_REQUIRE_TLS,true);
        this.tokenHeader = TokenHeader.from(this.getConfigLoader().getProperty(ConfigKey.REST_API_TOKEN_HEADER,TokenHeader.NONE.toString()));
        this.allowCors = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_ALLOW_CORS,false);
        this.allowAdmin = this.getConfigLoader().getBooleanProperty(ConfigKey.REST_API_ALLOW_ADMIN, false);
    }

    public boolean isEnableRestApi() {return enableRestApi;}
    public void setEnableRestApi(boolean enableRestApi) {this.enableRestApi = enableRestApi;}
    public int getPort() {return port;}
    public void setPort(int port) {this.port = port;}
    public boolean isRequireTls() {return requireTls;}
    public void setRequireTls(boolean requireTls) {this.requireTls = requireTls;}
    public TokenHeader getTokenHeader() {return tokenHeader;}
    public void setTokenHeader(TokenHeader tokenHeader) {this.tokenHeader = tokenHeader;}
    public boolean isAllowCors() {return allowCors;}
    public void setAllowCors(boolean allowCors) {this.allowCors = allowCors;}
    public boolean isAllowAdmin() {return allowAdmin;}
    public void setAllowAdmin(boolean allowAdmin) {this.allowAdmin = allowAdmin;}
}
