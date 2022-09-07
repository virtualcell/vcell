package org.vcell.util.logging;

import java.io.IOException;

/**
 * A wrapper class to interface with Log4J through the logging system.
 */
public class Log4JLog extends Log {
    private final static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(Log4JStdErrLog.class);

    public void trace(String message){
        logger.trace(message);
    }

    public void trace(String message, Throwable throwable){
        logger.trace(message, throwable);
    }

    public void debug(String message){
        logger.debug(message);
    }

    public void debug(String message, Throwable throwable){
        logger.debug(message, throwable);
    }

    public void info(String message){
        logger.info(message);
    }

    public void info(String message, Throwable throwable){
        logger.info(message, throwable);
    }

    public void warn(String message){
        logger.warn(message);
    }

    public void warn(String message, Throwable throwable){
        logger.warn(message, throwable);
    }

    public void error(String message){
        logger.error(message);
    }

    public void error(String message, Throwable throwable){
        logger.error(message, throwable);
    }

    public void fatal(String message){
        logger.fatal(message);
    }

    public void fatal(String message, Throwable throwable){
        logger.fatal(message, throwable);
    }

    @Override
    public void close() throws IOException {
        // Nothing to close in Log4J
    }

    protected void write(String m) throws IOException {
        
    }
}
