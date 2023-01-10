package org.vcell.cli.run;

import org.vcell.cli.CLIRecorder;
import org.vcell.cli.PythonStreamException;
import org.vcell.util.FileUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Contains the code necessary to execute an Omex archive in VCell
 */
public class ExecutionJob {

    private final static Logger logger = LogManager.getLogger(ExecutionJob.class);

    private long startTime, endTime; 
    private boolean bExactMatchOnly, bSmallMeshOverride, bKeepTempFiles;
    private StringBuilder logOmexMessage;
    private String inputFilePath, bioModelBaseName, outputBaseDir, outputDir;
    private boolean anySedmlDocumentHasSucceeded = false;    // set to true if at least one sedml document run is successful
    private boolean anySedmlDocumentHasFailed = false;       // set to true if at least one sedml document run fails
    
    private OmexHandler omexHandler = null;
    private List<String> sedmlLocations;
    private Path sedmlPath2d3d;
    private File inputFile;

    private CLIRecorder cliRecorder;

    /** 
     * Constructor to provide all necessary info.
     * 
     * @param inputFile the omex archive to execute
     * @param rootOutputDir the top-level directory for all output material
     * @param cliRecorder recorder objecte used for CLI applications
     * @param bKeepTempFiles whether temp files shouldn't be deleted, or should.
     * @param bExactMatchOnly enforces a KISAO match, with no substitution
     * @param bEncapsulateOutput whether to provide a sub-folder for outputs (needed for batch jobs)
     * @param bSmallMeshOverride whether to use small meshes or standard meshes.
     */
    public ExecutionJob(File inputFile, File rootOutputDir, CLIRecorder cliRecorder,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bEncapsulateOutput, boolean bSmallMeshOverride){
        this();
        this.inputFile = inputFile;
        this.cliRecorder = cliRecorder;
        
        this.inputFilePath = inputFile.getAbsolutePath();
        this.bioModelBaseName = FileUtils.getBaseName(inputFile.getName()); // input file without the path
        this.outputBaseDir = rootOutputDir.getAbsolutePath();
        this.outputDir = bEncapsulateOutput ? Paths.get(outputBaseDir, bioModelBaseName).toString() : outputBaseDir;
        this.bKeepTempFiles = bKeepTempFiles;
        this.bExactMatchOnly = bExactMatchOnly;
        this.bSmallMeshOverride = bSmallMeshOverride;
    }

    private ExecutionJob(){
        this.logOmexMessage = new StringBuilder("");
    }

    /**
     * Run the neexed steps to prepare an archive for execution.
     * 
     * Follow up call: `executeArchive()`
     * 
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues.
     */
    public void preprocessArchive() throws PythonStreamException, IOException {
        // Start the clock
        this.startTime = System.currentTimeMillis();

        // Beginning of Execution
        logger.info("Executing OMEX archive " + inputFilePath);
        RunUtils.drawBreakLine("-", 100);

        // Unpack the Omex Archive
        try { // It's unlikely, but if we get errors here they're fatal.
            this.sedmlPath2d3d = Paths.get(outputDir, "temp");
            this.omexHandler = new OmexHandler(inputFilePath, outputDir);
            this.omexHandler.extractOmex();
            this.sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
        } catch (IOException e){
            String error = e.getMessage() + ", error for OmexHandler with " + inputFilePath;
            this.cliRecorder.writeErrorList(bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(bioModelBaseName + ", " + "IO error with OmexHandler");
            logger.error(error);
            throw new RuntimeException(error, e);
        } catch (Exception e) { 
            omexHandler.deleteExtractedOmex();
            String error = e.getMessage() + ", error for archive " + inputFilePath;
            this.cliRecorder.writeErrorList(bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(bioModelBaseName + ", " + "unknown error with the archive file");
            logger.error(error);
            throw new RuntimeException(error, e);
        } 
        
        // Update Status
        PythonCalls.updateOmexStatusYml(Status.RUNNING, outputDir, "0");
    }

    /**
     * Run solvers on all the models in the archive.
     * 
     * Called after: `preprocessArchive()`
     * Called before: `postProcessArchive()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     * @throws ExecutionException if an execution specfic error occurs
     */
    public void executeArchive() throws InterruptedException, PythonStreamException, IOException, ExecutionException {
        try {
            this.queueAllSedml();

            for (String sedmlLocation : this.sedmlLocations){
                SedmlJob job = new SedmlJob(sedmlLocation, this.omexHandler, this.inputFile, new File(this.outputBaseDir), this.outputDir, this.sedmlPath2d3d.toString(), 
                    this.cliRecorder, this.bKeepTempFiles, this.bExactMatchOnly, this.bSmallMeshOverride, this.logOmexMessage);
                if (!job.preProcessDoc()){
                    SedmlStatistics stats = job.getDocStatistics(); // Must process document first
                    logger.error("Statistics of failed SedML:\n" + stats.toString());
                    this.anySedmlDocumentHasFailed = true;
                }
                SedmlStatistics stats = job.getDocStatistics();
                boolean hasSucceeded = job.simulateSedml();
                this.anySedmlDocumentHasSucceeded |= hasSucceeded;
                this.anySedmlDocumentHasFailed &= hasSucceeded;
                if (hasSucceeded) logger.info("Processing of SedML succeeded.\n" + stats.toString());
                else logger.error("Processing of SedML has failed.\n" + stats.toString());
            }
        } catch(PythonStreamException e){
            logger.error("Python-processing encountered fatal error. Execution is unable to properly continue.", e);
            throw e;
        } catch(InterruptedException|IOException e){
            logger.error("System IO encountered a fatal error");
            throw new ExecutionException();
        } 
    }

    /**
     * Clean up and analyze the results of the archive's execution
     * 
     * Called after: `executeArchive()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     */
    public void postProcessessArchive() throws InterruptedException, PythonStreamException, IOException {
        omexHandler.deleteExtractedOmex();

        this.endTime = System.currentTimeMillis();
        long elapsedTime = this.endTime - this.startTime;
        int duration = (int) Math.ceil(elapsedTime / 1000.0);
        logger.info("Omex " + inputFile.getName() + " processing completed (" + Integer.toString(duration) + "s)");
        //
        // failure if at least one of the documents in the omex archive fails
        //
        if (anySedmlDocumentHasFailed) {
            String error = " All sedml documents in this archive failed to execute";
            if (anySedmlDocumentHasSucceeded) {        // some succeeded, some failed
                error = " At least one document in this archive failed to execute";
            }
            PythonCalls.updateOmexStatusYml(Status.FAILED, outputDir, duration + "");
            logger.error(error);
            logOmexMessage.append(error);
            cliRecorder.writeErrorList(bioModelBaseName);
        } else {
            PythonCalls.updateOmexStatusYml(Status.SUCCEEDED, outputDir, duration + "");
            cliRecorder.writeFullSuccessList(bioModelBaseName);
            logOmexMessage.append(" Done");
            
        }
        PythonCalls.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage.toString());
        logger.debug("Finished Execution of Archive: " + bioModelBaseName);
    }

    private void queueAllSedml() throws PythonStreamException, InterruptedException, IOException {
        for (String sedmlLocation: sedmlLocations){
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.QUEUED, outputDir);
        }
    }

}
