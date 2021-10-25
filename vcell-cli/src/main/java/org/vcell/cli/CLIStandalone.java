package org.vcell.cli;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.jlibsedml.*;
import org.vcell.cli.CLIUtils.Status;
import org.vcell.cli.vcml.VCMLHandler;
//import org.vcell.util.FileUtils;
import org.vcell.cli.vcml.VcmlOmexConversion;
import org.vcell.util.GenericExtensionFilter;
import org.vcell.util.exe.Executable;

import com.lowagie.text.pdf.crypto.RuntimeCryptoException;

//import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CLIStandalone {
	
    public static void main(String[] args) {

    	if(args == null || args.length < 4) {		// -i <input> -o <output>
    		System.err.println(CLIHandler.usage);
    		System.exit(1);
    	}

        if(args[0].toLowerCase().equals("convert")) {
            // VCML to OMex conversion
        	try {
        		VcmlOmexConversion.parseArgsAndConvert(ArrayUtils.remove(args, 0));
        	} catch(IOException e) {
        		e.printStackTrace(System.err);
        	}
        }

        else {
            File input = null;

            // Arguments may not always be files, trying for other scenarios
            try {
                input = new File(args[1]);
            } catch (Exception e1) {
                // Non file or invalid argument received, let it pass, CLIHandler will handle the invalid (or non file) arguments
            }
            
            Executable.setTimeoutMS(CLIUtils.EXECUTABLE_MAX_WALLCLOK_MILLIS);

            if (input != null && input.isDirectory()) {
            	String outputDir = args[3];
            	
                FilenameFilter filter = (f, name) -> name.endsWith(".omex") || name.endsWith(".vcml");
                String[] inputFiles = input.list(filter);
                if (inputFiles == null) System.out.println("No input files found in the directory");
                assert inputFiles != null;
                for (String inputFile : inputFiles) {
                    File file = new File(input, inputFile);
                    System.out.println(file);
                    args[1] = file.toString();
                    try {
                        if (inputFile.endsWith("omex")) {
                			String bioModelBaseName = org.vcell.util.FileUtils.getBaseName(inputFile);
                			args[3] = outputDir + File.separator + bioModelBaseName;
                			Files.createDirectories(Paths.get(args[3]));
                            singleExecOmex(outputDir, args);
                        }
                        if (inputFile.endsWith("vcml")) {
                            singleExecVcml(args);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            } else {
                try {
                    if (input == null || input.toString().endsWith("omex")) {
                        singleExecOmex(args[3], args);
                    } else if (input.toString().endsWith("vcml")) {
                        singleExecVcml(args);
                    } else {
                    	throw new RuntimeException("Invalid arguments: " + Arrays.toString(args));
                    }
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    private static boolean isBatchExecution(String outputBaseDir) {
    	Path path = Paths.get(outputBaseDir);
    	boolean isDirectory = Files.isDirectory(path);
    	return isDirectory;
    }
    
    // we just make a list with the omex files that failed
    static void writeErrorList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "errorLog.txt";
    		Files.write(Paths.get(dest), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
   	}
    static void writeDetailedErrorList(String outputBaseDir, String s) throws IOException {
    	if(isBatchExecution(outputBaseDir)) {
    		String dest = outputBaseDir + File.separator + "detailedErrorLog.txt";
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


    private static void singleExecOmex(String outputBaseDir, String[] args) throws Exception {
        OmexHandler omexHandler = null;
        CLIHandler cliHandler;
        String inputFile;
        String bioModelBaseName = "";		// input file without the path
        String outputDir;
        ArrayList<String> sedmlLocations;
        int nModels;
        int nSimulations;
        int nSedml;
        int nTasks;
        List<Output> outputs;
        int nReportsCount = 0;
        int nPlots2DCount = 0;
        int nPlots3DCount = 0;
        SedML sedml;
        Path sedmlPath2d3d = null;
        File sedmlPathwith2dand3d = null;
        SedML sedmlFromPseudo = null;

        long startTimeOmex = System.currentTimeMillis();

        try {
            cliHandler = new CLIHandler(args);
            inputFile = cliHandler.getInputFilePath();
            bioModelBaseName = org.vcell.util.FileUtils.getBaseName(inputFile);
            outputDir = cliHandler.getOutputDirPath();
            sedmlPath2d3d = Paths.get(outputDir, "temp");
            System.out.println("VCell CLI input archive " + inputFile);
            CLIUtils.drawBreakLine("-", 100);
            omexHandler = new OmexHandler(inputFile, outputDir);
            omexHandler.extractOmex();
            sedmlLocations = omexHandler.getSedmlLocationsAbsolute();
            nSedml = sedmlLocations.size();
            // any error up to now is fatal (unlikely, but still...)
        } catch (Throwable exc) {
            assert omexHandler != null;
            omexHandler.deleteExtractedOmex();
            String error = exc.getMessage() + ", error for archive " + args[1];
            writeErrorList(outputBaseDir, bioModelBaseName);
            throw new Exception(error);
        }
        CLIUtils.checkInstallationError();					// check python installation
        CLIUtils.generateStatusYaml(inputFile, outputDir);	// generate Status YAML
        
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        // we now have the log file created, so that we also have a place to put them
        boolean oneSedmlDocumentSucceeded = false;
        
    	String logOmexMessage = "";
    	String logOmexError = "";		// not used for now
        for (String sedmlLocation : sedmlLocations) {		// for each sedml document

        	// this variable is set correctly but it is not used!!! because the way we define success
            boolean somethingFailed = false;		// shows that the current document suffered a partial or total failuri
            
        	String logDocumentMessage = "Initializing sedml document... ";
        	String logDocumentError = "";
        	
            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash = null;
            String sedmlName = "";
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));

            try {
                CLIUtils.makeDirs(outDirForCurrentSedml);

                SedML sedmlFromOmex = Libsedml.readDocument(completeSedmlPath).getSedMLModel();

                String[] sedmlNameSplit;
                if (CLIUtils.isWindowsPlatform) sedmlNameSplit = sedmlLocation.split("\\\\", -2);
                else sedmlNameSplit = sedmlLocation.split("/", -2);
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];
                logOmexMessage += "Processing " + sedmlName + ". ";

                nModels = sedmlFromOmex.getModels().size();
                nTasks = sedmlFromOmex.getTasks().size();
                outputs = sedmlFromOmex.getOutputs();
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
                CLIUtils.genSedmlForSed2DAnd3D(inputFile, outputDir);
                // SED-ML file generated by python VCell_cli_util
                sedmlPathwith2dand3d = new File(String.valueOf(sedmlPath2d3d), "simulation_" + sedmlName);
                Path path = Paths.get(sedmlPathwith2dand3d.getAbsolutePath());
                if(!Files.exists(path)) {
                	throw new RuntimeException("Failed to create file " + sedmlPathwith2dand3d.getAbsolutePath());
                }
                
                // Converting pseudo SED-ML to biomodel
                sedmlFromPseudo = Libsedml.readDocument(sedmlPathwith2dand3d).getSedMLModel();

                /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */
                sedml = sedmlFromPseudo;

            } catch (Exception e) {
            	String prefix = "SED-ML processing for " + sedmlLocation + " failed with error: ";
            	logDocumentError = prefix + e.getMessage();
            	String category = e.getClass().getSimpleName();
                CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", category, logDocumentError);
                writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + category + ": " + logDocumentError);
            	
                System.err.println(prefix + e.getMessage());
                e.printStackTrace(System.err);
                somethingFailed = true;
                CLIUtils.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                continue;
            }
            // Run solvers and make reports; all failures/exceptions are being caught
            SolverHandler solverHandler = new SolverHandler();
            // we send both the whole OMEX file and the extracted SEDML file path
            // XmlHelper code uses two types of resolvers to handle absolute or relative paths
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFile), true);
            resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
            try {
            	String str = "Starting simulate all tasks... ";
            	System.out.println(str);
            	logDocumentMessage += str;
            	resultsHash = solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml, outputDir, outputBaseDir, sedmlLocation);
            } catch(Exception e) {
            	somethingFailed = true;
            	logDocumentError = e.getMessage();		// probably the hash is empty
            	// still possible to have some data in the hash, from some task that was successful - that would be partial success
            }
            // resultHash contains only non-null values, so there must be at least some data in the result set
            if (resultsHash.size() != 0) {
            	logDocumentMessage += "done. ";
            	//
            	// WARNING!!! the logic here is that if at least one task produces some results we declare the sedml document status as successful
            	// that will include spatial simulations for which we don't produce reports or plots!
            	//
            	oneSedmlDocumentSucceeded = true;
            	CLIUtils.updateSedmlDocStatusYml(sedmlLocation, Status.SUCCEEDED, outputDir);
            	try {
            		if(resultsHash.containsValue(null)) {		// some tasks failed, but not all
            			// in the current implementation this cannot happen! 
            			// we don't put in the hash any null value
            			logDocumentMessage += "Failed to execute one or more tasks. ";
            		}
            		logDocumentMessage += "Generating outputs... ";
            		reportsHash = CLIUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, outputDir, sedmlLocation);
            		if(reportsHash == null || reportsHash.size() == 0) {
            			throw new RuntimeException("failed to generate any reports. ");
            		}
            		if(reportsHash.containsValue(null)) {
            			logDocumentMessage += "failed to create one or more reports. ";
            		} else {
                    	logDocumentMessage += "done. ";
            		}

            		CLIUtils.execPlotOutputSedDoc(inputFile, outputDir);							// create the HDF5 file
            		if(!containsExtension(outputDir, "h5")) {
            			throw new RuntimeException("failed to generate the .h5 output file. ");
            		}
            		CLIUtils.genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());	// generate the plots
                	org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python
            	} catch (Exception e) {
                    somethingFailed = true;
                	logDocumentError += e.getMessage();
                	String category = e.getClass().getSimpleName();
                    CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                    CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", category, logDocumentError);
                    writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + category + ": " + logDocumentError);
                    org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python
                    continue;
            	}
            } else {           	// no data in the hash -> no results to show
            	Exception e = new RuntimeException("Failure executing the tasks within the sed document. ");
            	logDocumentError += e.getMessage();
            	String category = e.getClass().getSimpleName();
                CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
                CLIUtils.setExceptionMessage(sedmlLocation, sedmlName, outputDir, "sedml", category, logDocumentError);
                writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  doc:    " + category + ": " + logDocumentError);
                CLIUtils.updateSedmlDocStatusYml(sedmlLocation, Status.FAILED, outputDir);
                org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));	// removing temp path generated from python
                continue;		// no point to create h5 or zip files with no data
            }

            // archiving res files
            CLIUtils.zipResFiles(new File(outputDir));
            CLIUtils.setOutputMessage(sedmlLocation, sedmlName, outputDir, "sedml", logDocumentMessage);
        }
        omexHandler.deleteExtractedOmex();
        
        long endTimeOmex = System.currentTimeMillis();
		long elapsedTime = endTimeOmex - startTimeOmex;
		int duration = (int)Math.ceil(elapsedTime / 1000.0);
     
        //
        // success if at least one of the documents in the omex archive is successful
        //
        if(oneSedmlDocumentSucceeded) {
        	CLIUtils.updateOmexStatusYml(CLIUtils.Status.SUCCEEDED, outputDir, duration + "");
        } else {
        	String error = "All sedml documents in this archive failed to execute";
        	CLIUtils.updateOmexStatusYml(CLIUtils.Status.FAILED, outputDir, duration + "");
        	System.err.println(error);
        	writeErrorList(outputBaseDir, bioModelBaseName);
        }
        CLIUtils.setOutputMessage("", "", outputDir, "omex", logOmexMessage);
    }

    private static void singleExecVcml(String[] args) throws Exception {
        CLIHandler cliHandler;
        String inputFile;
        String outputDir;


        try {
            cliHandler = new CLIHandler(args);
            inputFile = cliHandler.getInputFilePath();
            outputDir = cliHandler.getOutputDirPath();
            VCMLHandler.outputDir = outputDir;
            System.out.println("VCell CLI input file " + inputFile);

        } catch (Throwable exc) {
            throw new Exception(exc.getMessage());
        }
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;
        HashMap<String, ODESolverResultSet> resultsHash;
        HashMap<String, File> reportsHash = null;

        String vcmlName = inputFile.substring(inputFile.lastIndexOf(File.separator) + 1);
        File outDirForCurrentVcml = new File(Paths.get(outputDir, vcmlName).toString());

        try {
            CLIUtils.makeDirs(outDirForCurrentVcml);
        } catch (Exception e) {
            System.err.println("Error in creating required directories: " + e.getMessage());
            e.printStackTrace(System.err);
            somethingFailed = true;
        }

        // Run solvers and make reports; all failures/exceptions are being caught
        SolverHandler solverHandler = new SolverHandler();

        resultsHash = solverHandler.simulateAllVcmlTasks(new File(inputFile), outDirForCurrentVcml);


        for (String simName : resultsHash.keySet()) {
            String CSVFilePath = Paths.get(outDirForCurrentVcml.toString(), simName + ".csv").toString();
            CLIUtils.createCSVFromODEResultSet(resultsHash.get(simName), new File(CSVFilePath));
            CLIUtils.transposeVcmlCsv(CSVFilePath);
        }

        if (somethingFailed) {
            String error = "One or more errors encountered while executing VCML " + args[1];
            throw new Exception(error);
        }


    }
}


