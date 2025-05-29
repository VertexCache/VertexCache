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
package com.vertexcache.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.net.URI;

/**
 * Centralized logging utility for consistent log formatting and behavior across the application.
 *
 * Intended to reduce boilerplate and encourage uniform logging practices throughout VertexCache.
 * Built on top of standard SLF4J logging interfaces.
 */
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
     * Checks and returns the URI of the current Log4j2 configuration file.
     *
     * @return the URI of the configuration file, or null if not set
     */
    public URI getCurrentConfigurationFile() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            URI configLocation = context.getConfigLocation();

            if (configLocation != null) {
                logger.info("Current Log4j2 configuration file: " + configLocation);
                return configLocation;
            } else {
                logger.warn("No Log4j2 configuration file is currently loaded.");
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error retrieving current Log4j2 configuration file", ex);
            return null;
        }
    }

    /**
     * Checks if Log4j2 configuration is loaded successfully.
     *
     * @return true if configuration is loaded, false otherwise
     */
    public boolean isConfigurationLoaded() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            URI configLocation = context.getConfigLocation();

            if (configLocation != null) {
                logger.info("Log4j2 configuration is successfully loaded from: " + configLocation);
                return true;
            } else {
                logger.warn("Log4j2 configuration is not loaded.");
                return false;
            }
        } catch (Exception ex) {
            logger.error("Error checking if Log4j2 configuration is loaded", ex);
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
    public void logWarn(String message, Exception e) {logger.warn(message + ", " + e.getMessage());}

    /**
     * Logs a message at the FATAL level.
     *
     * @param message the message to log
     */
    public void logFatal(String message) { logger.fatal(message);}
    public void logFatal(String message, Exception e) {logger.fatal(message + ", " + e.getMessage());}


    /**
     * Logs a message at the DEBUG level.
     *
     * @param message the message to log
     */
    public void logDebug(String message) {
        logger.debug(message);
    }
}
