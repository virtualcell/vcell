package org.vcell.cli.commands.conversion;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.cli.vcml.VcmlOmexConverter;
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

    @Option(names = {"--writeLogFiles"}, defaultValue = "true", description = "write log files")
    private boolean bWriteLogFiles = true;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            if (!inputFilePath.exists() || !inputFilePath.isDirectory()){
                throw new RuntimeException("inputFilePath '"+inputFilePath+"' should be a directory");
            }
            if (!outputFilePath.exists() || !outputFilePath.isDirectory()){
                throw new RuntimeException("outputFilePath '"+outputFilePath+"' should be a directory");
            }
            logger.debug("Beginning batch import");
            VcmlOmexConverter.importOmexFiles(inputFilePath, outputFilePath, bWriteLogFiles);
            return 0;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch import completed");
        }
    }
}
