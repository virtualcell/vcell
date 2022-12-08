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

    private boolean hasOverrides = false, hasScans = false;
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
    }

    public ExecutionJob(){
        this.logOmexMessage = "";
    }

    public void beginExecution() throws PythonStreamException, IOException {
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

    public void queueAllSedml() throws PythonStreamException, InterruptedException, IOException {
        for (String sedmlLocation: sedmlLocations){
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.QUEUED, outputDir);
        }
    }

    public List<String> getAllSedmlLocations(){
        return this.sedmlLocations;
    }

    // This method is a bit weird; it uses a temp file as a reference to compare against while getting the file straight from the archive.
    private static SedML getSedMLFile(String[] tokenizedPath, File inputFile) throws FileNotFoundException, XMLException, IOException {
        SedML file = null;
        String identifyingPath = FilenameUtils.separatorsToUnix(ExecutionJob.getRelativePath(tokenizedPath).toString());
        FileInputStream omexStream = new FileInputStream(inputFile);
        ArchiveComponents omexComponents = Libsedml.readSEDMLArchive(omexStream);
        List<SEDMLDocument> sedmlDocuments = omexComponents.getSedmlDocuments();
        for (SEDMLDocument doc : sedmlDocuments){
            SedML potentiallyCorrectFile = doc.getSedMLModel();
            if (identifyingPath.equals(potentiallyCorrectFile.getPathForURI() + potentiallyCorrectFile.getFileName())){
                file = potentiallyCorrectFile;
                break;
            }
        }
        omexStream.close();
        return file;
    }

    /**
     * In its current state, the sed-ml generated with python is missing two important fields;
     * this function fixes that.
     */
    private static SedML repairSedML(SedML brokenSedML, String[] tokenizedPath){
        Path relativePath = getRelativePath(tokenizedPath);
        if (relativePath == null) return null;
        String name = relativePath.getFileName().toString();
        brokenSedML.setFileName(name);
        // Take the relative path, remove the file name, and...
        String source = relativePath.toString().substring(0, relativePath.toString().length() - name.length());
        // Convert to unix file separators (java URI does not windows style)
        brokenSedML.setPathForURI(FilenameUtils.separatorsToUnix(source));
        return brokenSedML; // now fixed!
    }

    private static Path getRelativePath(String[] tokenizedPath){
        for (int i = 0; i < tokenizedPath.length; i++){
            if (tokenizedPath[i].startsWith(RunUtils.VCELL_TEMP_DIR_PREFIX)){
                return  Paths.get(tokenizedPath[i + 1], Arrays.copyOfRange(tokenizedPath, i + 2, tokenizedPath.length));
            }
        }
        return null;
    }

    private static boolean containsExtension(String folder, String ext) {
        GenericExtensionFilter filter = new GenericExtensionFilter(ext);
        File dir = new File(folder);
        if (dir.isDirectory() == false) {
            return false;
        }
        String[] list = dir.list(filter);
        if (list.length > 0) {
            return true;
        }
        return false;
    }

    private static boolean somethingDidFail(){
        StackTraceElement elem = new Exception().getStackTrace()[1];
        
        logger.debug(String.format("Something failed in %s @ line %d", elem.getClassName(), elem.getLineNumber()));
        return true;
    }

}
