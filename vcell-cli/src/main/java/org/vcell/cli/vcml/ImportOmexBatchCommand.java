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

@Command(name = "import-omex-batch", description = "convert directory of COMBINE archives (.omex) to VCML documents (1 COMBINE archive could yeild more than one BioModel).")
public class ImportOmexBatchCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ImportOmexBatchCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, description = "directory of .omex files", required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, description = "full path to output directory", required = true)
    private File outputFilePath;

    @Option(names = { "--coerceToDistributed" }, defaultValue = "true", description = "import SBML lumped reactions as VCell distributed reactions if possible")
    private boolean bCoerceToDistributed = true;

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
            if (!inputFilePath.exists() || !inputFilePath.isDirectory()){
                throw new RuntimeException("inputFilePath '"+inputFilePath+"' should be a directory");
            }
            if (!outputFilePath.exists() || !outputFilePath.isDirectory()){
                throw new RuntimeException("outputFilePath '"+outputFilePath+"' should be a directory");
            }
            logger.debug("Beginning batch import");
            VcmlOmexConverter.importOmexFiles(inputFilePath, outputFilePath, cliLogger, true, bCoerceToDistributed);
            return 0;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch import completed");
        }
    }
}
