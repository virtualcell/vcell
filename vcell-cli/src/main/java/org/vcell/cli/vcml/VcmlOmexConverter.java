package org.vcell.cli.vcml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.db.FieldDataDBOperationDriver;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.Variable;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.parser.Expression;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.util.NativeLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.sbml.libcombine.CombineArchive;
import org.sbml.libcombine.KnownFormats;
import org.vcell.cli.CLIHandler;
import org.vcell.cli.CLIStandalone;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCInfoContainer;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class VcmlOmexConverter {

    private static ConnectionFactory conFactory;
	private static AdminDBTopLevel adminDbTopLevel;
	private static Map <String, BioModelInfo> bioModelInfoMap = new LinkedHashMap<>();
	
	private static boolean bForceVCML = false;		// set by the -vcml CL argument, means we export to omex as vcml (if missing, default we try sbml first)
	private static boolean bHasDataOnly = false;	// we only export those simulations that have at least some results; set by -hasDataOnly CL argument
	private static boolean bMakeLogsOnly = false;	// we do not build omex files, we just write the logs
	private static CLIHandler cliHandler;

	public static void parseArgsAndConvert(String[] args) throws IOException {
        File input = null;
        try {
            // TODO: handle if it's not valid PATH
            input = new File(args[1]);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        args = parseArgs(args);
    	 
//    	if (bHasDataOnly) {
		try {
			conFactory = DatabaseService.getInstance().createConnectionFactory();
			adminDbTopLevel = new AdminDBTopLevel(conFactory);
		} catch (SQLException e) {
			System.err.println("\n\n\n=====>>>>EXPORT FAILED: connection to database failed");
			e.printStackTrace();
		}
//    	}
		
		VCInfoContainer vcic;
		try {
			vcic = adminDbTopLevel.getPublicOracleVCInfoContainer(false);
			if(vcic != null) {
				User user = vcic.getUser();
				BioModelInfo[] bioModelInfos = vcic.getBioModelInfos();
//				GeometryInfo[] geometryInfos = vcic.getGeometryInfos();
//				MathModelInfo[] mathModelInfos = vcic.getMathModelInfos();
//				int count = 0;
				for(BioModelInfo bi : bioModelInfos) {
//					if(bi.getPublicationInfos() != null && bi.getPublicationInfos().length > 0) {
//						// let's see what has PublicationInfo
//						System.out.println(bi.getVersion().getName());
//						count++;
//					}
					
					// build the biomodel id / biomodel info map
					String biomodelId1 = "biomodel-" + bi.getVersion().getVersionKey();
					String biomodelId2 = "biomodel-" + bi.getModelKey();
					bioModelInfoMap.put(biomodelId1, bi);
				}
//				System.out.println("User: " + user.getName() + "   count published biomodels: " + count);
			}
		} catch (SQLException | DataAccessException e1) {
			System.err.println("\n\n\n=====>>>>EXPORT FAILED: failed to retrieve metadata");
			e1.printStackTrace();
		}
		System.out.println("Found " + bioModelInfoMap.size() + "BioNodelInfo objects");


        if (input != null && input.isDirectory()) {
            FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
            String[] inputFiles = input.list(filterVcmlFiles);		// jusr a list of vcml names, like biomodel-185577495.vcml, ...
            if (inputFiles == null) {
                System.err.println("No VCML files found in the directory `" + input + "`");
            }
            String outputDir = args[3];			// full directory name, like C:\TEMP\biomodel\omex\native
            
            CLIStandalone.writeSimErrorList(outputDir, "hasDataOnly is " + bHasDataOnly);
            CLIStandalone.writeSimErrorList(outputDir, "makeLogsOnly is " + bMakeLogsOnly);
            
//            assert inputFiles != null;
            for (String inputFile : inputFiles) {
                File file = new File(input, inputFile);
                System.out.println(" ============== start: " + inputFile);
                args[1] = file.toString();
                cliHandler = new CLIHandler(args);
                try {
                    if (inputFile.endsWith(".vcml")) {
                        boolean isCreated = vcmlToOmexConversion(outputDir);
                        if (isCreated) {
                        	System.out.println("Combine archive created for file(s) `" + input + "`");
                        }
                        else {
                        	System.err.println("Failed converting VCML to OMEX archive for `" + input + "`");
                        }
                    } else {
                    	System.err.println("No VCML files found in the directory `" + input + "`");
                    }
                } catch (Exception e) {
//					e.printStackTrace(System.err);
                    
                	System.out.println("\n\n\n=====>>>>EXPORT FAILED: " +inputFile+"\n\n\n");
                	CLIStandalone.writeDetailedErrorList(outputDir, inputFile + ",   " + e.getMessage());

                    //                   System.exit(1);
                }
            }
        } else {
            try {
                assert input != null;
                if (input.toString().endsWith(".vcml")) {
                    cliHandler = new CLIHandler(args);
                    boolean isCreated = vcmlToOmexConversion(null);
                    if (isCreated) System.out.println("Combine archive created for `" + input + "`");
                    else System.err.println("Failed converting VCML to OMEX archive for `" + input + "`");
                } else System.err.println("No input files found in the directory `" + input + "`");
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("\n\n\n=====>>>>EXPORT FAILED: " +input+"\n\n\n");   
//                System.exit(1);
            }
        }
    }

    public static boolean vcmlToOmexConversion(String outputBaseDir) throws XmlParseException, IOException, DataAccessException, SQLException {

        // Get VCML file path from -i flag
		int sedmlLevel = 1;
		int sedmlVersion = 2;
        
        String inputVcmlFile = cliHandler.getInputFilePath();

        // Get directory file path from -o flag
        String outputDir = cliHandler.getOutputDirPath();

        // get VCML name from VCML path
        //String vcmlName = inputVcmlFile.split(File.separator, 10)[inputVcmlFile.split(File.separator, 10).length - 1].split("\\.", 5)[0];
        String vcmlName = FilenameUtils.getBaseName(inputVcmlFile);		// platform independent, strips extension too
        
        // recover the bioModelInfo
        BioModelInfo bioModelInfo = bioModelInfoMap.get(vcmlName);

        File vcmlFilePath = new File(inputVcmlFile);
        // Create biomodel
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));
        bioModel.refreshDependencies();
        
        int numSimulations = bioModel.getNumSimulations();
        if(outputBaseDir != null && numSimulations == 0) {
        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " has no simulations.");
        	return false;
        }
        
        // we extract the simulations with field data from the list of simulations since they are not supported
        List<Simulation> simulations = new ArrayList<> ();
        simulations.add((Simulation) Arrays.asList(bioModel.getSimulations()));
		for (Simulation simulation : simulations) {
	        boolean bFieldDataFound = false;
			Enumeration<Variable> variables = simulation.getMathDescription().getVariables();
			while(variables.hasMoreElements()) {
				Variable var = variables.nextElement();
				Expression exp = var.getExpression();
				FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments( exp);
				
				if(ffas != null && ffas.length > 0) {
					bFieldDataFound = true;
					break;	// we are done with this simulation if we know it has at least one variable with a field data in its expression
				}
			}
			if(bFieldDataFound) {
				try {
					bioModel.removeSimulation(simulation);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
	        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " excluded: FieldData not supported at this time.");
				SolverDescription solverDescription = simulation.getSolverTaskDescription().getSolverDescription();
				String solverName = solverDescription.getShortDisplayLabel();
				CLIStandalone.writeSimErrorList(outputBaseDir, "   " + solverName);
			}
		}
        
        
        // NOTE: SEDML exporter exports both SEDML as well as required SBML
        List<Simulation> simsToExport =null;
        Set<String> solverNames = new LinkedHashSet<>();
        if (bHasDataOnly) {
        	// make list of simulations to export with only sims that have data on the server
        	simsToExport = new ArrayList<Simulation>();
			for (Simulation simulation : bioModel.getSimulations()) {
				SolverDescription solverDescription = simulation.getSolverTaskDescription().getSolverDescription();
				String solverName = solverDescription.getShortDisplayLabel();
				solverNames.add(solverName);
				// check server status
				KeyValue parentKey = simulation.getSimulationVersion().getParentSimulationReference();
				SimulationJobStatusPersistent[] statuses = adminDbTopLevel.getSimulationJobStatusArray(parentKey == null ? simulation.getKey() : parentKey, false);
				if (statuses == null) continue;
				if (statuses.length == 0) continue;
				for (int i = 0; i < statuses.length; i++) {
					if (statuses[i].hasData()) {
						simsToExport.add(simulation);
						continue;
					}
				}
				
//				String dbDriverName = PropertyLoader.getProperty(PropertyLoader.dbDriverName, null);
//				String dbConnectURL = PropertyLoader.getProperty(PropertyLoader.dbConnectURL, null);
//				String dbSchemaUser = PropertyLoader.getProperty(PropertyLoader.dbUserid, null);
//				String dbPassword = PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue, PropertyLoader.dbPasswordFile);
//		        DataSetControllerImpl dsControllerImpl = new DataSetControllerImpl(null, new File(outputBaseDir), null);
//				
//				code used to recover field data
//				
//				HashMap<User, Vector<ExternalDataIdentifier>> allExternalDataIdentifiers = FieldDataDBOperationDriver.getAllExternalDataIdentifiers();
//				Enumeration<Variable> variables = simulation.getMathDescription().getVariables();
//				while(variables.hasMoreElements()) {
//					Variable var = variables.nextElement();
//					Expression exp = var.getExpression();
//					FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments( exp);
//					
//					if(ffas != null && ffas.length > 0) {
//						 FieldDataIdentifierSpec[] aaa = DataSetControllerImpl.getFieldDataIdentifierSpecs_private(
//								 ffas, simulation.getVersion().getOwner(), true, allExternalDataIdentifiers);
//
//							System.out.println((ffas == null) ? "null" : exp.infix() + ", not null:" + ffas.length);
//					}
//				}
			}
			
        }
        
        if(outputBaseDir != null && bHasDataOnly == true && simsToExport.size() == 0) {
        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " has no simulations with any results.");
        	for(String solverName : solverNames) {
            	CLIStandalone.writeSimErrorList(outputBaseDir, "   " + solverName);
        	}
        	return false;
        }
        if(bMakeLogsOnly) {
        	return false;
        }
        
        SEDMLExporter sedmlExporter = new SEDMLExporter(bioModel, sedmlLevel, sedmlVersion, simsToExport);
        String sedmlString = sedmlExporter.getSEDMLFile(outputDir, vcmlName, bForceVCML, bHasDataOnly, true);
        XmlUtil.writeXMLStringToFile(sedmlString, String.valueOf(Paths.get(outputDir, vcmlName + ".sedml")), true);

        // libCombine needs native lib
        ResourceUtil.setNativeLibraryDirectory();
        NativeLoader.load("combinej");

        boolean isDeleted = false;
        boolean isCreated;

        try {
            CombineArchive archive = new CombineArchive();

            String[] files;

            // TODO: try-catch if no files
            File dir = new File(outputDir);
            files = dir.list();

            for (String sd : files) {
                if (sd.endsWith(".sedml")) {
                    archive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            "./" + sd, // target file name
                            KnownFormats.lookupFormat("sedml"),
                            true // mark file as master
                    );
                } else if (sd.endsWith(".sbml") || sd.endsWith(".xml")) {
                    archive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            "./" + sd, // target file name
                            KnownFormats.lookupFormat("sbml"),
                            false // mark file as master
                    );
                }
            }

            archive.addFile(
                    Paths.get(String.valueOf(vcmlFilePath)).toString(),
                    "./" + vcmlName + ".vcml",
                    "http://purl.org/NET/mediatypes/application/vcml+xml",
                    false
            );
            
            // writing into combine archive
            String omexPath = Paths.get(outputDir, vcmlName + ".omex").toString();
            File omexFile = new File(omexPath);

            // Deleting file if already exists with same name
            if(omexFile.exists()) {
                omexFile.delete();
            }
            isCreated = archive.writeToFile(omexPath);

            // Removing all other files(like SEDML, XML, SBML) after archiving
            for (String sd : files) {
                // removing
                if (sd.endsWith(".sedml") || sd.endsWith(".sbml") || sd.endsWith("xml") || sd.endsWith("vcml")) {
                    isDeleted = Paths.get(outputDir, sd).toFile().delete();
                }
            }

            if (isDeleted) System.out.println("Removed intermediary files");

        } catch (Exception e) {
            throw new RuntimeException("createZipArchive threw exception: " + e.getMessage());
        }
        return isCreated;
    }

	private static String[] parseArgs(String[] args) {
		int position = 0;
    	for(String s : args) {
    		if("-vcml".equalsIgnoreCase(s)) {
    			bForceVCML = true;
    			args = ArrayUtils.remove(args, position);
    			break;
    		}
    		position++;
    	}
    	
    	position = 0;
    	for(String s : args) {
    		if("-hasDataOnly".equalsIgnoreCase(s)) {
    			bHasDataOnly = true;
    			args = ArrayUtils.remove(args, position);
    			break;
    		}
    		position++;
    	}
    	
    	position = 0;
    	for(String s : args) {
    		if("-makeLogsOnly".equalsIgnoreCase(s)) {
    			bMakeLogsOnly = true;
    			args = ArrayUtils.remove(args, position);
    			break;
    		}
    		position++;
    	}

		return args;
	}
}
