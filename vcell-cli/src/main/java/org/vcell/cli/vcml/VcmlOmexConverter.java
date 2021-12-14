package org.vcell.cli.vcml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.solver.Simulation;
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
import org.vcell.util.document.KeyValue;

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
import java.util.List;
import java.util.Set;

public class VcmlOmexConverter {

    private static ConnectionFactory conFactory;
	private static AdminDBTopLevel adminDbTopLevel;
	private static boolean bForceVCML;		// set by the -vcml CL argument, means we export to omex as vcml (if missing, default we try sbml first)
	private static boolean bHasDataOnly;	// we only export those simulations that have at least some results; set by -hasDataOnly CL argument
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
    	 
    	if (bHasDataOnly) {
			try {
				conFactory = DatabaseService.getInstance().createConnectionFactory();
				adminDbTopLevel = new AdminDBTopLevel(conFactory);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("\n\n\n=====>>>>EXPORT FAILED: connection to database failed");
				e.printStackTrace();
			}
    	}
    	
        
        if (input != null && input.isDirectory()) {
            FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
            String[] inputFiles = input.list(filterVcmlFiles);
            if (inputFiles == null) {
                System.err.println("No VCML files found in the directory `" + input + "`");
            }
            String outputDir = args[3];
            
            CLIStandalone.writeSimErrorList(outputDir, "hasDataOnly is " + bHasDataOnly);
            
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
        

        File vcmlFilePath = new File(inputVcmlFile);

        // Create biomodel
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));
        bioModel.refreshDependencies();
        
        int numSimulations = bioModel.getNumSimulations();
        if(outputBaseDir != null && numSimulations == 0) {
        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " has no simulations.");
        	return false;
        }

        // NOTE: SEDML exporter exports both SEDML as well as required SBML
        List<Simulation> simsToExport =null;
        if (bHasDataOnly) {
        	// make list of simulations to export with only sims that have data on the server
        	simsToExport = new ArrayList<Simulation>();
			for (Simulation simulation : bioModel.getSimulations()) {
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
			}
        }
        
        if(outputBaseDir != null && bHasDataOnly == true && simsToExport.size() == 0) {
        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " has no simulations with any results.");
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
		return args;
	}
}
