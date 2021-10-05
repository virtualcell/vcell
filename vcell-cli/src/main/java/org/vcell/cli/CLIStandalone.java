package org.vcell.cli;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.jlibsedml.*;
import org.vcell.cli.vcml.VCMLHandler;
//import org.vcell.util.FileUtils;
import org.vcell.cli.vcml.VcmlOmexConversion;
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

    private static boolean isBatchExecution(String[] args) {
    	if(args[1] == null) {
    		return false;
    	}
    	Path path = Paths.get(args[1]);
    	boolean isDirectory = Files.isDirectory(path);
    	return isDirectory;
    }
    
    // we just make a list with the omex files that failed
    private static void writeErrorList(String[] args, String s) throws IOException {
    	if(isBatchExecution(args)) {
    		Files.write(Paths.get("errorLog.txt"), (s + "\n").getBytes(), 
    			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    	}
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
            writeErrorList(args, bioModelBaseName);
            throw new Exception(error);
        }
        // from here on, we need to collect errors, since some subtasks may succeed while other do not
        boolean somethingFailed = false;

        // python installation
        CLIUtils.checkInstallationError();

        // Generate Status YAML
        CLIUtils.generateStatusYaml(inputFile, outputDir);
        for (String sedmlLocation : sedmlLocations) {
            HashMap<String, ODESolverResultSet> resultsHash;
            HashMap<String, File> reportsHash = null;
            String sedmlName;
            File completeSedmlPath = new File(sedmlLocation);
            File outDirForCurrentSedml = new File(omexHandler.getOutputPathFromSedml(sedmlLocation));

            try {
                CLIUtils.makeDirs(outDirForCurrentSedml);

                SedML sedmlFromOmex = Libsedml.readDocument(completeSedmlPath).getSedMLModel();

                String[] sedmlNameSplit;
                if (CLIUtils.isWindowsPlatform) sedmlNameSplit = sedmlLocation.split("\\\\", -2);
                else sedmlNameSplit = sedmlLocation.split("/", -2);
                sedmlName = sedmlNameSplit[sedmlNameSplit.length - 1];

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

                System.out.println("Successful translation: SED-ML file " + sedmlName);
                CLIUtils.drawBreakLine("-", 100);

                // For appending data for SED Plot2D and Plot3d to HDF5 files following a temp convention
                CLIUtils.genSedmlForSed2DAnd3D(inputFile, outputDir);
                // SED-ML file generated by python VCell_cli_util
                sedmlPathwith2dand3d = new File(String.valueOf(sedmlPath2d3d), "simulation_" + sedmlName);
                // Converting pseudo SED-ML to biomodel
                sedmlFromPseudo = Libsedml.readDocument(sedmlPathwith2dand3d).getSedMLModel();

                /* If SED-ML has only plots as an output, we will use SED-ML that got generated from vcell_cli_util python code
                * As of now, we are going to create a resultant dataSet for Plot output, using their respective data generators */
                sedml = sedmlFromPseudo;

            } catch (Exception e) {
                System.err.println("SED-ML processing for " + sedmlLocation + " failed with error: " + e.getMessage());
                e.printStackTrace(System.err);
                somethingFailed = true;
                continue;
            }
            // Run solvers and make reports; all failures/exceptions are being caught
            SolverHandler solverHandler = new SolverHandler();
            // we send both the whole OMEX file and the extracted SEDML file path
            // XmlHelper code uses two types of resolvers to handle absolute or relative paths
            ExternalDocInfo externalDocInfo = new ExternalDocInfo(new File(inputFile), true);
            resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
            try {
            	resultsHash = solverHandler.simulateAllTasks(externalDocInfo, sedml, outDirForCurrentSedml, outputDir, sedmlLocation);
            } catch(Exception e) {
            	somethingFailed = true;
            }
            if (resultsHash.size() != 0) {
                reportsHash = CLIUtils.generateReportsAsCSV(sedml, resultsHash, outDirForCurrentSedml, outputDir, sedmlLocation);

                // HDF5 conversion
//                if ((nReportsCount==0) && (nPlots2DCount!=0 || nPlots3DCount!=0)) {
                CLIUtils.execPlotOutputSedDoc(inputFile, outputDir);
                CLIUtils.genPlotsPseudoSedml(sedmlLocation, outDirForCurrentSedml.toString());
//                }
//                else {
//                    CLIUtils.genPlots(sedmlLocation, outDirForCurrentSedml.toString());
//                    CLIUtils.convertCSVtoHDF(inputFile, outputDir);
//                }
            }

            // removing temp path generated from python
            org.apache.commons.io.FileUtils.deleteDirectory(new File(String.valueOf(sedmlPath2d3d)));

            // archiving res files
            CLIUtils.zipResFiles(new File(outputDir));

            if (resultsHash.containsValue(null) || reportsHash == null) {
                somethingFailed = true;
            }
        }
        omexHandler.deleteExtractedOmex();
        if (somethingFailed) {
            String error = "One or more errors encountered while executing archive " + args[1];
            CLIUtils.finalStatusUpdate(CLIUtils.Status.FAILED, outputDir);
            System.err.println(error);
            writeErrorList(args, bioModelBaseName);
        }
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


