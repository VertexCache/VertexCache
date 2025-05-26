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
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.metric.MetricModule;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Optional;

public class MetricsCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "METRICS";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();

        boolean pretty = argumentParser.getPrimaryArgument().getArgs().size() == 1 &&
                argumentParser.getPrimaryArgument().getArgs().getFirst().equalsIgnoreCase(COMMAND_PRETTY);

        Optional<MetricModule> optMetricModule = ModuleRegistry.getInstance().getModule(MetricModule.class);
        if (!Config.getInstance().getMetricConfigLoader().isEnableMetric() || optMetricModule.isEmpty()) {
            commandResponse.setResponseError("Metric module is not loaded or enabled.");
            return commandResponse;
        }

        MetricModule metricModule = optMetricModule.get();
        MetricAccess metricAccess = metricModule.getMetricAccess();
        if (pretty) {
            commandResponse.setResponse(metricAccess.toSummaryAsPretty());
        } else {
            commandResponse.setResponse(metricAccess.toSummaryAsFlat());
        }
        return commandResponse;
    }
}
