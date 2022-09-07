package org.vcell.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vcell.util.logging.*;

import org.vcell.util.VCellUtilityHub;

public class CLILogger extends Logger implements CLILoggable {
    protected final static boolean DEFAULT_SHOULD_PRINT_LOG_FILES = false, DEFAULT_SHOULD_FLUSH_LOG_FILES = false;
    protected boolean shouldPrintLogFiles, shouldFlushLogFiles;
    protected FileLog detailedErrorLog, fullSuccessLog, errorLog, detailedResultsLog, spatialLog, importErrorLog;
    protected Log4JLog logger;
    protected File outputDirectory;

    // Note: this constructor is not public
    protected CLILogger(Class<?> clazz){
        super(clazz);
        selfLogger.debug(this.getClass().getName() + " initializing");
    }

    // Note: this constructor is not public
    private CLILogger(){
        this(CLILogger.class);
    }

    // Note: this constructor is not public
    protected CLILogger(boolean shouldPrintLogFiles, boolean shouldFlushLogFiles){
        this();
        this.shouldPrintLogFiles = shouldPrintLogFiles;
        this.shouldFlushLogFiles = shouldFlushLogFiles;
    }

    public CLILogger(String outputDirectoryPath) throws IOException {
        this(outputDirectoryPath, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILogger(Path outputDirectoryPath) throws IOException {
        this(outputDirectoryPath, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILogger(File outputDirectory) throws IOException {
        this(outputDirectory, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILogger(String outputDirectoryPath, boolean forceLogFiles) throws IOException {
        this(new File(outputDirectoryPath), forceLogFiles, DEFAULT_SHOULD_FLUSH_LOG_FILES);
    }

    public CLILogger(Path outputDirectoryPath, boolean forceLogFiles) throws IOException {
        this(outputDirectoryPath.toFile(), forceLogFiles, DEFAULT_SHOULD_FLUSH_LOG_FILES);
    }

    public CLILogger(File outputDirectory, boolean forceLogFiles) throws IOException {
        this(outputDirectory, forceLogFiles, DEFAULT_SHOULD_FLUSH_LOG_FILES);
    }

    public CLILogger(String outputDirectoryPath, boolean forceLogFiles, boolean shouldFlushLogFiles) throws IOException {
        this(new File(outputDirectoryPath), forceLogFiles, shouldFlushLogFiles);
    }

    public CLILogger(Path outputDirectoryPath, boolean forceLogFiles, boolean shouldFlushLogFiles) throws IOException {
        this(outputDirectoryPath.toFile(), forceLogFiles, shouldFlushLogFiles);
    }

    public CLILogger(File outputDirectory, boolean forceLogFiles, boolean shouldFlushLogFiles) throws IOException {
        this(CLIUtils.isBatchExecution(outputDirectory.getAbsolutePath(), forceLogFiles), shouldFlushLogFiles);
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            String format = "Path: <%s> does not lead to an existing directory, nor could it be created.";
            String message = String.format(format, outputDirectory.getAbsolutePath());
            selfLogger.error(message);
            throw new IllegalArgumentException (message);
        }
        this.outputDirectory = outputDirectory;

        LogManager logManager = VCellUtilityHub.getLogManager();
        this.detailedErrorLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "detailedErrorLog.txt").toString());
        this.fullSuccessLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "fullSuccessLog.txt").toString());
        this.errorLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "errorLog.txt").toString());
        this.detailedResultsLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "detailedResultLog.txt").toString());
        this.spatialLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "hasSpatialLog.txt").toString());
        this.importErrorLog = logManager.requestNewFileLog(Paths.get(this.outputDirectory.getAbsolutePath(), "importErrorLog.txt").toString());
        this.logger = logManager.requestLog4JLog(CLILogger.class);
        
        this.createHeader();
    }

    // Logging Methods

    private void writeToFileLog(FileLog log, String message) throws IOException {
        if (!this.shouldPrintLogFiles) return;
        log.print(message);
        if (this.shouldFlushLogFiles) log.flush();
    }

    public void writeDetailedErrorList(String message) throws IOException {
        this.writeToFileLog(this.detailedErrorLog, message);     
    }

    public void writeFullSuccessList(String message) throws IOException {
        this.writeToFileLog(this.fullSuccessLog, message);
    }

    public void writeErrorList(String message) throws IOException {
        this.writeToFileLog(this.errorLog, message);
    }

    public void writeDetailedResultList(String message) throws IOException {
        this.writeToFileLog(this.detailedResultsLog, message);
    }

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    public void writeSpatialList(String message) throws IOException {
        this.writeToFileLog(this.spatialLog, message);
    }

    public void writeImportErrorList(String message) throws IOException {
        this.writeToFileLog(this.importErrorLog, message);
    }

    // Helper Methods

    private void createHeader() throws IOException { 
        String header =         // Header Components:
            "BaseName," +           // base name of the omex file
            "SedML," +              // (current) sed-ml file name and its...
            "Error," +              // error(s) (if any)
            "Models," +             // number of models
            "Sims," +               // number of simulations
            "Tasks," +              // number of tasks
            "Outputs," +            // number of outputs
            "BioModels," +          // number of biomodels
            "NumSimsSuccessful";    // number of succesful sims that we managed to run
        // (NB: we assume that the # of failures = # of tasks - # of successful simulations)
        // (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
        this.writeDetailedResultList(header);
    }
}
