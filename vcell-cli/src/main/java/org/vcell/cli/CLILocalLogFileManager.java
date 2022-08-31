package org.vcell.cli;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuffer;
import java.util.List;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CLILocalLogFileManager implements LocalLogFileManager {

    private final static boolean DEFAULT_SHOULD_PRINT_LOG_FILES = false;
    private final static Logger logger = LogManager.getLogger(CLILocalLogFileManager.class);
    private final static LocalLogFileName[] localLogFileNames = {
        new LocalLogFileName("detailedErrorLog.txt"),
        new LocalLogFileName("fullSuccessLog.txt"),
        new LocalLogFileName("errorLog.txt"),
        new LocalLogFileName("detailedResultLog.txt")
    };

    private final static int DETAILED_ERROR_LOG = 0; 
    private final static int FULL_SUCCESS_LOG = 1; 
    private final static int ERROR_LOG = 2; 
    private final static int DETAILED_RESULTS_LOG = 3; 

    private boolean shouldPrintLogFiles;
    private Map<LocalLogFileName, StringBuffer> logFileBuffers;
    private File outputDirectory;

    // Note: this constructor is private
    private CLILocalLogFileManager(){
        logger.debug(this.getClass().getName() + " initializing");
        this.logFileBuffers = new HashMap<>();
        for(LocalLogFileName name : this.getAllLocalLogFileName())
            this.logFileBuffers.put(name, null);
    }

    // Note: this constructor is private
    private CLILocalLogFileManager(boolean shouldPrintLogFiles){
        this();
        this.shouldPrintLogFiles = shouldPrintLogFiles;
    }

    public CLILocalLogFileManager(String outputDirectoryPath){
        this(outputDirectoryPath, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILocalLogFileManager(Path outputDirectoryPath){
        this(outputDirectoryPath, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILocalLogFileManager(File outputDirectory){
        this(outputDirectory, DEFAULT_SHOULD_PRINT_LOG_FILES);
    }

    public CLILocalLogFileManager(String outputDirectoryPath, boolean forceLogFiles){
        this(new File(outputDirectoryPath), forceLogFiles);
    }

    public CLILocalLogFileManager(Path outputDirectoryPath, boolean forceLogFiles){
        this(outputDirectoryPath.toFile(), forceLogFiles);
    }

    public CLILocalLogFileManager(File outputDirectory, boolean forceLogFiles){
        this(CLIUtils.isBatchExecution(outputDirectory.getAbsolutePath(), forceLogFiles));
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            String format = "Path: <%s> does not lead to an existing directory, nor could it be created.";
            String message = String.format(format, outputDirectory.getAbsolutePath());
            logger.error(message);
            throw new IllegalArgumentException (message);
        }
            
        this.outputDirectory = outputDirectory;
        this.createHeader();
    }

    public List<LocalLogFileName> getAllLocalLogFileName(){
        return Arrays.asList(localLogFileNames);
    }

    public void writeDetailedErrorList(String message) {
        this.appendToLogFile(this.getAllLocalLogFileName().get(DETAILED_ERROR_LOG), message);
    }

    public void writeFullSuccessList(String message) {
        this.appendToLogFile(this.getAllLocalLogFileName().get(FULL_SUCCESS_LOG), message);
    }

    public void writeErrorList(String message) {
        this.appendToLogFile(this.getAllLocalLogFileName().get(ERROR_LOG), message);
    }

    public void writeDetailedResultList(String message)  {
        this.appendToLogFile(this.getAllLocalLogFileName().get(DETAILED_RESULTS_LOG), message);
    }

    public File getLogFile(LocalLogFileName logFile){
        return new File(this.outputDirectory, logFile.getFileName());
    }

    public String getLogFileContents(LocalLogFileName logFile){
        return this.logFileBuffers.get(logFile).toString();
    }

    public void clearLogFile(LocalLogFileName file){
        this.logFileBuffers.put(file, null);
    }

    public void clearAllLogFiles(){
        for (LocalLogFileName logFile : this.getAllLocalLogFileName()) this.clearLogFile(logFile);
    }

    public boolean finalizeAndExportLogFiles(){
        boolean didSucceed = false;
        if (!outputDirectory.exists()) return false;
        logger.debug("Exporting CLI Local Log Files");
        try {
            for (LocalLogFileName logFileType : this.getAllLocalLogFileName()){
                if (this.logFileBuffers.containsKey(logFileType) && this.logFileBuffers.get(logFileType) != null) {
                    this.writeToSystemFile(logFileType);
                    this.clearLogFile(logFileType);
                }
            }    
            didSucceed = true;
        } catch (IOException e){
            logger.warn("Encountered exception while exporting LogFiles", e);
        }
        return didSucceed;
    }

    private void writeToSystemFile(LocalLogFileName name) throws IOException {
        if (!this.shouldPrintLogFiles) return;
        File targetFile = this.getLogFile(name);
        FileWriter writer = new FileWriter(targetFile);
        StringBuffer buffer = logFileBuffers.get(name);
        writer.write(buffer.toString());
        writer.close();
    }

    private void appendToLogFile(LocalLogFileName name, String message) {
        if (this.logFileBuffers.get(name) == null) this.logFileBuffers.put(name, new StringBuffer());
        this.logFileBuffers.get(name).append(message + '\n');
    }

    /**
     * Header Components:
     *  * base name of the omex file
     *  *   foreach sed-ml file:
     *  *   - (current) sed-ml file name
     *  *   - error(s) (if any)
     *  *   - number of models
     *  *   - number of sims
     *  *   - number of tasks
     *  *   - number of outputs
     *  *   - number of biomodels
     *  *   - number of succesful sims that we managed to run
     *  *   (NB: we assume that the # of failures = # of tasks - # of successful simulations)
     *  *   (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
     */
    private void createHeader() {
        String header = "BaseName,SedML,Error,Models,Sims,Tasks,Outputs,BioModels,NumSimsSuccessful";
        this.writeDetailedResultList(header);
    }
}
