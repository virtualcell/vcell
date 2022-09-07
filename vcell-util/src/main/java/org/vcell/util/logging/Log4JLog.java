package org.vcell.util.logging;

import java.io.IOException;

/**
 * A wrapper class to interface with Log4J through the logging system.
 */
public class Log4JLog extends Log implements Log4JLoggable {
    private org.apache.logging.log4j.Logger logger;
    private final static org.apache.logging.log4j.Level IMPORTANT = org.apache.logging.log4j.Level.forName("IMPORTANT", 350);

    // Self Logging
    public Log4JLog(){
        this.setLogger(Log4JLog.class);
    }

    public Log4JLog(Class<?> clazz){
        this.setLogger(clazz);
    }

    public void setLogger(Class<?> clazz){
        this.logger = org.apache.logging.log4j.LogManager.getLogger(clazz);
    }

    public void trace(String message){
        this.logger.trace(message);
    }

    public void trace(String message, Throwable throwable){
        this.logger.trace(message, throwable);
    }

    public void debug(String message){
        this.logger.debug(message);
    }

    public void debug(String message, Throwable throwable){
        this.logger.debug(message, throwable);
    }

    public void info(String message){
        this.logger.info(message);
    }

    public void info(String message, Throwable throwable){
        this.logger.info(message, throwable);
    }

    public void warn(String message){
        this.logger.warn(message);
    }

    public void warn(String message, Throwable throwable){
        this.logger.warn(message, throwable);
    }

    public void error(String message){
        this.logger.error(message);
    }

    public void error(String message, Throwable throwable){
        this.logger.error(message, throwable);
    }

    public void fatal(String message){
        this.logger.fatal(message);
    }

    public void fatal(String message, Throwable throwable){
        this.logger.fatal(message, throwable);
    }

    @Override
    public void close() throws IOException {
        // Nothing to close in Log4J
    }

    public void log(org.apache.logging.log4j.Level level, String message){
        logger.log(level, message);
    }

    public void log(org.apache.logging.log4j.Level level, String message, Throwable throwable){
        logger.log(level, message, throwable);
    }

    protected void write(String m) throws IOException {
        // default: Whatever the logging level is.
        this.log(Log4JLog.IMPORTANT, m);
    }
}
