package org.vcell.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vcell.util.VCellUtilityHub;
import org.vcell.util.recording.CLIRecordManager;
import org.vcell.util.recording.Recorder;
import org.vcell.util.recording.TextFileRecord;

/**
 * Creates records for VCell CLI and the Record Management System (see `org.vcell.util.recording.*`)
 */
public class CLIRecorder extends Recorder implements CLIRecordable {
    protected final static boolean DEFAULT_SHOULD_PRINT_LOG_FILES = false, DEFAULT_SHOULD_FLUSH_LOG_FILES = false;
    protected boolean shouldPrintLogFiles, shouldFlushLogFiles;
    protected TextFileRecord detailedErrorLog, fullSuccessLog, errorLog, detailedResultsLog, spatialLog, importErrorLog;
    protected File outputDirectory;

    // Note: this constructor is not public
    protected CLIRecorder(Class<?> clazz){
        super(clazz);
        selfLogger.debug(this.getClass().getName() + " initializing");
    }

    // Note: this constructor is not public
    private CLIRecorder(){
        this(CLIRecorder.class);
    }

    // Note: this constructor is not public
    protected CLIRecorder(boolean shouldPrintLogFiles, boolean shouldFlushLogFiles){
        this();
        this.shouldPrintLogFiles = shouldPrintLogFiles;
        this.shouldFlushLogFiles = shouldFlushLogFiles;
    }

    /**
     * Basic constructor, will not override any functionality
     * 
     * @param outputDirectoryPath where to put records (if applicable)
     * @throws IOException if there is a system IO issue breaking execution
     */
    public CLIRecorder(File outputDirectoryPath) throws IOException {
        this(outputDirectoryPath, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    /**
     * Constructor that allows for forcing the creation of logs, even processing happens all in the same directory, allowing overrides.
     * 
     * @param outputDirectory where to put records (if applicable)
     * @param forceLogFiles whether vcell should force log files to be created
     * @throws IOException if there is a system IO issue breaking execution.
     */
    public CLIRecorder(File outputDirectory, boolean forceLogFiles) throws IOException {
        this(outputDirectory, forceLogFiles, DEFAULT_SHOULD_FLUSH_LOG_FILES);
    }

    /**
     * 
     * @param outputDirectory where to put records (if applicable)
     * @param forceLogFiles whether vcell should force log files to be created
     * @param shouldFlushLogFiles whether vcell should flush all changes to logs immediately to the system.
     * @throws IOException if there is a system IO issue breaking execution.
     */
    public CLIRecorder(File outputDirectory, boolean forceLogFiles, boolean shouldFlushLogFiles) throws IOException {
        this(isBatchExecution(outputDirectory.getAbsolutePath(), forceLogFiles), shouldFlushLogFiles);
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            String format = "Path: <%s> does not lead to an existing directory, nor could it be created.";
            String message = String.format(format, outputDirectory.getAbsolutePath());
            selfLogger.error(message);
            throw new IllegalArgumentException (message);
        }
        this.outputDirectory = outputDirectory;

        CLIRecordManager logManager;
        if (VCellUtilityHub.getLogManager() instanceof CLIRecordManager){
            logManager = (CLIRecordManager)VCellUtilityHub.getLogManager();
        } else {
            throw new RuntimeException("LogManager is not initalized to CLI-mode");
        }
        this.detailedErrorLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "detailedErrorLog.txt").toString());
        this.fullSuccessLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "fullSuccessLog.txt").toString());
        this.errorLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "errorLog.txt").toString());
        this.detailedResultsLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "detailedResultLog.txt").toString());
        this.spatialLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "hasSpatialLog.txt").toString());
        this.importErrorLog = logManager.requestNewRecord(Paths.get(this.outputDirectory.getAbsolutePath(), "importErrorLog.txt").toString());
        
        this.createHeader();
    }

    /**
     * Determines whether a single file or a batch of files were submitted for processing in VCell CLI (or should be treated as a batch execution);
     *
     * @param outputBaseDir output dir of the execution
     * @param bForceKeepLogs whether VCell has been asked to force keeping logs
     * @return whether the execution should be treated as a batch execution
     * @Deprecated: this method should be removed,
     */
    private static boolean isBatchExecution(String outputBaseDir, boolean bForceKeepLogs) {
        // TODO: remove this method
        Path path = Paths.get(outputBaseDir);
        boolean isDirectory = Files.isDirectory(path);
        return isDirectory || bForceKeepLogs;
    }


    // Logging file methods

    private void writeToFileLog(TextFileRecord log, String message) throws IOException {
        if (!this.shouldPrintLogFiles) return;
        log.print(message + "\n");
        if (this.shouldFlushLogFiles) log.flush();
    }

    /**
     * Write to `detailedErrorLog.txt`
     * 
     * @param message string to write to file
     */
    @Override
    public void writeDetailedErrorList(Exception _e, String message) throws IOException {
        this.writeToFileLog(this.detailedErrorLog, message);     
    }

    /**
     * Write to `fullSuccessLog.txt`
     * 
     * @param message string to write to file
     */
    @Override
    public void writeFullSuccessList(String message) throws IOException {
        this.writeToFileLog(this.fullSuccessLog, message);
    }

    /**
     * Write to `errorLog.txt`
     * 
     * @param message string to write to file
     */
    @Override
    public void writeErrorList(Exception _e, String message) throws IOException {
        this.writeToFileLog(this.errorLog, message);
    }

    /**
     * Write to `detailedResultLog.txt`
     * 
     * @param message string to write to file
     */
    @Override
    public void writeDetailedResultList(String message) throws IOException {
        this.writeToFileLog(this.detailedResultsLog, message);
    }

    
    /**
     * Write to `hasSpatialLog.txt`
     * 
     * @param message string to write to file
     */
    public void writeSpatialList(String message) throws IOException {
        // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
        this.writeToFileLog(this.spatialLog, message);
    }

    /**
     * Write to `importErrorLog.txt`
     * 
     * @param message string to write to file
     */
    @Override
    public void writeImportErrorList(Exception _e, String message) throws IOException {
        this.writeToFileLog(this.importErrorLog, message);
    }

    // Helper Methods

    private void createHeader() throws IOException { 
        String header =         // Header Components:
            "BaseName," +           // base name of the omex file
            "Models," +             // number of sedml models
            "Sims," +               // number of sedml simulations
            "Tasks," +              // number of sedmltasks
            "Outputs," +            // number of sedmloutputs
            
            "BioModels," +          // number of biomodels
            "HasOverrides," +		// true if any vcell simulation has math overrides
            "HasScans," +			// true if any vcell simulation has param scans
            "NumSimsSuccessful";    // number of succesful sims that we managed to run
        // (NB: we assume that the # of failures = # of tasks - # of successful simulations)
        // (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
        this.writeDetailedResultList(header);
    }
}
