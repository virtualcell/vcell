package org.vcell.util.logging;

/**
 * Interface through which all VCell based Logging goes that has been requested by the user's requests.
 * All formatting, input validation, etc. should be done in a class that derrives this logger class
 * 
 * Note: Loggers must log Log4J through themselves, or explicitly as themselves; 
 * current implementation style of Log4JLog.java will automatically skip over classes that extend this one when determining caller automatically.
 */
public abstract class Logger {
    protected final org.apache.logging.log4j.Logger selfLogger;
    // All loggers must be able to log themselves (even if they choose not to)!
    protected Logger(Class<?> clazz){
        this.selfLogger = org.apache.logging.log4j.LogManager.getLogger(clazz);
    }
}
