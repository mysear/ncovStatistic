package com.ncov.stats.config;


import java.util.logging.Level;

/**
 * @author contact@vector.link
 */
public class Logger {
    public static java.util.logging.Logger logger = java.util.logging.Logger.getGlobal();

    public static void error(String arg) {
        if (logger == null) {
            logger = java.util.logging.Logger.getGlobal();
        }
        logger.log(Level.WARNING, arg);
    }
}
