package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.CLIRecorder;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "validate", description = "validate either a directory or single VCML file.")
public class ValidateBatchCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ValidateBatchCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, description = "directory of .vcml files or single .vcml file", required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, description = "full path to output directory", required = true)
    private File outputFilePath;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    // TODO: add forceLogFile option to command and pass down

    public Integer call() {
        CLIRecorder cliLogger = null;

        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            boolean bForceLogFiles = true;
            boolean bKeepFlushingLogs = true;
            cliLogger = new CLIRecorder(outputFilePath, bForceLogFiles, bKeepFlushingLogs);
            PropertyLoader.loadProperties();
            if (!inputFilePath.exists()){
                throw new RuntimeException("inputFilePath '"+inputFilePath+"' should be a directory or single .vcml file");
            }
            if (!outputFilePath.exists() || !outputFilePath.isDirectory()){
                throw new RuntimeException("outputFilePath '"+outputFilePath+"' should be a directory");
            }
            if (inputFilePath.isDirectory()) {
                logger.debug("Beginning batch validation");
                VcmlValidator.validateVcmlFiles(inputFilePath, outputFilePath, cliLogger, true);
            }else{
                logger.debug("Beginning validation");
                VcmlValidator.validateOneVcmlFile(inputFilePath, outputFilePath, true);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch import completed");
        }
    }
}
