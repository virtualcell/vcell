package org.vcell.cli.run;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.xml.ExternalDocInfo;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.jlibsedml.*;

import org.apache.commons.io.FilenameUtils;
import org.vcell.cli.CLIRecorder;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.run.hdf5.Hdf5DatasetWrapper;
import org.vcell.cli.run.hdf5.Hdf5Factory;
import org.vcell.cli.run.hdf5.Hdf5FileWrapper;
import org.vcell.cli.run.hdf5.Hdf5Writer;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.util.GenericExtensionFilter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SedmlJob {
    private boolean somethingFailed, hasScans, hasOverrides, bKeepTempFiles, bExactMatchOnly, bSmallMeshOverride;
    private String sedmlLocation, bioModelBaseName, resultsDirPath, logDocumentMessage, logDocumentError, sedmlName;
    private StringBuilder logOmexMessage;
    private SedmlStatistics docStatistics;
    private SedML sedml;
    private File masterOmexArchive, rootOutputDir, plotsDirectory, plotFile;
    private List<Output> outputs;
    private CLIRecorder cliRecorder;
    private File outDirForCurrentSedml;

    private final static Logger logger = LogManager.getLogger(SedmlJob.class);

    /**
     * Constructor to provide all needed info for a SedML processing job.
     * 
     * @param sedmlLocation location of the sedml document with the model to process
     * @param omexHandler object to deal with omex archive related utilities
     * @param masterOmexArchive the archive containing the sedml file
     * @param rootOutputDir the top-level directory for all the output of omex execution
     * @param resultsDirPath path to where the results should be placed
     * @param sedmlPath2d3dString path to where 2D and 3D plots are stored
     * @param cliRecorder recorder objecte used for CLI applications
     * @param bKeepTempFiles whether temp files shouldn't be deleted, or should.
     * @param bExactMatchOnly enforces a KISAO match, with no substitution
     * @param bSmallMeshOverride whether to use small meshes or standard meshes.
     * @param logOmexMessage a string-builder to contain progress updates of omex execution
     */
    public SedmlJob(String sedmlLocation, OmexHandler omexHandler, File masterOmexArchive, File rootOutputDir, String resultsDirPath, String sedmlPath2d3dString,
            CLIRecorder cliRecorder, boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bSmallMeshOverride, StringBuilder logOmexMessage){
        this.somethingFailed = false;
        this.masterOmexArchive = masterOmexArchive;
        this.sedmlLocation = sedmlLocation;
        this.outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));
        this.docStatistics = new SedmlStatistics();
        this.bioModelBaseName = FileUtils.getBaseName(masterOmexArchive.getName());
        this.rootOutputDir = rootOutputDir;
        this.resultsDirPath = resultsDirPath;
        this.logOmexMessage = logOmexMessage;
        this.logDocumentMessage = "";
        this.logDocumentError = "";
        this.plotsDirectory = new File(sedmlPath2d3dString); 
        this.plotFile = null;
        this.cliRecorder = cliRecorder;
        this.logDocumentMessage = "Initializing SED-ML document... ";
        this.logDocumentError = "";
        
    }

    /**
     * Returns a object with variables containing useful information about the sedml document
     * @return the statistics of the document
     */
    public SedmlStatistics getDocStatistics(){
        return this.docStatistics;
    }

    /**
     * Prepare the SedML model for execution
     * 
     * Called before: `simulateSedml()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     */
    public boolean preProcessDoc() throws PythonStreamException, InterruptedException, IOException {
        logger.info("Initializing SED-ML document...");

        try {
            SedML sedmlFromOmex, sedmlFromPython;
            String[] sedmlNameSplit;
            
            RunUtils.removeAndMakeDirs(outDirForCurrentSedml);
            sedmlNameSplit = sedmlLocation.split(OperatingSystemInfo.getInstance().isWindows() ? "\\\\" : "/", -2);
            sedmlFromOmex = SedmlJob.getSedMLFile(sedmlNameSplit, this.masterOmexArchive);
            this.sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
            logOmexMessage.append("Processing " + this.sedmlName + ". ");
            logger.info("Processing SED-ML: " + this.sedmlName);
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.RUNNING, this.resultsDirPath);

            this.docStatistics.setNumModels(sedmlFromOmex.getModels().size());
            for(Model m : sedmlFromOmex.getModels()) {
                List<Change> changes = m.getListOfChanges();	// change attribute caused by a math override
                if(changes != null && changes.size() > 0) {
                    hasOverrides = true;
                }
            }
            for(AbstractTask at : sedmlFromOmex.getTasks()) {
                if(at instanceof RepeatedTask) {
                    RepeatedTask rt = (RepeatedTask)at;
                    List<SetValue> changes = rt.getChanges();
                    if(changes != null && changes.size() > 0) {
                        hasScans = true;
                    }
                }
            }
            this.docStatistics.setNumTasks(sedmlFromOmex.getTasks().size());
            this.outputs = sedmlFromOmex.getOutputs();
            this.docStatistics.setNumOutputs(outputs.size());;
            for (Output output : this.outputs) {
                if (output instanceof Report) docStatistics.setReportsCount(docStatistics.getReportsCount() + 1);
                if (output instanceof Plot2D) docStatistics.setPlots2DCount(docStatistics.getPlots2DCount() + 1);
                if (output instanceof Plot3D) docStatistics.setPlots3Dcount(docStatistics.getPlots3Dcount() + 1);
            }
            this.docStatistics.setNumSimultions(sedmlFromOmex.getSimulations().size());
            String summarySedmlContentString = "Found one SED-ML document with "
                    + this.docStatistics.getNumModels() + " model(s), "
                    + this.docStatistics.getNumSimultions() + " simulation(s), "
                    + this.docStatistics.getNumTasks() + " task(s), "
                    + this.docStatistics.getReportsCount() + "  report(s),  "
                    + this.docStatistics.getPlots2DCount() + " plot2D(s), and "
                    + this.docStatistics.getPlots3Dcount() + " plot3D(s)\n";
            logger.info(summarySedmlContentString);

            logDocumentMessage += "done. ";
            String str = "Successful translation of SED-ML file";
            logDocumentMessage += str + ". ";
            logger.info(str + " : " + this.sedmlName);
            RunUtils.drawBreakLine("-", 100);

            // For appending data for SED Plot2D and Plot3d to HDF5 files following a temp convention
            logger.info("Creating pseudo SED-ML for HDF5 conversion...");
            PythonCalls.genSedmlForSed2DAnd3D(this.masterOmexArchive.getAbsolutePath(), this.resultsDirPath);
            // SED-ML file generated by python VCell_cli_util
            this.plotFile = new File(this.plotsDirectory, "simulation_" + this.sedmlName);
            Path path = Paths.get(this.plotFile.getAbsolutePath());
            if (!Files.exists(path)) {
                String message = "Failed to create file " + this.plotFile.getAbsolutePath();
                this.cliRecorder.writeDetailedResultList(bioModelBaseName + "," + this.sedmlName + "," + message);
                throw new RuntimeException(message);
            }

            // Converting pseudo SED-ML to biomodel
            logger.info("Creating Biomodel from pseudo SED-ML");
            sedmlFromPython = Libsedml.readDocument(this.plotFile).getSedMLModel();

            /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */

                // We need the name and path of the sedml file, which sedmlFromPseudo doesnt have!
            sedml = SedmlJob.repairSedML(sedmlFromPython, sedmlNameSplit);
            
        } catch (Exception e) {
            String prefix = "SED-ML processing for " + sedmlLocation + " failed with error: ";
            logDocumentError = prefix + e.getMessage();
            this.reportProblem(e);
            somethingFailed = somethingDidFail();
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, this.resultsDirPath);
            return false;
        }
        return true;
    }

    /**
     * Prepare the SedML model for execution
     * 
     * Called after: `preProcessDoc()`
     * 
     * @throws InterruptedException if there is an issue with accessing data
     * @throws PythonStreamException if calls to the python-shell instance are not working correctly
     * @throws IOException if there are system I/O issues
     * @throws ExecutionException if an execution specfic error occurs
     */
    public boolean simulateSedml() throws InterruptedException, ExecutionException, PythonStreamException, IOException {
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
        ExternalDocInfo externalDocInfo = new ExternalDocInfo(masterOmexArchive, true);

        this.runSimulations(solverHandler, externalDocInfo);
        this.recordRunDetails(solverHandler);
        this.processOutputs(solverHandler);
        return this.evalulateResults();
    }

    private void runSimulations(SolverHandler solverHandler, ExternalDocInfo externalDocInfo) throws IOException {
        /*
         * - Run solvers and make reports; all failures/exceptions are being caught
         * - we send both the whole OMEX file and the extracted SEDML file path
         * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
         */
        try {
            String str = "Building solvers and starting simulation of all tasks... ";
            logger.info(str);
            this.logDocumentMessage += str;
            solverHandler.simulateAllTasks(externalDocInfo, sedml, cliRecorder, outDirForCurrentSedml, this.resultsDirPath,
                    this.rootOutputDir.getAbsolutePath(), sedmlLocation, bKeepTempFiles, bExactMatchOnly, bSmallMeshOverride);
        } catch (Exception e) {
            somethingFailed = somethingDidFail();
            logDocumentError = e.getMessage();        // probably the hash is empty
            logger.error(e.getMessage(), e);
            // still possible to have some data in the hash, from some task that was successful - that would be partial success
        }

        this.recordRunDetails(solverHandler);
    }

    private void processOutputs(SolverHandler solverHandler) throws PythonStreamException, InterruptedException, IOException, ExecutionException {
        // WARNING!!! Current logic dictates that if any task fails we fail the sedml document
        // change implemented on Nov 11, 2021
        // Previous logic was that if at least one task produces some results we declare the sedml document status as successful
        // that will include spatial simulations for which we don't produce reports or plots!
        try {
            if (solverHandler.nonSpatialResults.containsValue(null) || solverHandler.spatialResults.containsValue(null)) {        // some tasks failed, but not all
                somethingFailed = somethingDidFail();
                logDocumentMessage += "Failed to execute one or more tasks. ";
                logger.info("Failed to execute one or more tasks in " + this.sedmlName);
            }

            logDocumentMessage += "Generating outputs... ";
            logger.info("Generating outputs... ");

            if (!solverHandler.nonSpatialResults.isEmpty()) {
                this.generateCSV(solverHandler);
                this.generatePlots();
                this.generateHDF5(solverHandler);
            }
            
            if (!solverHandler.spatialResults.isEmpty()) {
                // TODO
                // check for failures
                // generate reports from hdf5 outputs and add to non-spatial reports, if any
            }

        } catch (Exception e) {
            somethingFailed = somethingDidFail();
            logDocumentError += e.getMessage();
            this.reportProblem(e);
            org.apache.commons.io.FileUtils.deleteDirectory(this.plotsDirectory);    // removing temp path generated from python
            throw new ExecutionException();
        }
    }

    private boolean evalulateResults() throws PythonStreamException, InterruptedException, IOException {
        if (this.somethingFailed) {        // something went wrong but no exception was fired
            Exception e = new RuntimeException("Failure executing the sed document. ");
            logDocumentError += e.getMessage();
            this.reportProblem(e);
            org.apache.commons.io.FileUtils.deleteDirectory(this.plotsDirectory);    // removing temp path generated from python
            logger.warn(logDocumentError);
            return false;
        }

        // This may no longer make sense, as we could have multiple hdf5 reports, and renaming would be complicated.
        //Files.copy(new File(outDirForCurrentSedml,"reports.h5").toPath(),Paths.get(this.resultsDirPath,"reports.h5"));

        // archiving result files
        logger.info("Archiving result files");
        RunUtils.zipResFiles(new File(this.resultsDirPath));
        org.apache.commons.io.FileUtils.deleteDirectory(this.plotsDirectory);    // removing sedml dir which stages results.

        // Declare success!
        PythonCalls.setOutputMessage(sedmlLocation, this.sedmlName, this.resultsDirPath, "sedml", logDocumentMessage);
        PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.SUCCEEDED, this.resultsDirPath);
        logger.info("SED-ML : " + this.sedmlName + " successfully completed");
        return true;
    }

    private void generateCSV(SolverHandler solverHandler) throws DataAccessException, IOException {
        HashMap<String, File> csvReports = null;
        logDocumentMessage += "Generating CSV file... ";
        logger.info("Generating CSV file... ");
        
        csvReports = RunUtils.generateReportsAsCSV(sedml, solverHandler.nonSpatialResults, outDirForCurrentSedml, this.resultsDirPath, sedmlLocation);
        File[] plotFilesToRename = outDirForCurrentSedml.listFiles(f -> f.getName().startsWith("__plot__"));
        for (File plotFileToRename : plotFilesToRename){
            String newFilename = plotFileToRename.getName().replace("__plot__","");
            plotFileToRename.renameTo(new File(plotFileToRename.getParent(),newFilename));
        }
        if (csvReports == null || csvReports.isEmpty() || csvReports.containsValue(null)) {
            somethingFailed = somethingDidFail();
            String msg = "Failed to generate one or more reports. ";
            logDocumentMessage += msg;
        } else {
            logDocumentMessage += "Done. ";
        }
    }

    private void generatePlots() throws PythonStreamException, InterruptedException, IOException {
        logger.info("Generating Plots... ");
        PythonCalls.genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());    // generate the plots

        // remove CSV files associated with reports, these values are in report.h5 file anyway
        //              for (File file : csvReports.values()){
        //                  file.delete();
        //              }
    }

    private void generateHDF5(SolverHandler solverHandler) throws HDF5Exception, ExpressionException, DataAccessException, IOException {
        logDocumentMessage += "Generating HDF5 file... ";
        logger.info("Generating HDF5 file... ");

        /*
        Hdf5FileWrapper hdf5FileWrapper = new Hdf5FileWrapper();
        hdf5FileWrapper.combineArchiveLocation = outDirForCurrentSedml.getName();
        hdf5FileWrapper.uri = outDirForCurrentSedml.getName();

        List<Hdf5DatasetWrapper> nonspatialDatasets = RunUtils.prepareNonspatialHdf5(sedml, solverHandler.nonSpatialResults, solverHandler.taskToSimulationMap, sedmlLocation);
        List<Hdf5DatasetWrapper> spatialDatasets = RunUtils.prepareSpatialHdf5(sedml, solverHandler.spatialResults, solverHandler.taskToSimulationMap, sedmlLocation);
        hdf5FileWrapper.datasetWrappers.addAll(nonspatialDatasets);
        hdf5FileWrapper.datasetWrappers.addAll(spatialDatasets);

        */

        Hdf5Factory hdf5Factory = new Hdf5Factory(sedml, solverHandler.taskToSimulationMap, sedmlLocation);

        Hdf5Writer.writeHdf5(hdf5Factory.generateHdf5File(solverHandler.nonSpatialResults, solverHandler.spatialResults), outDirForCurrentSedml);

        for (File tempH5File : solverHandler.spatialResults.values()){
            if (tempH5File!=null) Files.delete(tempH5File.toPath());
        }

        if (!containsExtension(outDirForCurrentSedml.getAbsolutePath(), "h5")) {
            String errorMessage = "Failed to generate the HDF5 output file.";
            somethingFailed = somethingDidFail();
            logger.error(errorMessage);
            throw new RuntimeException(); // Get to the catch block below.
        } else {
            logDocumentMessage += "Done. ";
        }
    }

    // This method is a bit weird; it uses a temp file as a reference to compare against while getting the file straight from the archive.
    private static SedML getSedMLFile(String[] tokenizedPath, File inputFile) throws FileNotFoundException, XMLException, IOException {
        SedML file = null;
        String identifyingPath = FilenameUtils.separatorsToUnix(SedmlJob.getRelativePath(tokenizedPath).toString());
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

    private void reportProblem(Exception e) throws PythonStreamException, InterruptedException, IOException{
        logger.error(e.getMessage(), e);
        String type = e.getClass().getSimpleName();
        PythonCalls.setOutputMessage(sedmlLocation, this.sedmlName, this.resultsDirPath, "sedml", logDocumentMessage);
        PythonCalls.setExceptionMessage(sedmlLocation, this.sedmlName, this.resultsDirPath, "sedml", type, logDocumentError);
        cliRecorder.writeDetailedErrorList(bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
        PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, this.resultsDirPath);
    }

    private void recordRunDetails(SolverHandler solverHandler) throws IOException {
        String message = this.docStatistics.getNumModels() + ",";
        message += this.docStatistics.getNumSimultions() + ",";
        message += this.docStatistics.getNumTasks() + ",";
        message += this.docStatistics.getNumOutputs() + ",";
        
        message += solverHandler.countBioModels + ",";
        message += hasOverrides + ",";
        message += hasScans + ",";
        message += solverHandler.countSuccessfulSimulationRuns;
        cliRecorder.writeDetailedResultList(bioModelBaseName + "," + message);
        logger.debug(message);
    }
}
