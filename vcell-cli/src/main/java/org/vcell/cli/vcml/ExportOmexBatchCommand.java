package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.cli.CLIRecorder;
import org.vcell.sedml.ModelFormat;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "export-omex-batch", description = "convert directory of VCML documents to COMBINE archives (.omex)")
public class ExportOmexBatchCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ExportOmexBatchCommand.class);

    @Option(names = { "-m", "--outputModelFormat" }, defaultValue = "SBML", description = "expecting SBML or VCML")
    private ModelFormat outputModelFormat = ModelFormat.SBML;

    @Option(names = { "-i", "--inputFilePath" }, required = true, description = "directory of .vcml files")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" }, required = true, description = "directory to create .omex files")
    private File outputFilePath;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = "--hasDataOnly")
    boolean bHasDataOnly;

    @Option(names = "--makeLogsOnly")
    boolean bMakeLogsOnly;

    @Option(names = "--nonSpatialOnly")
    boolean bNonSpatialOnly;

    @Option(names = "--validate")
    boolean bValidateOmex;

    @Option(names = "--writeLogFiles", defaultValue = "false")
    boolean bWriteLogFiles = false;

    @Option(names = { "--skipUnsupportedApps" }, defaultValue = "false", description = "skip unsupported applications (e.g. electrical in SBML)")
    private boolean bSkipUnsupportedApps = false;

    @Option(names = "--keepFlushingLogs")
    boolean bKeepFlushingLogs;

    @Option(names = "--offline")
    boolean bOffline=false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            
            logger.debug("Batch export of omex files requested");
            PropertyLoader.loadProperties();
            if (inputFilePath == null || !inputFilePath.exists() || !inputFilePath.isDirectory())
                throw new RuntimeException("inputFilePath '" + (inputFilePath == null ? "" : inputFilePath) + "' is not a 'valid directory'");
            
            if (outputFilePath.exists() && !outputFilePath.isDirectory())
                throw new RuntimeException("outputFilePath '" + outputFilePath + "' is not a 'valid directory'");

            if (bOffline) this.runInOfflineMode();
            else this.run();
            
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch export completed");
        }
    }

    private void runInOfflineMode() throws IOException {
        logger.info("Offline mode selected.");
        VcmlOmexConverter.convertFilesNoDatabase(
                inputFilePath, outputFilePath, outputModelFormat, bWriteLogFiles, bValidateOmex, bSkipUnsupportedApps);
    }

    private void run() {
        logger.info("Online mode selected");
        try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
            VcmlOmexConverter.queryVCellDbPublishedModels(cliDatabaseService, outputFilePath, bWriteLogFiles);

            VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                    outputModelFormat, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bWriteLogFiles, bValidateOmex, bSkipUnsupportedApps);
        } catch (IOException | SQLException | DataAccessException e) {
            e.printStackTrace(System.err);
        }
    }
}
