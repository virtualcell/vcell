package org.vcell.cli.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.run.hdf5.BiosimulationsHdf5Writer;
import org.vcell.cli.run.hdf5.HDF5ExecutionResults;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.vcell.cli.run.hdf5.BiosimulationsHdfWriterException;

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

    private CLIRecordable cliRecorder;

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
    public ExecutionJob(File inputFile, File rootOutputDir, CLIRecordable cliRecorder,
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
     * Follow-up call: `executeArchive()`
     * 
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues.
     */
    public void preprocessArchive() throws PythonStreamException, IOException {
        // Start the clock
        this.startTime = System.currentTimeMillis();

        // Beginning of Execution
        logger.info("Executing OMEX archive `{}`", this.inputFile.getName());
        logger.info("Archive location: {}", this.inputFilePath);
        RunUtils.drawBreakLine("-", 100);

        // Unpack the Omex Archive
        try { // It's unlikely, but if we get errors here they're fatal.
            this.sedmlPath2d3d = Paths.get(this.outputDir, "temp");
            this.omexHandler = new OmexHandler(this.inputFilePath, this.outputDir);
            this.omexHandler.extractOmex();
            this.sedmlLocations = this.omexHandler.getSedmlLocationsAbsolute();
        } catch (IOException e){
            String error = e.getMessage() + ", error for OmexHandler with " + this.inputFilePath;
            this.cliRecorder.writeErrorList(e, this.bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(this.bioModelBaseName + ", " + "IO error with OmexHandler");
            logger.error(error);
            throw new RuntimeException(error, e);
        } catch (Exception e) {
            String error = e.getMessage() + ", error for archive " + this.inputFilePath;
            logger.error(error);
            if (this.omexHandler != null) this.omexHandler.deleteExtractedOmex();
            this.cliRecorder.writeErrorList(e, this.bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(this.bioModelBaseName + ", " + "unknown error with the archive file");

            throw new RuntimeException(error, e);
        } 
        
        // Update Status
        BiosimulationLog.updateOmexStatusYml(BiosimulationLog.Status.RUNNING, outputDir, "0");
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
    public void executeArchive(boolean isBioSimSedml) throws BiosimulationsHdfWriterException, PythonStreamException, ExecutionException {
        try {
            HDF5ExecutionResults masterHdf5File = new HDF5ExecutionResults(isBioSimSedml);
            this.queueAllSedml();

            for (String sedmlLocation : this.sedmlLocations){
                SedmlJob job = new SedmlJob(sedmlLocation, this.omexHandler, this.inputFile, new File(this.outputBaseDir),
                        this.outputDir, this.sedmlPath2d3d.toString(), this.cliRecorder,
                        this.bKeepTempFiles, this.bExactMatchOnly, this.bSmallMeshOverride, this.logOmexMessage);
                if (!job.preProcessDoc()){
                    SedmlStatistics stats = job.getDocStatistics(); // Must process document first
                    logger.error("Statistics of failed SedML:\n" + stats.toString());
                    this.anySedmlDocumentHasFailed = true;
                }
                SedmlStatistics stats = job.getDocStatistics();
                boolean hasSucceeded = job.simulateSedml(masterHdf5File);
                this.anySedmlDocumentHasSucceeded |= hasSucceeded;
                this.anySedmlDocumentHasFailed &= hasSucceeded;
                if (hasSucceeded){
                    String formattedStats = stats.toFormattedString();
                    logger.info("Processing of SedML succeeded.\n" + formattedStats);
                }
                else logger.error("Processing of SedML has failed.\n" + stats.toString());
            }
            BiosimulationsHdf5Writer.writeHdf5(masterHdf5File, new File(this.outputDir));
            
        } catch(PythonStreamException e){
            logger.error("Python-processing encountered fatal error. Execution is unable to properly continue.", e);
            throw e;
        } catch(InterruptedException | IOException e){
            logger.error("System IO encountered a fatal error");
            throw new ExecutionException(e);
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
            BiosimulationLog.updateOmexStatusYml(BiosimulationLog.Status.FAILED, outputDir, duration + "");
            logger.error(error);
            logOmexMessage.append(error);
            cliRecorder.writeErrorList(new Exception("exception not recorded"), bioModelBaseName);
        } else {
            BiosimulationLog.updateOmexStatusYml(BiosimulationLog.Status.SUCCEEDED, outputDir, duration + "");
            cliRecorder.writeFullSuccessList(bioModelBaseName);
            logOmexMessage.append(" Done");
            
        }
        BiosimulationLog.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage.toString());

        logger.debug("Finished Execution of Archive: " + bioModelBaseName);
    }

    private void queueAllSedml() throws PythonStreamException, InterruptedException, IOException {
        for (String sedmlLocation: sedmlLocations){
            BiosimulationLog.updateSedmlDocStatusYml(sedmlLocation, BiosimulationLog.Status.QUEUED, outputDir);
        }
    }

}
