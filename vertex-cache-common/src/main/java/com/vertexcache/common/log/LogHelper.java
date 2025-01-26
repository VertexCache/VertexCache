package com.vertexcache.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class LogHelper {

    private static volatile LogHelper instance;
    private static final Logger logger = LogManager.getLogger(LogHelper.class);

    // Private constructor to prevent instantiation
    private LogHelper() {
    }

    // Thread-safe, non-blocking singleton instance retrieval
    public static LogHelper getInstance() {
        if (instance == null) { // First check (no locking)
            synchronized (LogHelper.class) {
                if (instance == null) { // Second check (with locking)
                    instance = new LogHelper();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the Log4j2 configuration from the specified file path.
     *
     * @param log4jConfigFilePath the path to the Log4j2 configuration file
     * @return true if the configuration is successfully loaded, false otherwise
     */
    public boolean loadConfiguration(String log4jConfigFilePath) {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File(log4jConfigFilePath);

            if (file.exists()) {
                context.setConfigLocation(file.toURI());
                context.reconfigure();
                logger.info("Log4j2 configuration reloaded.");
            } else {
                logger.error("Configuration file not found: " + log4jConfigFilePath);
                return false;
            }

            return true;
        } catch (Exception ex) {
            logger.error("Error loading Log4j2 configuration", ex);
            return false;
        }
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to log
     */
    public void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message the message to log
     */
    public void logError(String message) {
        logger.error(message);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param message the message to log
     */
    public void logWarn(String message) {
        logger.warn(message);
    }

    /**
     * Logs a message at the FATAL level.
     *
     * @param message the message to log
     */
    public void logFatal(String message) {
        logger.fatal(message);
    }
}

