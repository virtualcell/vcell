package org.vcell.cli.run;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.exceptions.PreProcessingException;
import org.vcell.cli.run.hdf5.BiosimulationsHdf5Writer;
import org.vcell.cli.run.hdf5.BiosimulationsHdfWriterException;
import org.vcell.cli.run.hdf5.HDF5ExecutionResults;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.FileUtils;

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

    private long startTime_ms, endTime_ms;
    private boolean bExactMatchOnly, bSmallMeshOverride, bKeepTempFiles;
    private final StringBuilder logOmexMessage;
    private String bioModelBaseName;
    private String outputDir;
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

        this.bioModelBaseName = FileUtils.getBaseName(inputFile.getName()); // input file without the path
        String outputBaseDir = rootOutputDir.getAbsolutePath();
        this.outputDir = bEncapsulateOutput ? Paths.get(outputBaseDir, bioModelBaseName).toString() : outputBaseDir;
        this.bKeepTempFiles = bKeepTempFiles;
        this.bExactMatchOnly = bExactMatchOnly;
        this.bSmallMeshOverride = bSmallMeshOverride;
    }

    private ExecutionJob(){
        this.logOmexMessage = new StringBuilder();
    }

    /**
     * Run the neexed steps to prepare an archive for execution.
     * Follow-up call: `executeArchive()`
     *
     * @throws IOException if there are system I/O issues.
     */
    public void preprocessArchive() throws IOException {
        // Start the clock
        this.startTime_ms = System.currentTimeMillis();

        // Beginning of Execution
        logger.info("Executing OMEX archive `{}`", this.inputFile.getName());
        if (logger.isDebugEnabled()) logger.info("Archive location: {}", this.inputFile.getAbsolutePath());
        RunUtils.drawBreakLine("-", 100);

        // Unpack the Omex Archive
        try { // It's unlikely, but if we get errors here they're fatal.
            this.sedmlPath2d3d = Paths.get(this.outputDir, "temp");
            this.omexHandler = new OmexHandler(this.inputFile.getAbsolutePath(), this.outputDir);
            this.omexHandler.extractOmex();
            this.sedmlLocations = this.omexHandler.getSedmlLocationsAbsolute();
        } catch (IOException e){
            String error = e.getMessage() + ", error for OmexHandler with " + this.inputFile.getAbsolutePath();
            this.cliRecorder.writeErrorList(e, this.bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(this.bioModelBaseName + ", " + "IO error with OmexHandler");
            logger.error(error);
            throw new RuntimeException(error, e);
        } catch (Exception e) {
            String error = e.getMessage() + ", error for archive " + this.inputFile.getAbsolutePath();
            logger.error(error);
            if (this.omexHandler != null) this.omexHandler.deleteExtractedOmex();
            this.cliRecorder.writeErrorList(e, this.bioModelBaseName);
            this.cliRecorder.writeDetailedResultList(this.bioModelBaseName + ", " + "unknown error with the archive file");

            throw new RuntimeException(error, e);
        } 
        
        // Update Status
        BiosimulationLog.instance().updateOmexStatusYml(BiosimulationLog.Status.RUNNING, 0.0);
    }

    /**
     * Run solvers on all the models in the archive.
     * </br>
     * Called after: `preprocessArchive()`
     * Called before: `postProcessArchive()`
     *
     * @throws ExecutionException if an execution specific error occurs
     */
    public void executeArchive(boolean isBioSimSedml) throws BiosimulationsHdfWriterException, ExecutionException {
        HDF5ExecutionResults cumulativeHdf5Results = new HDF5ExecutionResults(isBioSimSedml);

        try {
            for (String sedmlLocation : this.sedmlLocations) {
                try {
                    this.executeSedmlDocument(sedmlLocation, cumulativeHdf5Results);
                } catch (PreProcessingException e) {
                    this.anySedmlDocumentHasFailed = true;
                }
            }
            BiosimulationsHdf5Writer.writeHdf5(cumulativeHdf5Results, new File(this.outputDir));

        } catch (IOException e){
            logger.error("System IO encountered a fatal error");
            throw new ExecutionException(e);
        }
    }

    private void executeSedmlDocument(String sedmlLocation, HDF5ExecutionResults cumulativeHdf5Results) throws IOException, PreProcessingException {
        BiosimulationLog.instance().updateSedmlDocStatusYml(sedmlLocation, BiosimulationLog.Status.QUEUED);
        SedmlJob job = new SedmlJob(sedmlLocation, this.omexHandler, this.inputFile,
                this.outputDir, this.sedmlPath2d3d.toString(), this.cliRecorder,
                this.bKeepTempFiles, this.bExactMatchOnly, this.bSmallMeshOverride, this.logOmexMessage);
        SedmlStatistics stats = job.preProcessDoc();
        boolean hasSucceeded = job.simulateSedml(cumulativeHdf5Results);
        this.anySedmlDocumentHasSucceeded |= hasSucceeded;
        this.anySedmlDocumentHasFailed &= hasSucceeded;
        logger.log(hasSucceeded ? Level.INFO : Level.ERROR, "Processing of SedML ({}) {}", stats.getSedmlName(), hasSucceeded ? "succeeded." : "failed!");
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
    public void postProcessessArchive() throws IOException {
        omexHandler.deleteExtractedOmex();

        this.endTime_ms = System.currentTimeMillis();
        long elapsedTime_ms = this.endTime_ms - this.startTime_ms;
        double duration_s = elapsedTime_ms / 1000.0;
        logger.info("Omex `" + inputFile.getName() + "` processing completed (" + duration_s + "s)");
        //
        // failure if at least one of the documents in the omex archive fails
        //
        if (anySedmlDocumentHasFailed) {
            String error = " All sedml documents in this archive failed to execute";
            if (anySedmlDocumentHasSucceeded) {        // some succeeded, some failed
                error = " At least one document in this archive failed to execute";
            }
            BiosimulationLog.instance().updateOmexStatusYml(BiosimulationLog.Status.FAILED, duration_s);
            logger.error(error);
            logOmexMessage.append(error);
            cliRecorder.writeErrorList(new Exception("exception not recorded"), bioModelBaseName);
        } else {
            BiosimulationLog.instance().updateOmexStatusYml(BiosimulationLog.Status.SUCCEEDED, duration_s);
            cliRecorder.writeFullSuccessList(bioModelBaseName);
            logOmexMessage.append(" Done");
            
        }
        BiosimulationLog.instance().setOutputMessage("null", "null", "omex", logOmexMessage.toString());

        if (logger.isDebugEnabled()) logger.info("Finished Execution of Archive: {}", this.bioModelBaseName);
    }

}
