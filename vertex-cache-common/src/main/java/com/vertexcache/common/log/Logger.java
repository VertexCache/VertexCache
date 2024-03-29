package com.vertexcache.common.log;

public class Logger {
    private static volatile Logger instance;

    private Logger() {
        // Private constructor to prevent instantiation from outside
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public synchronized void log(String message) {
        // Logging implementation
        System.out.println(message);
    }

    public synchronized void log(Exception exception) {
        // Logging implementation
        //System.out.println(message);
        exception.printStackTrace(); // do something
    }
}
