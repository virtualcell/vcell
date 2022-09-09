package org.vcell.util.recording;

/**
 * Interface through which all VCell based Logging goes that has been requested by the user's requests.
 * All formatting, input validation, etc. should be done in a class that derrives this logger class
 * 
 * Note: Loggers must log Log4J through themselves, or explicitly as themselves; 
 * current implementation style of Log4JLog.java will automatically skip over classes that extend this one when determining caller automatically.
 * 
 * @since VCell 7.4.0.62
 */
public abstract class Recorder {
    /**
     * Log4J2 logger for {@code Logger} to use for its own logging needs.
     * 
     * NB: While this logger's use isn't required in children, the capability should be there.
     */
    protected final org.apache.logging.log4j.Logger selfLogger;
    // All loggers must be able to log themselves (even if they choose not to)!

    /**
     * Base constructor; feeds directly into log4j2
     * @param clazz
     */
    protected Recorder(Class<?> clazz){
        this.selfLogger = org.apache.logging.log4j.LogManager.getLogger(clazz);
    }
}
