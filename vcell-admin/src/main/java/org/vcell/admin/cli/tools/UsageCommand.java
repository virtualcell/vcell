package org.vcell.admin.cli.tools;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;

@Command(name = "usage", description = "reports regarding vcell usage")
public class UsageCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(UsageCommand.class);

    @Option(names = { "-o", "--outputFile"}, required = true)
    private File outputFilePath;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                String basicStatistics = cliDatabaseService.getBasicStatistics();
                try (FileWriter fileWriter = new FileWriter(outputFilePath)){
                    fileWriter.write(basicStatistics);
                }
             } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
