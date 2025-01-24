package org.vcell.cli.commands.execution;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.concurrent.Callable;

public abstract class ExecutionBasedCommand implements Callable<Integer>  {
    protected static void generateLoggerContext(Level logLevel, boolean bDebug){
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell.sbml").getName()).setLevel(bDebug ? logLevel : Level.ERROR);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(bDebug ? logLevel : Level.WARN );
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.jlibsedml").getName()).setLevel(bDebug ? logLevel : Level.WARN);
        config.updateLoggers();
    }
}
