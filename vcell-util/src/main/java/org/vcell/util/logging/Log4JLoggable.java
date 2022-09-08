package org.vcell.util.logging;

public interface Log4JLoggable {
    public void log(org.apache.logging.log4j.Level level, String message);
    public void log(org.apache.logging.log4j.Level level, String message, Throwable throwable);
    public void setLogLevel(org.apache.logging.log4j.Level level);
    public org.apache.logging.log4j.Level getLogLevel();
}
