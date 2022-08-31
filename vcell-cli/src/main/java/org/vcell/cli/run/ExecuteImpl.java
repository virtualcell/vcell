package org.vcell.cli.run;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;

import org.jlibsedml.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.CLILocalLogFileManager;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.util.FileUtils;
import org.vcell.util.GenericExtensionFilter;

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
import java.util.LinkedHashMap;
import java.util.List;

public class ExecuteImpl {
    
    private final static Logger logger = LogManager.getLogger(ExecuteImpl.class);

    public static void batchMode(File dirOfArchivesToProcess, File outputDir, CLILocalLogFileManager logManager,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bForceLogFiles) throws IOException {
        FilenameFilter filter = (f, name) -> name.endsWith(".omex") || name.endsWith(".vcml");
        File[] inputFiles = dirOfArchivesToProcess.listFiles(filter);
        if (inputFiles == null) throw new IOException("Error trying to retrieve files from input directory.");

        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            logger.info("Processing " + inputFileName + "(" + inputFile + ")");
            try {
                if (inputFileName.endsWith("omex")) {
                    String bioModelBaseName = inputFileName.substring(0, inputFileName.indexOf(".")); // ".omex"??
                    Files.createDirectories(Paths.get(outputDir.getAbsolutePath() + File.separator + bioModelBaseName)); // make output subdir
                    singleExecOmex(inputFile, outputDir, logManager, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                }

                if (inputFileName.endsWith("vcml")) {
                    singleExecVcml(inputFile, outputDir, logManager);
                }
            } catch (Exception e) {
                logger.error("Error caught executing batch mode", e);
            }
        }
    }

    @Deprecated
    public static void singleExecVcml(File vcmlFile, File outputDir, CLILocalLogFileManager logManager) {

        VCMLHandler.outputDir = outputDir.getAbsolutePath();
        logger.debug("Executing VCML file " + vcmlFile);

        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        HashMap<String, ODESolverResultSet> resultsHash;

        String vcmlName = vcmlFile.getAbsolutePath().substring(vcmlFile.getAbsolutePath().lastIndexOf(File.separator) + 1);
        File outDirForCurrentVcml = new File(Paths.get(outputDir.getAbsolutePath(), vcmlName).toString());

        try {
            RunUtils.removeAndMakeDirs(outDirForCurrentVcml);
        } catch (Exception e) {
            logger.error("Error in creating required directories: " + e.getMessage(), e);
            somethingFailed = true;
        }

        // Run solvers and make reports; all failures/exceptions are being caught
        SolverHandler solverHandler = new SolverHandler();
        try {
            resultsHash = solverHandler.simulateAllVcmlTasks(vcmlFile, outDirForCurrentVcml);

            for (String simName : resultsHash.keySet()) {
                String CSVFilePath = Paths.get(outDirForCurrentVcml.toString(), simName + ".csv").toString();
                RunUtils.createCSVFromODEResultSet(resultsHash.get(simName), new File(CSVFilePath));
                PythonCalls.transposeVcmlCsv(CSVFilePath);
            }
        } catch (IOException e) {
            logger.error("IOException while processing VCML " + vcmlFile.getName(), e);
            somethingFailed = true;
        } catch (ExpressionException e) {
            logger.error("InterruptedException while creating results CSV from VCML " + vcmlFile.getName(), e);
            somethingFailed = true;
        } catch (InterruptedException e) {
            logger.error("InterruptedException while transposing CSV from VCML " + vcmlFile.getName(), e);
            somethingFailed = true;
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected exception while transposing CSV from VCML <%s>\n%s", vcmlFile.getName(), e.toString());
            logger.error(errorMessage, e);
            somethingFailed = true;
        }

        if (somethingFailed) {
            RuntimeException e = new RuntimeException("One or more errors encountered while executing VCML " + vcmlFile.getName());
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    public static void singleExecOmex(File inputFile, File rootOutputDir, CLILocalLogFileManager logManager, 
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bForceLogFiles) throws Exception {
        ExecuteImpl.singleExecOmex(inputFile, rootOutputDir, logManager, bKeepTempFiles, bExactMatchOnly, bForceLogFiles, true);
    }

    public static void singleExecOmex(File inputFile, File rootOutputDir, CLILocalLogFileManager logManager,
            boolean bKeepTempFiles, boolean bExactMatchOnly, boolean bForceLogFiles, boolean bEncapsulateOutput) throws Exception {
        int nModels, nSimulations, nTasks, nOutputs, nReportsCount = 0, nPlots2DCount = 0, nPlots3DCount = 0;
        String logOmexMessage = "";

        String inputFilePath = inputFile.getAbsolutePath();
        String bioModelBaseName = FileUtils.getBaseName(inputFile.getName());
        String outputBaseDir = rootOutputDir.getAbsolutePath(); // bioModelBaseName = input file without the path
        String outputDir = bEncapsulateOutput ? Paths.get(outputBaseDir, bioModelBaseName).toString() : outputBaseDir;
        boolean anySedmlDocumentHasSucceeded = false;    // set to true if at least one sedml document run is successful
        boolean anySedmlDocumentHasFailed = false;        // set to true if at least one sedml document run fails
        
        OmexHandler omexHandler = null;
        List<String> sedmlLocations;
        List<Output> outputs;
        SedML sedml;
        Path sedmlPath2d3d;
        File sedmlPathwith2dand3d;

        long startTimeOmex = System.currentTimeMillis();

        logger.info("Executing OMEX archive " + inputFilePath);
        RunUtils.drawBreakLine("-", 100);
        try { // It's unlikely, but if we get errors here they're fatal.
            sedmlPath2d3d = Paths.get(outputDir, "temp");
            omexHandler = new OmexHandler(inputFilePath, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
        } catch (IOException e){
            String error = e.getMessage() + ", error for OmexHandler with " + inputFilePath;
            logManager.writeErrorList(bioModelBaseName);
            //CLIUtils.writeErrorList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            logManager.writeDetailedResultList(bioModelBaseName + ", " + ", IO error with OmexHandler");
            //CLIUtils.writeDetailedResultList(outputBaseDir, bioModelBaseName + ", " + ", IO error with OmexHandler", bForceLogFiles);
            logger.error(error);
            throw new RuntimeException(error, e);
        } catch (Exception e) { 
            omexHandler.deleteExtractedOmex();
            String error = e.getMessage() + ", error for archive " + inputFilePath;
            //CLIUtils.writeErrorList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            logManager.writeErrorList(bioModelBaseName);
            //CLIUtils.writeDetailedResultList(outputBaseDir, bioModelBaseName + ", " + ",unknown error with the archive file", bForceLogFiles);
            logManager.writeDetailedResultList(bioModelBaseName + ", " + ",unknown error with the archive file");
            logger.error(error);
            throw new RuntimeException(error, e);
        } 

        logger.info("Preparing output directory...");
        CLIUtils.cleanRootDir(new File(outputBaseDir));
        if (bEncapsulateOutput) RunUtils.removeAndMakeDirs(new File(outputDir));
        PythonCalls.generateStatusYaml(inputFilePath, outputDir);    // generate Status YAML

        /*
         * from here on, we need to collect errors, since some subtasks may succeed while other do not
         * we now have the log file created, so that we also have a place to put them
         */
        for (String sedmlLocation : sedmlLocations) {
            logger.info("Initializing SED-ML document...");
            String sedmlName = "", logDocumentMessage = "Initializing SED-ML document... ", logDocumentError = "";
            boolean somethingFailed = false; // shows that the current document suffered a partial or total failure
            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash = null;
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));

            try {
                SedML sedmlFromOmex, sedmlFromPython;
                String[] sedmlNameSplit;

                
                RunUtils.removeAndMakeDirs(outDirForCurrentSedml);
                sedmlNameSplit = sedmlLocation.split(OperatingSystemInfo.getInstance().isWindows() ? "\\\\" : "/", -2);
                //sedmlFromOmex = Libsedml.readDocument(new File(sedmlLocation)).getSedMLModel();
                sedmlFromOmex = ExecuteImpl.getSedMLFile(sedmlNameSplit, inputFile);
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                logOmexMessage += "Processing " + sedmlName + ". ";
                logger.info("Processing SED-ML: " + sedmlName);

                nModels = sedmlFromOmex.getModels().size();
                nTasks = sedmlFromOmex.getTasks().size();
                outputs = sedmlFromOmex.getOutputs();
                nOutputs = outputs.size();
                for (Output output : outputs) {
                    if (output instanceof Report) nReportsCount++;
                    if (output instanceof Plot2D) nPlots2DCount++;
                    if (output instanceof Plot3D) nPlots3DCount++;
                }
                nSimulations = sedmlFromOmex.getSimulations().size();
                String summarySedmlContentString = "Found " + sedmlLocations.size() + " SED-ML document(s) with "
                        + nModels + " model(s), "
                        + nSimulations + " simulation(s), "
                        + nTasks + " task(s), "
                        + nReportsCount + "  report(s),  "
                        + nPlots2DCount + " plot2D(s), and "
                        + nPlots3DCount + " plot3D(s)\n";
                logger.debug(summarySedmlContentString);

                logDocumentMessage += "done. ";
                String str = "Successful translation of SED-ML file";
                logDocumentMessage += str + ". ";
                logger.info(str + " : " + sedmlName);
                RunUtils.drawBreakLine("-", 100);

                // For appending data for SED Plot2D and Plot3d to HDF5 files following a temp convention
                logger.info("Creating pseudo SED-ML for HDF5 conversion...");
                PythonCalls.genSedmlForSed2DAnd3D(inputFilePath, outputDir);
                // SED-ML file generated by python VCell_cli_util
                sedmlPathwith2dand3d = new File(String.valueOf(sedmlPath2d3d), "simulation_" + sedmlName);
                Path path = Paths.get(sedmlPathwith2dand3d.getAbsolutePath());
                if (!Files.exists(path)) {
                    String message = "Failed to create file " + sedmlPathwith2dand3d.getAbsolutePath();
                    //CLIUtils.writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + message, bForceLogFiles);
                    logManager.writeDetailedResultList(bioModelBaseName + "," + sedmlName + "," + message);
                    throw new RuntimeException(message);
                }

                // Converting pseudo SED-ML to biomodel
                logger.info("Creating Biomodel from pseudo SED-ML");
                sedmlFromPython = Libsedml.readDocument(sedmlPathwith2dand3d).getSedMLModel();

                /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                 * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */
                sedml = ExecuteImpl.repairSedML(sedmlFromPython, sedmlNameSplit);
                // We need the name and path of the sedml file, which sedmlFromPseudo doesnt have!
                //sedml = this.

            } catch (Exception e) {
                String prefix = "SED-ML processing for " + sedmlLocation + " failed with error: ";
                logDocumentError = prefix + e.getMessage();
                String type = e.getClass().getSimpleName();
                PythonCalls.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                PythonCalls.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                //CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                logManager.writeDetailedErrorList(bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                //CLIUtils.writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + logDocumentError, bForceLogFiles);
                logManager.writeDetailedResultList(bioModelBaseName + "," + sedmlName + "," + logDocumentError);

                logger.error(prefix, e);
                somethingFailed = true;
                anySedmlDocumentHasFailed = true;
                PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                continue; // Next document
            }

            /*  temp code to test plot name correctness
            String idNamePlotsMap = utils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);
            utils.execPlotOutputSedDoc(inputFile, idNamePlotsMap, outputDir);
            */

            /*
             * - Run solvers and make reports; all failures/exceptions are being caught
             * - we send both the whole OMEX file and the extracted SEDML file path
             * - XmlHelper code uses two types of resolvers to handle absolute or relative paths
             */
            SolverHandler solverHandler = new SolverHandler();
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFilePath), true);
            resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
            try {
                String str = "Building solvers and starting simulation of all tasks... ";
                logger.info(str);
                logDocumentMessage += str;
                resultsHash = solverHandler.simulateAllTasks(externalDocInfo, sedml, logManager, outDirForCurrentSedml, outputDir,
                        outputBaseDir, sedmlLocation, bKeepTempFiles, bExactMatchOnly, bForceLogFiles);
                //Map<String, String> sim2Hdf5Map = solverHandler.sim2Hdf5Map;    // may not need it
            } catch (Exception e) {
                somethingFailed = true;
                anySedmlDocumentHasFailed = true;
                logDocumentError = e.getMessage();        // probably the hash is empty
                logger.error(e.getMessage(), e);
                // still possible to have some data in the hash, from some task that was successful - that would be partial success
            }

            String message = nModels + ",";
            message += nSimulations + ",";
            message += nTasks + ",";
            message += nOutputs + ",";
            message += solverHandler.countBioModels + ",";
            message += solverHandler.countSuccessfulSimulationRuns;
            //CLIUtils.writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + ", ," + message, bForceLogFiles);
            logManager.writeDetailedResultList(bioModelBaseName + "," + sedmlName + ", ," + message);
            logger.debug(message);

            //
            // WARNING!!! Current logic dictates that if any task fails we fail the sedml document
            // change implemented on Nov 11, 2021
            // Previous logic was that if at least one task produces some results we declare the sedml document status as successful
            // that will include spatial simulations for which we don't produce reports or plots!
            //
            try {
                if (resultsHash.containsValue(null)) {        // some tasks failed, but not all
                    anySedmlDocumentHasFailed = true;
                    somethingFailed = true;
                    logDocumentMessage += "Failed to execute one or more tasks. ";
                    logger.info("Failed to execute one or more tasks in " + sedmlName);
                }
                logDocumentMessage += "Generating outputs... ";
                logger.info("Generating outputs... ");
                reportsHash = RunUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, outputDir, sedmlLocation);

                if (reportsHash == null || reportsHash.size() == 0) {
                    anySedmlDocumentHasFailed = true;
                    somethingFailed = true;
                    String msg = "Failed to generate any reports. ";
                    throw new RuntimeException(msg);
                }
                if (reportsHash.containsValue(null)) {
                    anySedmlDocumentHasFailed = true;
                    somethingFailed = true;
                    String msg = "Failed to generate one or more reports. ";
                    logDocumentMessage += msg;
                } else {
                    logDocumentMessage += "Done. ";
                }

                logDocumentMessage += "Generating HDF5 file... ";
                logger.info("Generating HDF5 file... ");
                String idNamePlotsMap = RunUtils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);

                PythonCalls.execPlotOutputSedDoc(inputFilePath, idNamePlotsMap, outputDir);                            // create the HDF5 file

                if (!containsExtension(outputDir, "h5")) {
                    anySedmlDocumentHasFailed = true;
                    somethingFailed = true;
                    throw new RuntimeException("Failed to generate the HDF5 output file. ");
                } else {
                    logDocumentMessage += "Done. ";
                }

                logger.info("Generating Plots... ");
                PythonCalls.genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());    // generate the plots
                anySedmlDocumentHasSucceeded = true;
            } catch (Exception e) {
            	logger.error(e.getMessage(), e);
                somethingFailed = true;
                anySedmlDocumentHasFailed = true;
                logDocumentError += e.getMessage();
                String type = e.getClass().getSimpleName();
                PythonCalls.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                PythonCalls.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                //CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                logManager.writeDetailedErrorList(bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python
                continue;
            }

            if (somethingFailed) {        // something went wrong but no exception was fired
                Exception e = new RuntimeException("Failure executing the sed document. ");
                logDocumentError += e.getMessage();
                logger.error(e.getMessage(), e);
                String type = e.getClass().getSimpleName();
                PythonCalls.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                PythonCalls.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                //CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError, bForceLogFiles);
                logManager.writeDetailedErrorList(bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python
                continue;
            }
            org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));    // removing temp path generated from python

            // archiving res files
            logger.info("Archiving result files");
            RunUtils.zipResFiles(new File(outputDir));
            PythonCalls.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            PythonCalls.updateSedmlDocStatusYml(sedmlLocation, Status.SUCCEEDED, outputDir);
            logger.info("SED-ML : " + sedmlName + " successfully completed");
        }

        omexHandler.deleteExtractedOmex();

        long endTimeOmex = System.currentTimeMillis();
        long elapsedTime = endTimeOmex - startTimeOmex;
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
            logManager.writeErrorList(bioModelBaseName);
        } else {
            PythonCalls.updateOmexStatusYml(Status.SUCCEEDED, outputDir, duration + "");
            //CLIUtils.writeFullSuccessList(outputBaseDir, bioModelBaseName, bForceLogFiles);
            logManager.writeFullSuccessList(bioModelBaseName);
            logOmexMessage += " Done";
        }
        PythonCalls.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage);

    }

    // This method is a bit weird; it uses a temp file as a reference to compare against while getting the file straight from the archive.
    private static SedML getSedMLFile(String[] tokenizedPath, File inputFile) throws FileNotFoundException, XMLException, IOException {
        SedML file = null;
        String identifyingPath = FilenameUtils.separatorsToUnix(ExecuteImpl.getRelativePath(tokenizedPath).toString());
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
}
