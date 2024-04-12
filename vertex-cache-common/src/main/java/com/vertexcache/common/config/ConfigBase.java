package com.vertexcache.common.config;

import com.vertexcache.common.cli.CommandLineArgsParser;

abstract public class ConfigBase {

  abstract public void loadPropertiesFromArgs(CommandLineArgsParser commandLineArgsParser);

}
