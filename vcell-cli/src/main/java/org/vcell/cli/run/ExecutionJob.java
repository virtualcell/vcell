package org.vcell.cli.run;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;

import org.jlibsedml.*;

import org.apache.commons.io.FilenameUtils;
import org.vcell.cli.CLIRecorder;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.run.hdf5.Hdf5DatasetWrapper;
import org.vcell.cli.run.hdf5.Hdf5FileWrapper;
import org.vcell.cli.run.hdf5.Hdf5Writer;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.util.FileUtils;
import org.vcell.util.GenericExtensionFilter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExecutionJob {

    private final static Logger logger = LogManager.getLogger(ExecutionJob.class);

    private int nModels, nSimulations, nTasks, nOutputs, nReportsCount = 0, nPlots2DCount = 0, nPlots3DCount = 0;
    private long startTime, endTime; 

    private boolean hasOverrides = false, hasScans = false, bExactMatchOnly, bSmallMeshOverride, bKeepTempFiles;
    private String logOmexMessage, inputFilePath, bioModelBaseName, outputBaseDir, outputDir;
    private boolean anySedmlDocumentHasSucceeded = false;    // set to true if at least one sedml document run is successful
    private boolean anySedmlDocumentHasFailed = false;       // set to true if at least one sedml document run fails
    
    private OmexHandler omexHandler = null;
    private List<String> sedmlLocations;
    private List<Output> outputs;
    private SedML sedml;
    private Path sedmlPath2d3d;
    private File inputFile, sedmlPathwith2dand3d;

    private CLIRecorder cliRecorder;

    public ExecutionJob(File inputFile, File rootOutputDir, CLIRecorder cliRecorder,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bEncapsulateOutput, boolean bSmallMeshOverride){
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

    public ExecutionJob(){
        this.logOmexMessage = "";
    }

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

    public void executeArchive() throws InterruptedException, PythonStreamException, IOException, ExecutionException {
        try {
            this.queueAllSedml();

            for (String sedmlLocation : this.sedmlLocations){
                SedmlJob job = new SedmlJob(sedmlLocation, inputFile, new File(outputBaseDir), outputDir, sedmlPath2d3d.toString(), 
                    cliRecorder, this.bKeepTempFiles, this.bExactMatchOnly, this.bSmallMeshOverride);
            }
        } catch(PythonStreamException e){
            logger.error("Python-processing encountered fatal error. Execution is unable to properly continue.", e);
            throw e;
        } catch(InterruptedException|IOException e){
            logger.error("System IO encountered a fatal error");
            throw new ExecutionException();
        } 
    }

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
            logOmexMessage += error;
            //CLIUtils.writeErrorList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            cliRecorder.writeErrorList(bioModelBaseName);
        } else {
            PythonCalls.updateOmexStatusYml(Status.SUCCEEDED, outputDir, duration + "");
            //CLIUtils.writeFullSuccessList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            cliRecorder.writeFullSuccessList(bioModelBaseName);
            logOmexMessage += " Done";
            
        }
        PythonCalls.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage);
        logger.debug("Finished Execution of Archive: " + bioModelBaseName);
    }

    public List<String> getAllSedmlLocations(){
        return this.sedmlLocations;
    }

    private void queueAllSedml() throws PythonStreamException, InterruptedException, IOException {
        for (String sedmlLocation: sedmlLocations){
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.QUEUED, outputDir);
        }
    }

}
