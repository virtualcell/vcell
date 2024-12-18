package org.vcell.cli.run;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.xml.ExternalDocInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.*;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.exceptions.ExecutionException;
import org.vcell.cli.run.hdf5.HDF5ExecutionResults;
import org.vcell.cli.run.hdf5.Hdf5DataContainer;
import org.vcell.cli.run.hdf5.Hdf5DataExtractor;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.trace.Span;
import org.vcell.trace.Tracer;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class that deals with the processing quest of a sedml file.
 */
public class SedmlJob {

    private final boolean SHOULD_KEEP_TEMP_FILES,
            ACCEPT_EXACT_MATCH_ONLY, SHOULD_OVERRIDE_FOR_SMALL_MESH;
    private final String SEDML_LOCATION, BIOMODEL_BASE_NAME, RESULTS_DIRECTORY_PATH;
    private final StringBuilder LOG_OMEX_MESSAGE;
    private final SedmlStatistics DOC_STATISTICS;
    private final File MASTER_OMEX_ARCHIVE, PLOTS_DIRECTORY, OUTPUT_DIRECTORY_FOR_CURRENT_SEDML;
    private final CLIRecordable CLI_RECORDER;
    private boolean somethingFailed, hasScans, hasOverrides;
    private String logDocumentMessage, logDocumentError, sedmlName;
    private SedML sedml;
    private File plotFile;


    private final static Logger logger = LogManager.getLogger(SedmlJob.class);

    /**
     * Constructor to provide all needed info for a SedML processing job.
     * 
     * @param sedmlLocation location of the sedml document with the model to process
     * @param omexHandler object to deal with omex archive related utilities
     * @param masterOmexArchive the archive containing the sedml file
     * @param resultsDirPath path to where the results should be placed
     * @param sedmlPath2d3dString path to where 2D and 3D plots are stored
     * @param cliRecorder recorder object used for CLI applications
     * @param bKeepTempFiles whether temp files shouldn't be deleted, or should.
     * @param bExactMatchOnly enforces a KISAO match, with no substitution
     * @param bSmallMeshOverride whether to use small meshes or standard meshes.
     * @param logOmexMessage a string-builder to contain progress updates of omex execution
     */
    public SedmlJob(String sedmlLocation, OmexHandler omexHandler, File masterOmexArchive,
                    String resultsDirPath, String sedmlPath2d3dString, CLIRecordable cliRecorder,
                    boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bSmallMeshOverride,
                    StringBuilder logOmexMessage){
        this.MASTER_OMEX_ARCHIVE = masterOmexArchive;
        this.SEDML_LOCATION = sedmlLocation;
        this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
        this.DOC_STATISTICS = new SedmlStatistics();
        this.BIOMODEL_BASE_NAME = FileUtils.getBaseName(masterOmexArchive.getName());
        this.RESULTS_DIRECTORY_PATH = resultsDirPath;
        this.LOG_OMEX_MESSAGE = logOmexMessage;
        this.PLOTS_DIRECTORY = new File(sedmlPath2d3dString);
        this.CLI_RECORDER = cliRecorder;
        this.SHOULD_KEEP_TEMP_FILES = bKeepTempFiles;
        this.ACCEPT_EXACT_MATCH_ONLY = bExactMatchOnly;
        this.SHOULD_OVERRIDE_FOR_SMALL_MESH = bSmallMeshOverride;

        this.somethingFailed = false;
        this.plotFile = null;
        this.logDocumentMessage = "Initializing SED-ML document... ";
        this.logDocumentError = "";

    }

    /**
     * Returns an object with variables containing useful information about the sedml document
     * @return the statistics of the document
     */
    public SedmlStatistics getDocStatistics(){
        return this.DOC_STATISTICS;
    }

    /**
     * Prepare the SedML model for execution
     * Called before: `simulateSedml()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     */
    public boolean preProcessDoc() throws PythonStreamException, InterruptedException, IOException {
        final String SAFE_WINDOWS_FILE_SEPARATOR = "\\\\";
        final String SAFE_UNIX_FILE_SEPARATOR = "/";
        logger.info("Initializing SED-ML document...");
        BiosimulationLog biosimLog = BiosimulationLog.instance();

        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.PROCESSING_SEDML, "preProcessDoc", null);
            SedML sedmlFromOmex, sedmlFromPython;
            String[] sedmlNameSplit;
            
            RunUtils.removeAndMakeDirs(this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML);

            sedmlNameSplit = this.SEDML_LOCATION.split(OperatingSystemInfo.getInstance().isWindows() ?
                    SAFE_WINDOWS_FILE_SEPARATOR : SAFE_UNIX_FILE_SEPARATOR, -2);
            sedmlFromOmex = SedmlJob.getSedMLFile(sedmlNameSplit, this.MASTER_OMEX_ARCHIVE);
            this.sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
            this.LOG_OMEX_MESSAGE.append("Processing ").append(this.sedmlName).append(". ");
            logger.info("Processing SED-ML: " + this.sedmlName);
            biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.RUNNING);

            this.DOC_STATISTICS.setNumModels(sedmlFromOmex.getModels().size());
            for(Model m : sedmlFromOmex.getModels()) {
                List<Change> changes = m.getListOfChanges();	// change attribute caused by a math override
                if(!changes.isEmpty()) { //m.getListOfChanges will never return null(?)
                    this.hasOverrides = true;
                }
            }
            
            for(AbstractTask at : sedmlFromOmex.getTasks()) {
                if(at instanceof RepeatedTask) {
                    RepeatedTask rt = (RepeatedTask)at;
                    List<SetValue> changes = rt.getChanges();
                    if(changes != null && !changes.isEmpty()) {
                        this.hasScans = true;
                    }
                }
            }
            this.DOC_STATISTICS.setNumTasks(sedmlFromOmex.getTasks().size());
            List<Output> outputs = sedmlFromOmex.getOutputs();
            this.DOC_STATISTICS.setNumOutputs(outputs.size());
            for (Output output : outputs) {
                if (output instanceof Report) this.DOC_STATISTICS.setReportsCount(this.DOC_STATISTICS.getReportsCount() + 1);
                if (output instanceof Plot2D) this.DOC_STATISTICS.setPlots2DCount(this.DOC_STATISTICS.getPlots2DCount() + 1);
                if (output instanceof Plot3D) this.DOC_STATISTICS.setPlots3DCount(this.DOC_STATISTICS.getPlots3DCount() + 1);
            }
            this.DOC_STATISTICS.setNumSimulations(sedmlFromOmex.getSimulations().size());
            String summarySedmlContentString = "Found one SED-ML document with "
                    + this.DOC_STATISTICS.getNumModels() + " model(s), "
                    + this.DOC_STATISTICS.getNumSimulations() + " simulation(s), "
                    + this.DOC_STATISTICS.getNumTasks() + " task(s), "
                    + this.DOC_STATISTICS.getReportsCount() + "  report(s),  "
                    + this.DOC_STATISTICS.getPlots2DCount() + " plot2D(s), and "
                    + this.DOC_STATISTICS.getPlots3DCount() + " plot3D(s)\n";
            logger.info(summarySedmlContentString);

            this.logDocumentMessage += "done. ";
            String str = "Successful translation of SED-ML file";
            this.logDocumentMessage += str + ". ";
            logger.info(str + " : " + this.sedmlName);
            RunUtils.drawBreakLine("-", 100);

            // For appending data for SED Plot2D and Plot3D to HDF5 files following a temp convention
            logger.info("Creating pseudo SED-ML for HDF5 conversion...");
            this.plotFile = new File(this.PLOTS_DIRECTORY, "simulation_" + this.sedmlName);
            Path path = Paths.get(this.plotFile.getAbsolutePath());
            if (!Files.exists(path)){
                // SED-ML file generated by python VCell_cli_util
                PythonCalls.genSedmlForSed2DAnd3D(this.MASTER_OMEX_ARCHIVE.getAbsolutePath(), this.RESULTS_DIRECTORY_PATH);
            }
            if (!Files.exists(path)) {
                String message = "Failed to create plot file " + this.plotFile.getAbsolutePath();
                this.CLI_RECORDER.writeDetailedResultList(this.BIOMODEL_BASE_NAME + "," + this.sedmlName + "," + message);
                RuntimeException exception = new RuntimeException(message);
                Tracer.failure(exception, this.BIOMODEL_BASE_NAME + "," + this.sedmlName + "," + message);
                throw exception;
            }

            // Converting pseudo SED-ML to biomodel
            logger.info("Creating Biomodel from pseudo SED-ML");
            sedmlFromPython = Libsedml.readDocument(this.plotFile).getSedMLModel();

            /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */

                // We need the name and path of the sedml file, which sedmlFromPseudo doesn't have!

            this.sedml = SedmlJob.repairSedML(sedmlFromPython, sedmlNameSplit);
            
        } catch (Exception e) {
            String prefix = "SED-ML processing for " + this.SEDML_LOCATION + " failed with error: ";
            this.logDocumentError = prefix + e.getMessage();
            Tracer.failure(e, prefix);
            this.reportProblem(e);
            this.somethingFailed = somethingDidFail();
            biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.FAILED);
            return false;
        } finally {
            if (span != null) {
                span.close();
            }
        }
        return true;
    }

    /**
     * Prepare the SedML model for execution
     * Called after: `preProcessDoc()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     */
    public boolean simulateSedml(HDF5ExecutionResults masterHdf5File) throws InterruptedException, PythonStreamException, IOException {
        /*  temp code to test plot name correctness
        String idNamePlotsMap = utils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);
        utils.execPlotOutputSedDoc(inputFile, idNamePlotsMap, this.resultsDirPath);
        */

        /*
         * - Run solvers and make reports; all failures/exceptions are being caught
         * - we send both the whole OMEX file and the extracted SEDML file path
         * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
         */
        SolverHandler solverHandler = new SolverHandler();
        ExternalDocInfo externalDocInfo = new ExternalDocInfo(this.MASTER_OMEX_ARCHIVE, true);

        this.runSimulations(solverHandler, externalDocInfo);
        this.recordRunDetails(solverHandler);
        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.PROCESSING_SIMULATION_OUTPUTS, "processOutputs", null);
            this.processOutputs(solverHandler, masterHdf5File);
        } catch (Exception e){ // TODO: Make more Fine grain
            Tracer.failure(e, "Error processing outputs");
            logger.warn("Outputs could not be processed.", e);
        } finally {
            if (span != null) {
                span.close();
            }
        }
        return this.evaluateResults();
    }

    private void runSimulations(SolverHandler solverHandler, ExternalDocInfo externalDocInfo) throws IOException {
        /*
         * - Run solvers and make reports; all failures/exceptions are being caught
         * - we send both the whole OMEX file and the extracted SEDML file path
         * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
         */
        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.SIMULATIONS_RUN, "runSimulations", null);
            String str = "Building solvers and starting simulation of all tasks... ";
            logger.info(str);
            this.logDocumentMessage += str;
            solverHandler.simulateAllTasks(externalDocInfo, this.sedml, this.CLI_RECORDER,
                    this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML, this.RESULTS_DIRECTORY_PATH,
                    this.SEDML_LOCATION, this.SHOULD_KEEP_TEMP_FILES,
                    this.ACCEPT_EXACT_MATCH_ONLY, this.SHOULD_OVERRIDE_FOR_SMALL_MESH);
        } catch (Exception e) {
            Throwable currentTierOfException = e;
            StringBuilder errorMessage = new StringBuilder();
            Tracer.failure(e, errorMessage.toString());
            this.somethingFailed = somethingDidFail();
            while (currentTierOfException != null && !currentTierOfException.equals(currentTierOfException.getCause())){
                errorMessage.append(currentTierOfException.getMessage());
                currentTierOfException = currentTierOfException.getCause();
            }
            this.logDocumentError = errorMessage.toString();        // probably the hash is empty
            logger.error(errorMessage.toString(), e);
            // still possible to have some data in the hash, from some task that was successful - that would be partial success
        } finally {
            if (span != null) {
                span.close();
            }
        }

        this.recordRunDetails(solverHandler);
    }

    private void processOutputs(SolverHandler solverHandler, HDF5ExecutionResults masterHdf5File) throws InterruptedException, ExecutionException, PythonStreamException {
        // WARNING!!! Current logic dictates that if any task fails we fail the sedml document
        // change implemented on Nov 11, 2021
        // Previous logic was that if at least one task produces some results we declare the sedml document status as successful
        // that will include spatial simulations for which we don't produce reports or plots!
        try {
            if (solverHandler.nonSpatialResults.containsValue(null) || solverHandler.spatialResults.containsValue(null)) {        // some tasks failed, but not all
                this.somethingFailed = somethingDidFail();
                this.logDocumentMessage += "Failed to execute one or more tasks. ";
                Tracer.failure(new Exception("Failed to execute one or more tasks in " + this.sedmlName), "Failed to execute one or more tasks in " + this.sedmlName);
                logger.info("Failed to execute one or more tasks in " + this.sedmlName);
            }

            this.logDocumentMessage += "Generating outputs... ";
            logger.info("Generating outputs... ");

            if (!solverHandler.nonSpatialResults.isEmpty()) {
                this.generateCSV(solverHandler);
                this.generatePlots();
            }

            this.generateHDF5(solverHandler, masterHdf5File);

        } catch (Exception e) {
            this.somethingFailed = somethingDidFail();
            this.logDocumentError += e.getMessage();
            try {
                this.reportProblem(e);
                org.apache.commons.io.FileUtils.deleteDirectory(this.PLOTS_DIRECTORY);    // removing temp path generated from python
            } catch (IOException ioe){
                throw new RuntimeException(String.format("Encountered IOException while trying to delete '%s':{%s}",
                        this.PLOTS_DIRECTORY.getName(), e.getMessage()), ioe);
            }
            throw new ExecutionException("error while processing outputs: " + e.getMessage(), e);
        }
    }

    private boolean evaluateResults() throws PythonStreamException, InterruptedException, IOException {
        if (this.somethingFailed) {        // something went wrong but no exception was fired
            Exception e = new RuntimeException("Failure executing the sed document. ");
            this.logDocumentError += e.getMessage();
            try{
                this.reportProblem(e);
                org.apache.commons.io.FileUtils.deleteDirectory(this.PLOTS_DIRECTORY);    // removing temp path generated from python
            } catch (IOException ioe){
                Tracer.failure(ioe, "Deletion of " + this.PLOTS_DIRECTORY.getName() + " failed");
                logger.warn("Deletion of " + this.PLOTS_DIRECTORY.getName() + " failed", ioe);
            }
            logger.warn(this.logDocumentError);
            return false;
        }

        //Files.copy(new File(outDirForCurrentSedml,"reports.h5").toPath(),Paths.get(this.resultsDirPath,"reports.h5"));

        // archiving result files
        logger.info("Archiving result files");
        RunUtils.zipResFiles(new File(this.RESULTS_DIRECTORY_PATH));
        org.apache.commons.io.FileUtils.deleteDirectory(this.PLOTS_DIRECTORY);    // removing sedml dir which stages results.

        // Declare success!
        BiosimulationLog biosimLog = BiosimulationLog.instance();
        biosimLog.setOutputMessage(this.SEDML_LOCATION, this.sedmlName, "sedml", this.logDocumentMessage);
        biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.SUCCEEDED);
        logger.info("SED-ML : " + this.sedmlName + " successfully completed");
        return true;
    }

    private void generateCSV(SolverHandler solverHandler) throws DataAccessException, IOException {
        HashMap<String, File> csvReports;
        this.logDocumentMessage += "Generating CSV file... ";
        logger.info("Generating CSV file... ");

        // csvReports is never null (?)
        csvReports = RunUtils.generateReportsAsCSV(this.sedml, solverHandler, this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML, this.RESULTS_DIRECTORY_PATH, this.SEDML_LOCATION);
        File[] plotFilesToRename = this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML.listFiles(f -> f.getName().startsWith("__plot__"));
        plotFilesToRename = plotFilesToRename == null ? new File[0] : plotFilesToRename;
        for (File plotFileToRename : plotFilesToRename){
            String newFilename = plotFileToRename.getName().replace("__plot__","");
            if (!plotFileToRename.renameTo(new File(plotFileToRename.getParent(),newFilename))){
                logger.warn(String.format("New file name '%s' may not have been applied to '%s'", newFilename, plotFileToRename.getName()));
            }
        }
        if (csvReports.isEmpty() || csvReports.containsValue(null)) {
            this.somethingFailed = somethingDidFail();
            Tracer.failure(new Exception("Failed to generate one or more reports"), "Failed to generate one or more reports");
            String msg = "Failed to generate one or more reports. ";
            this.logDocumentMessage += msg;
        } else {
            this.logDocumentMessage += "Done. ";
        }
    }

    private void generatePlots() throws PythonStreamException, InterruptedException, IOException {
        logger.info("Generating Plots... ");
        PythonCalls.genPlotsPseudoSedml(this.SEDML_LOCATION, this.OUTPUT_DIRECTORY_FOR_CURRENT_SEDML.toString());    // generate the plots
        // We assume if no exception is returned that the plots pass
        for (Output output : this.sedml.getOutputs()){
            if (!(output instanceof Plot2D plot)) continue;
            BiosimulationLog.instance().updatePlotStatusYml(this.SEDML_LOCATION, plot.getId(), BiosimulationLog.Status.SUCCEEDED);
        }
    }

    private void generateHDF5(SolverHandler solverHandler, HDF5ExecutionResults masterHdf5File) {
        this.logDocumentMessage += "Generating HDF5 file... ";
        logger.info("Generating HDF5 file... ");

        Hdf5DataExtractor hdf5Extractor = new Hdf5DataExtractor(this.sedml, solverHandler.taskToTempSimulationMap);

        Hdf5DataContainer partialHdf5File = hdf5Extractor.extractHdf5RelevantData(solverHandler.nonSpatialResults, solverHandler.spatialResults, masterHdf5File.isBioSimHdf5);
        masterHdf5File.addResults(this.sedml, partialHdf5File);

        for (File tempH5File : solverHandler.spatialResults.values()) {
            if (tempH5File == null) continue;
            tempH5File.deleteOnExit();
            if (!SystemUtils.IS_OS_WINDOWS) continue;
            String message = "VCell can not delete intermediate file '%s' on Windows OS " +
                    "(this is due to the JHDF library suffering from JDK-4715154?).";
            logger.warn(String.format(message, tempH5File.getName()));
        }
    }

    // This method is a bit weird; it uses a temp file as a reference to compare against while getting the file straight from the archive.
    private static SedML getSedMLFile(String[] tokenizedPath, File inputFile) throws XMLException, IOException {
        SedML file = null;
        Path convertedPath = SedmlJob.getRelativePath(tokenizedPath);
        if (convertedPath == null){
            RuntimeException exception = new RuntimeException("Was not able to get relative path to " + inputFile.getName());
            logger.error(exception);
            throw exception;
        }
        String identifyingPath = FilenameUtils.separatorsToUnix(convertedPath.toString());
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

    private static Path getRelativePath(String[] tokenizedPath){
        for (int i = 0; i < tokenizedPath.length; i++){
            if (tokenizedPath[i].startsWith(RunUtils.VCELL_TEMP_DIR_PREFIX)){
                return  Paths.get(tokenizedPath[i + 1], Arrays.copyOfRange(tokenizedPath, i + 2, tokenizedPath.length));
            }
        }
        return null;
    }

    private static boolean somethingDidFail(){
        StackTraceElement elem = new Exception().getStackTrace()[1];
        
        logger.debug(String.format("Something failed in %s @ line %d", elem.getClassName(), elem.getLineNumber()));
        return true;
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
        // Convert to unix file separators (java URI does not do windows style)
        brokenSedML.setPathForURI(FilenameUtils.separatorsToUnix(source));
        return brokenSedML; // now fixed!
    }

    private void reportProblem(Exception e) throws PythonStreamException, InterruptedException, IOException{
        logger.error(e.getMessage(), e);
        String type = e.getClass().getSimpleName();
        BiosimulationLog biosimLog = BiosimulationLog.instance();
        biosimLog.setOutputMessage(this.SEDML_LOCATION, this.sedmlName, "sedml", this.logDocumentMessage);
        biosimLog.setExceptionMessage(this.SEDML_LOCATION, this.sedmlName, "sedml", type, this.logDocumentError);
        this.CLI_RECORDER.writeDetailedErrorList(e, this.BIOMODEL_BASE_NAME + ",  doc:    " + type + ": " + this.logDocumentError);
        biosimLog.updateSedmlDocStatusYml(this.SEDML_LOCATION, BiosimulationLog.Status.FAILED);
    }

    private void recordRunDetails(SolverHandler solverHandler) throws IOException {
        String message = this.DOC_STATISTICS.getNumModels() + ",";
        message += this.DOC_STATISTICS.getNumSimulations() + ",";
        message += this.DOC_STATISTICS.getNumTasks() + ",";
        message += this.DOC_STATISTICS.getNumOutputs() + ",";
        
        message += solverHandler.countBioModels + ",";
        message += this.hasOverrides + ",";
        message += this.hasScans + ",";
        message += solverHandler.countSuccessfulSimulationRuns;
        this.CLI_RECORDER.writeDetailedResultList(this.BIOMODEL_BASE_NAME + "," + message);
        logger.debug(message);
    }
}
