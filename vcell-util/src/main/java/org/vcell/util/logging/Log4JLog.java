package org.vcell.util.logging;

import java.io.IOException;

import org.apache.logging.log4j.Level;

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
        this.trace(this.getCallerClass(), message);
    }

    public void trace(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.trace(message);
    }

    public void trace(String message, Throwable throwable){
        this.trace(this.getCallerClass(), message, throwable);
    }

    public void trace(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.trace(message, throwable);
    }

    public void debug(String message){
        this.debug(this.getCallerClass(), message);
    }

    public void debug(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.debug(message);
    }

    public void debug(String message, Throwable throwable){
        this.debug(this.getCallerClass(), message, throwable);
    }

    public void debug(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.debug(message, throwable);
    }

    public void info(String message){
        this.info(this.getCallerClass(), message);
    }

    public void info(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.info(message);
    }

    public void info(String message, Throwable throwable){
        this.info(this.getCallerClass(), message, throwable);
    }

    public void info(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.info(message, throwable);
    }

    public void warn(String message){
        this.warn(this.getCallerClass(), message);
    }

    public void warn(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.warn(message);
    }

    public void warn(String message, Throwable throwable){
        this.warn(this.getCallerClass(), message, throwable);
    }

    public void warn(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.warn(message, throwable);
    }

    public void error(String message){
        this.error(this.getCallerClass(), message);
    }

    public void error(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.error(message);
    }

    public void error(String message, Throwable throwable){
        this.error(this.getCallerClass(), message, throwable);
    }

    public void error(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.error(message, throwable);
    }

    public void fatal(String message){
        this.fatal(this.getCallerClass(), message);
    }

    public void fatal(Class<?> callingClass, String message){
        this.setLogger(callingClass);
        this.logger.fatal(message);
    }

    public void fatal(String message, Throwable throwable){
        this.fatal(this.getCallerClass(), message, throwable);
    }

    public void fatal(Class<?> callingClass, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.fatal(message, throwable);
    }

    @Override
    public void close() throws IOException {
        // Nothing to close in Log4J
    }

    @Override
    public void log(org.apache.logging.log4j.Level level, String message){
        this.fatal(this.getCallerClass(), message);
    }

    public void log(Class<?> callingClass, org.apache.logging.log4j.Level level, String message){
        this.setLogger(callingClass);
        this.logger.fatal(message);
    }

    @Override
    public void log(org.apache.logging.log4j.Level level, String message, Throwable throwable){
        this.fatal(this.getCallerClass(), message, throwable);
    }

    public void log(Class<?> callingClass, org.apache.logging.log4j.Level level, String message, Throwable throwable){
        this.setLogger(callingClass);
        this.logger.fatal(message, throwable);
    }

    @Override
    public void setLogLevel(Level logLevel) {
        
        org.apache.logging.log4j.core.LoggerContext ctx = org.apache.logging.log4j.core.LoggerContext.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = config.getLoggerConfig(org.apache.logging.log4j.LogManager.getLogger("org.vcell").getName());
        loggerConfig.setLevel(logLevel);
        loggerConfig = config.getLoggerConfig(org.apache.logging.log4j.LogManager.getLogger("cbit").getName());
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers(); // This causes all Loggers to refetch information from their LoggerConfig.
    }

    @Override
    public Level getLogLevel() {
        return this.logger.getLevel();
    }

    @Override
    protected void write(String m) throws IOException {
        // default: a custom log level inbetween warn and info.
        this.log(Log4JLog.IMPORTANT, m);
    }

    protected Class<?> getCallerClass(){
        ///TODO: improve this method to find the first stack element not from this class.
        // 0th frame is this method, 1st is a local method, 2nd is the caller; 
        Class<?> caller = null;
        try {
            caller = Class.forName(new Exception().getStackTrace()[2].getClassName());
        } catch (ClassNotFoundException e){
            this.setLogger(this.getClass());
            this.error("Black magic attempt failed; class name could not be resolved.", e);
            throw new RuntimeException("Error trying to log information.", e);
        }
        return caller;
    }
}
