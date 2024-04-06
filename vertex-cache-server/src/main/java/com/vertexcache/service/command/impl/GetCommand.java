package com.vertexcache.service.command.impl;

import com.vertexcache.common.log.LogUtil;
import com.vertexcache.domain.cache.CacheService;
import com.vertexcache.domain.cache.impl.Cache;
import com.vertexcache.service.command.Command;
import com.vertexcache.service.command.CommandResponse;

import java.util.Map;

public class GetCommand implements Command<String> {

    private static final LogUtil logger = new LogUtil(GetCommand.class);

    public static final String COMMAND_KEY = "get";

    private Map<String, String> data;

    public GetCommand(Map<String, String> data) {
        this.data = data;
    }

    public CommandResponse execute(String... args) {
        boolean isOK = false;
        String result;
        try {
            if (args.length == 1) {
                Cache cache = CacheService.getCache();
                String value = (String) cache.get(args[0]);
                if(value != null) {
                    result = "Fake-Value";
                    isOK = true;
                } else {
                    // not error, just not found
                    result = "(nil)";
                    isOK = true;
                }
            } else {
                result = "GET command requires a single argument, which is the key of the value you want to retrieve.";
            }
        } catch (Exception ex) {
            result = "GET command failed, fatal error, check logs.";
            logger.fatal(ex.getMessage());
        }
        return new CommandResponse(isOK,result);
    }
}
