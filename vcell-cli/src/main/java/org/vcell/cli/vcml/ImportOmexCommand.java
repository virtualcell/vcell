package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "import-omex", description = "import a COMBINE archive (.omex) to one or more VCML documents")
public class ImportOmexCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ImportOmexCommand.class);

    @Option(names = { "-i", "--inputFilePath" }, description = "full path to .omex file", required = true)
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, description = "full path to output directory", required = true)
    private File outputFilePath;

    @Option(names = {"--writeLogFiles"}, defaultValue = "false", description = "write log files")
    private boolean bWriteLogFiles;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            PropertyLoader.loadProperties();

            if (!inputFilePath.exists() || !inputFilePath.isFile()){
                throw new RuntimeException("expecting inputFilePath to be an existing file: "+inputFilePath.getAbsolutePath());
            }
            if (!outputFilePath.exists() || !outputFilePath.isDirectory()){
                throw new RuntimeException("expecting outputFilePath to be an existing directory: "+inputFilePath.getAbsolutePath());
            }
            logger.debug("Beginning import");
            VcmlOmexConverter.importOneOmexFile(inputFilePath, outputFilePath, bWriteLogFiles);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Import completed");
        }
    }
}
