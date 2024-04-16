package com.vertexcache.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;

/*
 * LogLoader help log Log4j2 xml or .properties file based off of file location
 * this location is set in the vertex-cache-server.properties
 */
public class LogUtil {

    private final Logger logger;

    public LogUtil(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void fatal(String message) {
        logger.fatal(message);
    }

    public static boolean load(String log4jConfigFilePath)
    {
        try {
            // Check if the file exists
            File log4jConfigFile = new File(log4jConfigFilePath);
            if (!log4jConfigFile.exists()) {
                System.err.println("Log4j2 configuration file not found: " + log4jConfigFilePath);
                // Handle the error, maybe set some default configuration or exit the application
                return false;
            }

            // Initialize log4j with the specified configuration file
            System.setProperty("log4j.configurationFile", log4jConfigFile.getAbsolutePath());

            // Reload the logging configuration
            //LoggerContext context = (LoggerContext) LogManager.getContext(false);
           //context.reconfigure();

           Configurator.initialize(null, log4jConfigFile.getAbsolutePath());

//System.out.println("FILE Exists");

           // ConfigurationSource source = new ConfigurationSource(new FileInputStream(log4jConfigFile));
           // Configurator.initialize(null, source);

            //ConfigurationSource source = new ConfigurationSource(new FileInputStream(log4jConfigFile));

           // Configurator.initialize(null, source);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


}
