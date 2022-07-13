package org.vcell.cli;


import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.jlibsedml.*;
import org.vcell.cli.CLIUtils.Status;
import org.vcell.cli.vcml.VCMLHandler;

import org.vcell.cli.vcml.VcmlOmexConverter;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.exe.Executable;
import org.vcell.util.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;



public class CLIStandalone {
	
    public static void main(String[] args) throws IOException {
        // We need to immediately Initialize the CLIHandler to parse CLIArgs
        CLIHandler.instantiateCliHandler(args);
        CLIHandler cliArgs = CLIHandler.getCLIHandler();
        File inputFile = new File(cliArgs.getInputFilePath());

        Executable.setTimeoutMS(CLIUtils.EXECUTABLE_MAX_WALLCLOK_MILLIS);
        
        if (cliArgs.inConversionMode()){
            CLIStandalone.wrappedConvertFiles();
        } else if (cliArgs.isInputTypeDirectory()){
            CLIStandalone.batchMode(inputFile);
        } else {
            CLIStandalone.singleMode(inputFile);
        }

        CLIUtils.getCLIUtils().closePythonProcess(); // WARNING: Python will need reinstantiation after this is called
    }

    private static boolean isBatchExecution(String outputBaseDir) {
    	Path path = Paths.get(outputBaseDir);
    	boolean isDirectory = Files.isDirectory(path);
    	return isDirectory || CLIHandler.getCLIHandler().shouldKeepLogs();
    }
    // publications with multiple models
    public static void writeMultiModelPublications(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "multiModelPublications.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    // omex files that were fully successful
    public static void writeFullSuccessList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "fullSuccessLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    // biomodels with no simulations and biomodels with no sim results (fired when building the omex files)
    public static void writeSimErrorList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "simsErrorLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    // we just make a list with the omex files that failed
    static void writeErrorList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "errorLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    public static void writeDetailedErrorList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "detailedErrorLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    public static void writeDetailedResultList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "detailedResultLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    static void writeSpatialList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "hasSpatialLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    // when creating the omex files from vcml, we write here the list of models that have spatial, non-spatial or both applications
    public static void writeLogForOmexCreation(String outputBaseDir, Set<String> hasNonSpatialSet, Set<String> hasSpatialSet, Set<String> hasBothSet) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String s = "";
    		s += "Only Non-spatial applications\n";
    		for(String name : hasNonSpatialSet) {
    			s += (name + "\n");
    		}
    		s += "\nOnly Spatial applications\n";
    		for(String name : hasSpatialSet) {
    			s += (name + "\n");
    		}
    		s += "\nBoth Spatial and Non-Spatial applications\n";
    		for(String name : hasBothSet) {
    			s += (name + "\n");
    		}
    		String dest = outputBaseDir + File.separator + "omexCreationLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
        			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
    }
    
    private static boolean containsExtension(String folder, String ext) {
    	GenericExtensionFilter filter = new GenericExtensionFilter(ext);
    	File dir = new File(folder);
    	if(dir.isDirectory() == false) {
    		return false;
    	}
    	String[] list = dir.list(filter);
    	if (list.length > 0) {
    		return true;
    	}
    	return false;
    }


    private static void singleExecOmex(File inputFile) throws Exception {
        int nModels, nSimulations, nSedml, nTasks, nOutputs, nReportsCount = 0, nPlots2DCount = 0, nPlots3DCount = 0;
        CLIUtils utils = CLIUtils.getCLIUtils();
        CLIHandler cliHandler = CLIHandler.getCLIHandler();
        String inputFileName = inputFile.getAbsolutePath(), bioModelBaseName = FileUtils.getBaseName(inputFile.getName()), 
            outputDir = Paths.get(cliHandler.getOutputDirPath(), bioModelBaseName).toString(), outputBaseDir = cliHandler.getOutputDirPath(); // bioModelBaseName = input file without the path 
        OmexHandler omexHandler = null;
        List<String> sedmlLocations;
        List<Output> outputs;
        SedML sedml, sedmlFromPseudo;
        Path sedmlPath2d3d;
        File sedmlPathwith2dand3d;
        boolean keepTempFiles = cliHandler.isKeepTempFiles(), exactMatchOnly = cliHandler.isExactMatchOnly();

        long startTimeOmex = System.currentTimeMillis();

        System.out.println("VCell CLI input archive " + inputFileName);
        CLIUtils.drawBreakLine("-", 100);
        try {
            sedmlPath2d3d = Paths.get(outputDir, "temp");
            omexHandler = new OmexHandler(inputFileName, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            nSedml = sedmlLocations.size();
            // any error up to now is fatal (unlikely, but still...)
        } catch (Throwable exc) {
            assert omexHandler != null;
            omexHandler.deleteExtractedOmex();
            String error = exc.getMessage() + ", error for archive " + inputFileName;
            CLIStandalone.writeErrorList(outputBaseDir, bioModelBaseName);
            CLIStandalone.writeDetailedResultList(outputBaseDir, bioModelBaseName + ", " + ",unknown error with the archive file");
            throw new Exception(error);
        }
        
        CLIUtils.removeAndMakeDirs(new File(outputDir));
        utils.generateStatusYaml(inputFileName, outputDir);	// generate Status YAML
        
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        // we now have the log file created, so that we also have a place to put them
        boolean oneSedmlDocumentSucceeded = false;	// set to true if at least one sedml document run is successful
        boolean oneSedmlDocumentFailed = false;		// set to true if at least one sedml document run fails
        
    	String logOmexMessage = "";
    	String logOmexError = "";		// not used for now
        for (String sedmlLocation : sedmlLocations) {		// for each sedml document

            boolean somethingFailed = false;		// shows that the current document suffered a partial or total failure
            
        	String logDocumentMessage = "Initializing sedml document... ";
        	String logDocumentError = "";
        	
            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash = null;
            String sedmlName = "";
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));

            try {
                CLIUtils.removeAndMakeDirs(outDirForCurrentSedml);

                SedML sedmlFromOmex = Libsedml.readDocument(completeSedmlPath).getSedMLModel();

                String[] sedmlNameSplit;
                sedmlNameSplit = sedmlLocation.split(CLIUtils.isWindowsPlatform ? "\\\\" : "/", -2);
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                logOmexMessage += "Processing " + sedmlName + ". ";

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
                String summarySedmlContentString = "Found " + nSedml + " SED-ML document(s) with "
                        + nModels + " model(s), "
                        + nSimulations + " simulation(s), "
                        + nTasks + " task(s), "
                        + nReportsCount + "  report(s),  "
                        + nPlots2DCount + " plot2D(s), and "
                        + nPlots3DCount + " plot3D(s)\n";
                System.out.println(summarySedmlContentString);

                logDocumentMessage += "done. ";
                String str = "Successful translation of SED-ML file";
                logDocumentMessage += str + ". ";
                System.out.println(str + " : "+ sedmlName);
                CLIUtils.drawBreakLine("-", 100);

                // For appending data for SED Plot2D and Plot3d to HDF5 files following a temp convention
                utils.genSedmlForSed2DAnd3D(inputFileName, outputDir);
                // SED-ML file generated by python VCell_cli_util
                sedmlPathwith2dand3d = new File(String.valueOf(sedmlPath2d3d), "simulation_" + sedmlName);
                Path path = Paths.get(sedmlPathwith2dand3d.getAbsolutePath());
                if(!Files.exists(path)) {
                	String message = "Failed to create file " + sedmlPathwith2dand3d.getAbsolutePath();
                    writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + message);
                	throw new RuntimeException(message);
                }
                
                // Converting pseudo SED-ML to biomodel
                sedmlFromPseudo = Libsedml.readDocument(sedmlPathwith2dand3d).getSedMLModel();

                /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */
                sedml = sedmlFromPseudo;

            } catch (Exception e) {
            	String prefix = "SED-ML processing for " + sedmlLocation + " failed with error: ";
            	logDocumentError = prefix + e.getMessage();
            	String type = e.getClass().getSimpleName();
            	utils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            	utils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + "," + logDocumentError);
           	
                System.err.println(prefix);
                e.printStackTrace(System.err);
                somethingFailed = true;
                oneSedmlDocumentFailed = true;
                utils.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                continue;
            }
            
            
            
            {
            //
            // temp code to test plot name correctness
            //
            //    		String idNamePlotsMap = utils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);
            //    		utils.execPlotOutputSedDoc(inputFile, idNamePlotsMap, outputDir);
            }
            
            
            
            // Run solvers and make reports; all failures/exceptions are being caught
            SolverHandler solverHandler = new SolverHandler();
            // we send both the whole OMEX file and the extracted SEDML file path
            // XmlHelper code uses two types of resolvers to handle absolute or relative paths
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFileName), true);
            resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
            try {
            	String str = "Starting simulate all tasks... ";
            	System.out.println(str);
            	logDocumentMessage += str;
            	resultsHash = solverHandler.simulateAllTasks(utils, externalDocInfo, sedml, outDirForCurrentSedml, outputDir, outputBaseDir, sedmlLocation, keepTempFiles, exactMatchOnly);
            	Map<String, String> sim2Hdf5Map = solverHandler.sim2Hdf5Map;	// may not need it
            } catch(Exception e) {
            	somethingFailed = true;
            	oneSedmlDocumentFailed = true;
            	logDocumentError = e.getMessage();		// probably the hash is empty
            	// still possible to have some data in the hash, from some task that was successful - that would be partial success
            }
            
            String message  = nModels + ",";
            message += nSimulations + ",";
            message += nTasks + ",";
            message += nOutputs + ",";
            message += solverHandler.countBioModels + ",";
            message += solverHandler.countSuccessfulSimulationRuns;
            writeDetailedResultList(outputBaseDir, bioModelBaseName + "," + sedmlName + ", ," + message);

        	//
        	// WARNING!!! Current logic dictates that if any task fails we fail the sedml document
        	// change implemented on Nov 11, 2021
        	// Previous logic was that if at least one task produces some results we declare the sedml document status as successful
        	// that will include spatial simulations for which we don't produce reports or plots!
        	//
        	try {
        		if(resultsHash.containsValue(null)) {		// some tasks failed, but not all
        			oneSedmlDocumentFailed = true;
        			somethingFailed = true;
        			logDocumentMessage += "Failed to execute one or more tasks. ";
        		}
        		logDocumentMessage += "Generating outputs... ";

                reportsHash = utils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, outputDir, sedmlLocation);

        		if(reportsHash == null || reportsHash.size() == 0) {
        			oneSedmlDocumentFailed = true;
        			somethingFailed = true;
        			String msg = "Failed to generate any reports. ";
        			throw new RuntimeException(msg);
        		}
        		if(reportsHash.containsValue(null)) {
        		    oneSedmlDocumentFailed = true;
        			somethingFailed = true;
        			String msg = "Failed to generate one or more reports. ";
        			logDocumentMessage += msg;
        		} else {
                	logDocumentMessage += "Done. ";
        		}

        		logDocumentMessage += "Generating HDF5 file... ";
        		String idNamePlotsMap = utils.generateIdNamePlotsMap(sedml, outDirForCurrentSedml);
        		
                utils.execPlotOutputSedDoc(inputFileName, idNamePlotsMap, outputDir);							// create the HDF5 file

        		if(!containsExtension(outputDir, "h5")) {
        			oneSedmlDocumentFailed = true;
        			somethingFailed = true;
        			throw new RuntimeException("Failed to generate the HDF5 output file. ");
        		} else {
        			logDocumentMessage += "Done. ";
        		}

                utils.genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());	// generate the plots
    			oneSedmlDocumentSucceeded = true;
        	} catch (Exception e) {
                somethingFailed = true;
                oneSedmlDocumentFailed = true;
            	logDocumentError += e.getMessage();
            	String type = e.getClass().getSimpleName();
            	utils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            	utils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                utils.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python
                continue;
        	}

            if(somethingFailed == true) {		// something went wrong but no exception was fired
            	Exception e = new RuntimeException("Failure executing the sed document. ");
            	logDocumentError += e.getMessage();
            	String type = e.getClass().getSimpleName();
            	utils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            	utils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", type, logDocumentError);
                writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + type + ": " + logDocumentError);
                utils.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python
                continue;
            }
        	org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python

            // archiving res files
            CLIUtils.zipResFiles(new File(outputDir));
            utils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
            utils.updateSedmlDocStatusYml(sedmlLocation, Status.SUCCEEDED, outputDir);
        }

        omexHandler.deleteExtractedOmex();
        
        long endTimeOmex = System.currentTimeMillis();
		long elapsedTime = endTimeOmex - startTimeOmex;
		int duration = (int)Math.ceil(elapsedTime / 1000.0);
     
        //
        // failure if at least one of the documents in the omex archive fails
        //
        
        if(oneSedmlDocumentFailed) {
        	String error = " All sedml documents in this archive failed to execute";
        	if(oneSedmlDocumentSucceeded) {		// some succeeded, some failed
        		error = " At least one document in this archive failed to execute";
        	}
        	utils.updateOmexStatusYml(CLIUtils.Status.FAILED, outputDir, duration + "");
        	System.err.println(error);
        	logOmexMessage += error;
        	writeErrorList(outputBaseDir, bioModelBaseName);
        } else {
        	utils.updateOmexStatusYml(CLIUtils.Status.SUCCEEDED, outputDir, duration + "");
        	writeFullSuccessList(outputBaseDir, bioModelBaseName);
        	logOmexMessage += " Done";
        }
        utils.setOutputMessage("null", "null", outputDir, "omex", logOmexMessage);
        
    }

    @Deprecated
    private static void singleExecVcml(File vcmlFile)  {
        CLIHandler cliHandler = CLIHandler.getCLIHandler();
        String inputFile = cliHandler.getInputFilePath();
        String outputDir = cliHandler.getOutputDirPath();

        VCMLHandler.outputDir = outputDir;
        System.out.println("VCell CLI input file " + inputFile);

        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        HashMap<String, ODESolverResultSet> resultsHash;

        String vcmlName = inputFile.substring(inputFile.lastIndexOf(File.separator) + 1);
        File outDirForCurrentVcml = new File(Paths.get(outputDir, vcmlName).toString());

        try {
            CLIUtils.removeAndMakeDirs(outDirForCurrentVcml);
        } catch (Exception e) {
            System.err.println("Error in creating required directories: " + e.getMessage());
            e.printStackTrace(System.err);
            somethingFailed = true;
        }

        // Run solvers and make reports; all failures/exceptions are being caught
        SolverHandler solverHandler = new SolverHandler();
        try {
            resultsHash = solverHandler.simulateAllVcmlTasks(new File(inputFile), outDirForCurrentVcml);

            for (String simName : resultsHash.keySet()) {
                String CSVFilePath = Paths.get(outDirForCurrentVcml.toString(), simName + ".csv").toString();
                CLIUtils.createCSVFromODEResultSet(resultsHash.get(simName), new File(CSVFilePath));
                CLIUtils.getCLIUtils().transposeVcmlCsv(CSVFilePath);
            }
        } catch(IOException e){
            System.err.println("IOException while processing VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (ExpressionException e){
            System.err.println("InterruptedException while creative results CSV from VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (InterruptedException e){
            System.err.println("InterruptedException while transposing CSV from VCML " + vcmlFile.getName());
            e.printStackTrace(System.err);
            somethingFailed = true;
        } catch (Exception e){
            System.err.println("Unexpected exception while transposing CSV from VCML " + vcmlFile.getName());
            System.err.println(e);
            e.printStackTrace(System.err);
            somethingFailed = true;
        }

        if (somethingFailed) {
            try {
                throw new Exception("One or more errors encountered while executing VCML " + vcmlFile.getName());
            } catch (Exception e){
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }

    static void batchMode(File dirOfArchivesToProcess){
        CLIHandler handler = CLIHandler.getCLIHandler();
        FilenameFilter filter = (f, name) -> name.endsWith(".omex") || name.endsWith(".vcml");
        File[] inputFiles = dirOfArchivesToProcess.listFiles(filter);
        if (inputFiles == null) System.out.println("No input files found in the directory");
        assert inputFiles != null;

        CLIStandalone.createHeader();

        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            System.out.println(inputFile);
            try {
                if (inputFileName.endsWith("omex")) {
                    String bioModelBaseName = inputFileName.substring(0, inputFileName.indexOf(".")); // ".omex"??
                    Files.createDirectories(Paths.get(handler.getOutputDirPath() + File.separator + bioModelBaseName)); // make output subdir
                    CLIStandalone.singleExecOmex(inputFile);
                }
                
                if (inputFileName.endsWith("vcml")) {
                    CLIStandalone.singleExecVcml(inputFile);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    static void singleMode(File archiveToProcess){
        if (archiveToProcess.getName().endsWith("vcml")){
            CLIStandalone.singleExecVcml(archiveToProcess);
        } else { // archiveToProcess.getName().endsWith("omex")
            CLIStandalone.wrappedExecSingleOmex(archiveToProcess);
        }
    }

    // TODO: Fix singleExecOmex execption system (maybe also singleExecVcml) to not require this wrapper for readibility and understanding.
    private static void wrappedExecSingleOmex(File archiveToProcess){
        try{
            CLIStandalone.singleExecOmex(archiveToProcess);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void wrappedConvertFiles(){
        try {
            VcmlOmexConverter.convertFiles();
        } catch (IOException e){
            e.printStackTrace(System.err);
        }
    }

    private static void createHeader(){
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
        
        CLIHandler handler = CLIHandler.getCLIHandler();
        String header = "BaseName,SedML,Error,Models,Sims,Tasks,Outputs,BioModels,NumSimsSuccessful";
        try {
            CLIStandalone.writeDetailedResultList(handler.getOutputDirPath(), header);
        } catch (IOException e1) {
            // not big deal, we just failed to make the header; we'll find out later what went wrong
            e1.printStackTrace();
        }
    }
}


