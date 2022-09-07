package org.vcell.util.logging;

import java.io.IOException;

public class Log4JStdErrLog extends StdErrLog {
    private final static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(Log4JStdErrLog.class);

    @Override
    protected void write(String m) {
        logger.error(m);
    }

    protected void write(String m, Throwable e){
        logger.error(m, e);
    }
    
    public void writeToStream(String m, Throwable e) throws IOException {
        this.write(m, e);
    }
}
