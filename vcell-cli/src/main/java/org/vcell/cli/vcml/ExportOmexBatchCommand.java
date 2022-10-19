package org.vcell.cli.vcml;

import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

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

    @Option(names = { "-i", "--inputFilePath" }, description = "directory of .vcml files")
    private File inputFilePath;

    @Option(names = { "-o", "--outputFilePath" })
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

    @Option(names = "--forceLogFiles")
    boolean bForceLogFiles;

    @Option(names = "--keepFlushingLogs")
    boolean bKeepFlushingLogs;

    @Option(names = "--offline")
    boolean bOffline=false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        CLIRecorder cliRecorder = null;

        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            
            logger.debug("Batch export of omex files requested");
            PropertyLoader.loadProperties();
            if (inputFilePath == null || !inputFilePath.exists() || !inputFilePath.isDirectory())
                throw new RuntimeException("inputFilePath '" + inputFilePath == null ? "" : inputFilePath + "' is not a 'valid directory'");
            
            if (outputFilePath == null)
                throw new RuntimeException("outputFilePath '" + outputFilePath == null ? "" : outputFilePath + "' is not a 'valid directory'");
            
            cliRecorder = new CLIRecorder(outputFilePath); // CLILogger will throw an execption if our output dir isn't valid.

            if (bOffline) this.runInOfflineMode(cliRecorder);
            else this.run(cliRecorder);
            
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch export completed");
        }
    }

    private void runInOfflineMode(CLIRecorder cliRecorder) throws IOException {
        logger.info("Offline mode selected.");
        VcmlOmexConverter.convertFilesNoDatabse(inputFilePath, outputFilePath, outputModelFormat, cliRecorder, bForceLogFiles, bValidateOmex, bOffline);
    }

    private void run(CLIRecorder cliRecorder) throws IOException {
        logger.info("Online mode selected");
        try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
            VcmlOmexConverter.queryVCellDbPublishedModels(cliDatabaseService, outputFilePath, bForceLogFiles);

            VcmlOmexConverter.convertFiles(cliDatabaseService, inputFilePath, outputFilePath,
                    outputModelFormat, cliRecorder, bHasDataOnly, bMakeLogsOnly, bNonSpatialOnly, bForceLogFiles, bValidateOmex, bOffline);
        } catch (IOException | SQLException | DataAccessException e) {
            e.printStackTrace(System.err);
        }
    }
}
