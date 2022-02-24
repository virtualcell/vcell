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
import org.eclipse.jetty.util.resource.Resource;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbml.libcombine.CombineArchive;
import org.sbml.libcombine.KnownFormats;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.OntUtil;
import org.sbpax.schemas.util.SBPAX3Util;
import org.sbpax.util.SesameRioUtil;
import org.vcell.cli.CLIHandler;
import org.vcell.cli.CLIStandalone;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.sedml.PubMet;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.Version;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
	private static Map <String, BioModelInfo> bioModelInfoMap = new LinkedHashMap<>();		// key: biomodel id,   value: biomodel info
	private static Map <String, BioModelInfo> bioModelInfoMap2 = new LinkedHashMap<>();		// key: biomodel name, value: biomodel info
	
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
					String biomodelId = "biomodel_" + bi.getVersion().getVersionKey();
					String biomodelName = bi.getVersion().getName();
					//String biomodelId2 = "biomodel_" + bi.getModelKey();
					bioModelInfoMap.put(biomodelId, bi);
					bioModelInfoMap2.put(biomodelName, bi);
				}
//				System.out.println("User: " + user.getName() + "   count published biomodels: " + count);
			}
		} catch (SQLException | DataAccessException e1) {
			System.err.println("\n\n\n=====>>>>EXPORT FAILED: failed to retrieve metadata");
			e1.printStackTrace();
		}
		System.out.println("Found " + bioModelInfoMap.size() + " public BioNodelInfo objects");


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
        List<Simulation> simulationsToRemove = new ArrayList<> ();
		for (Simulation simulation : bioModel.getSimulations()) {
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
				simulationsToRemove.add(simulation);
	        	CLIStandalone.writeSimErrorList(outputBaseDir, vcmlName + " excluded: FieldData not supported at this time.");
				SolverDescription solverDescription = simulation.getSolverTaskDescription().getSolverDescription();
				String solverName = solverDescription.getShortDisplayLabel();
				CLIStandalone.writeSimErrorList(outputBaseDir, "   " + solverName);
			}
		}
		for(Simulation simulation : simulationsToRemove) {
			try {
				bioModel.removeSimulation(simulation);
			} catch (PropertyVetoException e) {
				System.out.println("Failed to remove simulation with field data");
				e.printStackTrace();
			}
		}
		
		// we replace the obsolete solver with the fully supported equivalent
		for (Simulation simulation : bioModel.getSimulations()) {
			if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
				try {
					simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
				} catch (PropertyVetoException e) {
					System.out.println("Failed to replace obsolete solver");
					e.printStackTrace();
				}
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
        
        String rdfString = getMetadata(vcmlName, bioModel);
        XmlUtil.writeXMLStringToFile(rdfString, String.valueOf(Paths.get(outputDir, "metadata.rdf")), true);

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
                            "./" + sd,
                            KnownFormats.lookupFormat("sbml"),
                            false // mark file as master
                    );
                } else if (sd.endsWith(".rdf")) {
                    archive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            "./" + sd,
                            "http://identifiers.org/combine.specifications/omex-metadata",
//                            KnownFormats.lookupFormat("xml"),
                            false
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
                if (sd.endsWith(".sedml") || sd.endsWith(".sbml") || sd.endsWith("xml") || sd.endsWith("vcml") || sd.endsWith("rdf")) {
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
	
    private static String getMetadata(String vcmlName, BioModel bioModel) {
    	String ret = "";
        String ns = DefaultNameSpaces.EX.uri;

		Graph graph = new HashGraph();
		Graph schema = new HashGraph();

        // recover the bioModelInfo
        BioModelInfo bioModelInfo = bioModelInfoMap.get(vcmlName);		// we first assume that vcml name is the model id
        if(bioModelInfo == null) {										// if not, we try based on biomodel name
        	bioModelInfo = bioModelInfoMap2.get(vcmlName);
        }
        if(bioModelInfo == null) {								// perhaps it's not public, in which case is not in the map
        	String description = "http://omex-library.org/" + vcmlName + ".omex";	// make an empty rdf file
        	URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
    		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);
    		try {
    			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
    			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
    			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
    		} catch (RDFHandlerException e) {
    			e.printStackTrace();
    		}
    		return ret;
        }
        PublicationInfo[] publicationInfos = bioModelInfo.getPublicationInfos();
        if(publicationInfos == null || publicationInfos.length == 0) {				// we may not have PublicationInfo
        	String description = "http://omex-library.org/" + vcmlName + ".omex";	// make an empty rdf file
        	URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
    		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);
    		try {
    			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
    			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
    			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
    		} catch (RDFHandlerException e) {
    			e.printStackTrace();
    		}
    		return ret;
        }
        
        PublicationInfo publicationInfo = publicationInfos[0];
        String bioModelName = bioModel.getName();
        Version version = bioModelInfo.getVersion();
        String[] creators = publicationInfo.getAuthors();
        String citation = publicationInfo.getCitation();
        String doi = publicationInfo.getDoi();
        Date pubDate = publicationInfo.getPubDate();
        String pubmedid = publicationInfo.getPubmedid();
        String sTitle = publicationInfo.getTitle();
        String url = publicationInfo.getUrl();
        List<String> contributors = new ArrayList<>();
        contributors.add("Dan Vasilescu");
        contributors.add("Michael Blinov");
        contributors.add("Ion Moraru");
        
		String description = "http://omex-library.org/" + vcmlName + ".omex";	// "http://omex-library.org/biomodel_12345678.omex";
		URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
		Literal descTitle = OntUtil.createTypedString(schema, sTitle);
		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);		// <rdf:Description rdf:about='http://omex-library.org/Monkeyflower_pigmentation_v2.omex'>
		graph.add(descriptionURI, PubMet.Title, descTitle);
		
		try {
			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
//			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		
		String end = "\n\n" + ret.substring(ret.indexOf(PubMet.EndDescription0));
		ret = ret.substring(0, ret.indexOf(PubMet.EndDescription0));

		ret += PubMet.CommentTaxon;
		
		ret += PubMet.CommentOther;
		ret += PubMet.StartIs;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String isLabel = "vcell:" + version.getVersionKey();
		ret += isLabel;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += isLabel;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIs;

		ret += PubMet.StartIsDescribedBy;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String pubmed = "pubmed:" + pubmedid;
		ret += pubmed;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += pubmed;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIsDescribedBy;

		ret += PubMet.CommentCreator;
		for(String creator : creators) {
			ret += PubMet.StartCreator;
			ret += PubMet.StartDescription;
			ret += PubMet.StartName;
			ret += creator;
			ret += PubMet.EndName;
			ret += PubMet.StartLabel;
			ret += creator;
			ret += PubMet.EndLabel;
			ret += PubMet.EndDescription;
			ret += PubMet.EndCreator;
		}
		
		ret += PubMet.CommentContributor;
		for(String contributor : contributors) {
			ret += PubMet.StartContributor;
			ret += PubMet.StartDescription;
			ret += PubMet.StartName;
			ret += contributor;
			ret += PubMet.EndName;
			ret += PubMet.StartLabel;
			ret += contributor;
			ret += PubMet.EndLabel;
			ret += PubMet.EndDescription;
			ret += PubMet.EndContributor;
		}

		ret += PubMet.CommentCitations;
		ret += PubMet.StartIsDescribedBy;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String sdoi = "doi:" + doi;
		ret += sdoi;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += citation;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIsDescribedBy;
		
		ret += PubMet.CommentLicense;
		ret += PubMet.StartLicense;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String lic = "spdx:" + "CC0-1.0";
		ret += lic;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += "CC0-1.0";
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret+= PubMet.EndLicense;
		
		String sPubDate = new SimpleDateFormat("yyyy-MM-dd").format(pubDate);
		ret += PubMet.CommentCreated;
		ret += PubMet.StartCreated;
		ret += PubMet.StartDescription;
		ret += PubMet.PrefixCreated;
		ret += new SimpleDateFormat("yyyy-MM-dd").format(pubDate);
		ret += PubMet.SuffixCreated;
		ret += PubMet.EndDescription;
		ret += PubMet.EndCreated;
		
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		ret += PubMet.CommentModified;
		ret += PubMet.StartModified;
		ret += PubMet.StartDescription;
		ret += PubMet.PrefixModified;
		ret += new SimpleDateFormat("yyyy-MM-dd").format(today);
		ret += PubMet.SuffixModified;
		ret += PubMet.EndDescription;
		ret += PubMet.EndModified;
		
		ret += end;
		System.out.println(ret);
		System.out.println("");
		return(ret);
    }
    
    public static void main(String[] args) {
    	
		Graph graph = new HashGraph();
				
		String ns = DefaultNameSpaces.EX.uri;						// http://example/org/
		URI r1 = graph.getValueFactory().createURI(ns + "r1");		// http://example/org/r1
		URI pe1 = graph.getValueFactory().createURI(ns + "pe1");
		URI pe2 = graph.getValueFactory().createURI(ns + "pe2");
		URI ce3 = graph.getValueFactory().createURI(ns + "ce3");
		
		graph.add(r1, RDF.TYPE, BioPAX3.BiochemicalReaction);		// add the reaction to the graph, it'll look like this:
		graph.add(pe1, RDF.TYPE, BioPAX3.Protein);
		graph.add(pe2, RDF.TYPE, BioPAX3.Protein);
//		graph.add(ce3, RDF.TYPE, BioPAX3.Catalysis);
		
		graph.add(r1, BioPAX3.left, pe1);
		graph.add(r1, BioPAX3.right, pe2);						// add to reaction a child named right, it'll look like this
		graph.add(pe2, BioPAX3.Catalysis, ce3);						// add to reaction a child named right, it'll look like this
		
		try {
			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
			System.out.println("here");
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
 
		System.out.println("finished");
		
    	/*
		Graph graph = new HashGraph();
		Graph schema = new HashGraph();
		
		ValueFactory factory = ValueFactoryImpl.getInstance();
		
		
		String descriptionLocation = "http://omex-library.org/biomodel_12345678.omex";
		URI descriptionURI = ValueFactoryImpl.getInstance().createURI(descriptionLocation);
		Literal descTitle = OntUtil.createTypedString(schema, "Publication title");
		
		URI authorProperty = OntUtil.createDatatypeProperty(schema, DefaultNameSpaces.RDF.uri + "contributor");
		URI isDescribedBy = OntUtil.createObjectProperty(schema, DefaultNameSpaces.BQMODEL.uri + "isDescribedBy");
		URI is = OntUtil.createObjectProperty(schema, DefaultNameSpaces.BQMODEL.uri + "is");
		
		
//		alternate way, using factory
//		URI isDescribedBy = factory.createURI(DefaultNameSpaces.BQMODEL.uri, "isDescribedBy");
//		OntUtil.addTypedComment(schema, isDescribedBy, "A relationship");										// shema, resource, comment
//		Literal aaa = factory.createLiteral("aaa label");

		
//		URI ccc = OntUtil.createAnnotationProperty(graph, uri);				// graph, uri
//		URI ddd = OntUtil.createURIIndividual(graph, uri, RDF.TYPE);		// graph, uri, RDF.TYPE
//		URI fff = SBPAX3Util.addSubEntity(graph, is, descriptionURI, sbTerm);		// graph, uri, parent, sbTerm
		
//		OntUtil.addEnglishComment(schema, creatorProperty, "The authors of this, one per property value.");		// schema, resource, comment
//		BNode node = OntUtil.createDataRange(schema, "Dan Vasilescu");
		
		URI contributor = factory.createURI(DefaultNameSpaces.DUBLIN_CORE.uri, "contributor");
		
		graph.add(PubMet.rdfURI, RDF.TYPE, authorProperty);					// add to the rdf root
		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);		// <rdf:Description rdf:about='http://omex-library.org/Monkeyflower_pigmentation_v2.omex'>
		graph.add(descriptionURI, PubMet.Creator, PubMet.rdfsURI);			// <rdfs:creator rdf:resource="http://www.w3.org/2000/01/rdf-schema#"/>
		graph.add(descriptionURI, isDescribedBy, descTitle);
		
		try {
			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
			String ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
			System.out.println("here");

		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		System.out.println("finished");
*/
    }
}
