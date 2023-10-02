package org.vcell.cli.vcml;

import org.vcell.cli.vcml.ExportOmexCommand;
import cbit.vcell.resource.PropertyLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.cli.CLIRecorder;
import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.SEDMLEventLog;
import org.vcell.sedml.SEDMLEventLogFile;
import org.vcell.util.DataAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Level logLevel = this.bDebug ? Level.DEBUG : logger.getLevel();
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();
        final SEDMLEventLog sedmlEventLog;
        try {
            logger.debug("Batch export of omex files requested");
            PropertyLoader.loadProperties();
            File parentDir = new File(outputFilePath.getParent()).getCanonicalFile();
            if (this.inputFilePath == null || !this.inputFilePath.exists() || !this.inputFilePath.isDirectory())
                throw new RuntimeException("inputFilePath '" + (this.inputFilePath == null ? "" : this.inputFilePath) + "' is not a valid VCML archive");
            if (this.outputFilePath == null || !this.outputFilePath.isDirectory())
                throw new RuntimeException("outputFilePath '" + (this.outputFilePath == null ? "" : this.outputFilePath) + "' is not a valid target for OMEX archive");
            if (parentDir.exists() && !parentDir.isDirectory()) {
                throw new RuntimeException("directory for output '" + parentDir.getCanonicalPath() + "' is not a valid directory for OMEX archive");
            }
            if (!parentDir.exists())
                if (!parentDir.mkdirs())
                    throw new RuntimeException("Output dir doesn't exist and could not be made!");


            if (bWriteLogFiles) {
                sedmlEventLog = new SEDMLEventLogFile(new File(outputFilePath, "jobLog.txt"));
            } else {
                sedmlEventLog = (String entry) -> {};
            }
        } catch (IOException e){
            throw new RuntimeException("Error in setting up batch execution:\n\t", e);
        }

        try {
            PropertyLoader.loadProperties();

            logger.debug("Batch export of omex files requested");
            int numSuccessfulExports = 0, numTotalFiles = 0;
            Path inputDirPath = this.inputFilePath.getCanonicalFile().toPath();
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(inputDirPath)){
                for (Path child : dirStream){
                    // Begin Setup
                    File childFile, targetOutputFile;
                    try {
                        childFile = child.toFile();
                        if (childFile.isDirectory()){
                            continue;
                        }
                        if (!child.toString().endsWith(".vcml")){
                            continue;
                        }
                        numTotalFiles++;

                        String childFileFullName = childFile.getName();
                        String outputFileName = childFileFullName.substring(0, childFileFullName.length() - 5);
                        targetOutputFile = Paths.get(this.outputFilePath.getCanonicalPath(), outputFileName).toFile();
                    } catch (IOException e){
                        logger.error("Error setting up '" + child.getFileName() + "':\n\t", e);
                        continue;
                    }

                    // Setup Complete, Begin Conversion.
                    try {
                        ExportOmexCommand.exportVCMLFile(childFile, targetOutputFile, this.outputModelFormat, sedmlEventLog,
                                this.bWriteLogFiles, this.bValidateOmex, this.bSkipUnsupportedApps);
                        logger.info("Conversion from '" + child.getFileName()
                                + "' to '" + targetOutputFile.getName() + "' succeeded");
                        numSuccessfulExports++;
                    } catch (Exception e){
                        logger.error("Conversion for '" + child.getFileName() + "' failed:\n\t", e);
                    }
                    logger.error("Continuing to next file to convert.\n\n");
                }
                logger.info(String.format("Batch mode complete.\n\t"
                        + " %d/%d exports were successful.", numSuccessfulExports, numTotalFiles));
            }

        } catch (Exception e){
            logger.error("Unexpected IO Error occurred, ending batch conversion");
            throw new RuntimeException("Unexpected IO error occurred:\n\t", e);
        }
        return 0;
        /* old methodology
            if (bOffline) this.runInOfflineMode();
            else this.run();
            
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("Batch export completed");
        }
        */
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
