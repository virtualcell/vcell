package org.vcell.cli.vcml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.util.NativeLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbml.libcombine.CombineArchive;
import org.sbml.libcombine.KnownFormats;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.OntUtil;
import org.sbpax.util.SesameRioUtil;
import org.vcell.cli.*;
import org.vcell.sedml.PubMet;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.*;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VcmlOmexConverter {
	private static final Logger logger = LogManager.getLogger(VcmlOmexConverter.class);

	public static void convertOneFile(File input,
									  File outputDir,
									  ModelFormat modelFormat,
									  boolean bForceLogFiles,
									  boolean bValidateOmex)
			throws IOException, DataAccessException, XmlParseException {

		if (input == null || !input.isFile() || !input.toString().endsWith(".vcml")) {
			throw new RuntimeException("expecting inputFilePath '"+input+"' to be an existing .vcml file");
		}
		Predicate<Simulation> simulationExportFilter = simulation -> true;
		BioModelInfo bioModelInfo = null;
		boolean isCreated = vcmlToOmexConversion(input.getAbsolutePath(), bioModelInfo, null, outputDir.getAbsolutePath(),
				simulationExportFilter, modelFormat, bForceLogFiles, bValidateOmex);
		if (isCreated) {
			logger.info("Combine archive created for `" + input + "`");
		} else {
			logger.error("Failed converting VCML to OMEX archive for `" + input + "`");
		}
	}

	public static void convertFiles(CLIDatabaseService cliDatabaseService,
									File input,
									File outputDir,
									ModelFormat modelFormat,
									boolean bHasDataOnly,
									boolean bMakeLogsOnly,
									boolean bNonSpatialOnly,
									boolean bForceLogFiles,
									boolean bValidateOmex)
			throws IOException, SQLException, DataAccessException {

		Set<String> hasNonSpatialSet = new LinkedHashSet<>();
		Set<String> hasSpatialSet = new LinkedHashSet<>();
		if (input == null || !input.isDirectory()) {
			throw new RuntimeException("expecting inputFilePath to be an existing directory");
		}

		FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
		String[] inputFiles = input.list(filterVcmlFiles);		// jusr a list of vcml names, like biomodel-185577495.vcml, ...
		if (inputFiles == null) {
			logger.error("No VCML files found in the directory `" + input + "`");
		}

		writeSimErrorList(outputDir.getAbsolutePath(), "bForceVCML is " + modelFormat.equals(ModelFormat.VCML), bForceLogFiles);
		writeSimErrorList(outputDir.getAbsolutePath(), "bForceSBML is " + modelFormat.equals(ModelFormat.SBML), bForceLogFiles);
		writeSimErrorList(outputDir.getAbsolutePath(), "hasDataOnly is " + bHasDataOnly, bForceLogFiles);
		writeSimErrorList(outputDir.getAbsolutePath(), "makeLogsOnly is " + bMakeLogsOnly, bForceLogFiles);
		writeSimErrorList(outputDir.getAbsolutePath(), "nonSpatialOnly is " + bNonSpatialOnly, bForceLogFiles);

//            assert inputFiles != null;
		for (String inputFile : inputFiles) {
			File file = new File(input, inputFile);
			logger.info(" ============== start: " + inputFile);
			try {
				if (inputFile.endsWith(".vcml")) {
					Predicate<Simulation> simulationExportFilter = simulation -> keepSimulation(simulation, bHasDataOnly, bNonSpatialOnly, cliDatabaseService);
					// recover the bioModelInfo
					List<BioModelInfo> publicBioModelInfos = cliDatabaseService.queryPublicBioModels();
					BioModelInfo bioModelInfo = null;
					String vcmlName = FilenameUtils.getBaseName(inputFile);
					for (BioModelInfo bmi : publicBioModelInfos){
						if (vcmlName.equals("biomodel_"+bmi.getVersion().getVersionKey()) || vcmlName.equals(bmi.getVersion().getName())){
							bioModelInfo = bmi;
						}
					}

					boolean isCreated = vcmlToOmexConversion(file.toString(), bioModelInfo, outputDir.getAbsolutePath(), outputDir.getAbsolutePath(),
															simulationExportFilter, modelFormat, bForceLogFiles, bValidateOmex);
					if (isCreated) {
						logger.info("Combine archive created for file(s) `" + inputFile + "`");
					}
					else {
						logger.error("Failed converting VCML to OMEX archive for `" + inputFile + "`");
					}
				} else {
					logger.error("No VCML files found in the directory `" + input + "`");
				}
			} catch (Exception e) {
				logger.error("EXPORT FAILED: file=" +inputFile+", error="+e.getMessage(), e);
				CLIUtils.writeDetailedErrorList(outputDir.getAbsolutePath(), inputFile + ",   " + e.getMessage(), bForceLogFiles);
			}
		}
		writeLogForOmexCreation(outputDir.getAbsolutePath(), hasNonSpatialSet, hasSpatialSet, bForceLogFiles);
	}

	private static boolean keepSimulation(Simulation simulation, boolean bHasDataOnly, boolean bNonSpatialOnly, CLIDatabaseService cliDatabaseService) {

		//
		// exclude simulations with field data
		//
		Enumeration<Variable> variables = simulation.getMathDescription().getVariables();
		while(variables.hasMoreElements()) {
			Variable var = variables.nextElement();
			Expression exp = var.getExpression();
			FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments( exp);
			if(ffas != null && ffas.length > 0) {
				logger.warn("excluding simulation '"+simulation.getName()+", it has field data, not yet supported for OMEX export");
				return false;
			}
		}

		//
		// exclude spatial simulations if bNonSpatialOnly
		//
		if(bNonSpatialOnly == true) {		// we skip all spatial simulations
			SolverDescription sd = simulation.getSolverTaskDescription().getSolverDescription();
			if(sd.isSpatial()) {
				logger.warn("excluding simulation '"+simulation.getName()+", it is spatial, and bNonSpatialOnly was specified.");
				return false;
			}
		}

		//
		// exclude simulations without data if bHasDataOnly
		if (bHasDataOnly) {
			// check server status
			KeyValue parentKey = simulation.getSimulationVersion().getParentSimulationReference();
			try {
				SimulationJobStatusPersistent[] statuses = cliDatabaseService.querySimulationJobStatus(parentKey == null ? simulation.getKey() : parentKey);
				if (statuses == null || statuses.length == 0) {
					logger.warn("excluding simulation '" + simulation.getName() + ", it has not been run, and bHasDataOnly was specified.");
					return false;
				}
				boolean hasData = false;
				for (int i = 0; i < statuses.length; i++) {
					if (statuses[i].hasData()) {
						hasData = true;
					}
				}
				if (!hasData) {
					logger.warn("excluding simulation '" + simulation.getName() + ", it been run, but has no data, and bHasDataOnly was specified.");
					return false;
				}
			} catch (Exception e){
				logger.error("failed to retrieve status for simulation '"+simulation.getSimulationVersion()+"'", e);
				throw new RuntimeException("failed to retrieve status for simulation '"+simulation.getSimulationVersion()+"'", e);
			}
		}
		return true;
	}


	public static void queryVCellDbPublishedModels(CLIDatabaseService cliDatabaseService, File outputDir, boolean bForceLogFiles) throws SQLException, DataAccessException, IOException {
		VCInfoContainer vcic;
		Map<String, List<String>> publicationToModelMap = new LinkedHashMap<>();
		int count = 0;		// number of biomodels with publication info
		List<BioModelInfo> bioModelInfos = cliDatabaseService.queryPublicBioModels();
		logger.info("Found " + bioModelInfos.size() + " public BioNodelInfo objects");

		for(BioModelInfo bi : bioModelInfos) {
			String biomodelId = "biomodel_" + bi.getVersion().getVersionKey();
			PublicationInfo[] pis = bi.getPublicationInfos();
			if(pis != null && pis.length > 0) {
				// let's see what has PublicationInfo
				logger.trace("biomodelId="+biomodelId);
				count++;
				for(PublicationInfo pi : pis) {
					if(pi.getTitle().contains("Computational Modeling of RNase")) {
						logger.trace("publication title is "+pi.getTitle());
					}
					if(publicationToModelMap.containsKey(pi.getTitle())) {
						List<String> biomodelIds = publicationToModelMap.get(pi.getTitle());
						biomodelIds.add(biomodelId);
						publicationToModelMap.put(pi.getTitle(), biomodelIds);
					} else {
						List<String> biomodelIds = new ArrayList<String> ();
						biomodelIds.add(biomodelId);
						publicationToModelMap.put(pi.getTitle(), biomodelIds);
					}
				}
			}
		}
		logger.info("counted published biomodels: " + count);

		for( Map.Entry<String,List<String>> entry : publicationToModelMap.entrySet()) {
			String pubTitle = entry.getKey();
			List<String> models = entry.getValue();
			if(models.size() > 1) {
				String row = "";
				row += pubTitle;
				for(String model : models) {
					row += (", " + model);
				}
				writeMultiModelPublications(outputDir.getAbsolutePath(), row, bForceLogFiles);
				logger.trace("publication :"+row);
			}
		}
	}

	private static boolean vcmlToOmexConversion(String inputFilePath, BioModelInfo bioModelInfo, String outputBaseDir, String outputDir,
												Predicate<Simulation> simulationExportFilter,
												ModelFormat modelFormat,
												boolean bForceLogFiles,
												boolean bValidate
	) throws XmlParseException, IOException {

		int sedmlLevel = 1;
		int sedmlVersion = 2;
        
        String inputVcmlFile = inputFilePath;


        // get VCML name from VCML path
        //String vcmlName = inputVcmlFile.split(File.separator, 10)[inputVcmlFile.split(File.separator, 10).length - 1].split("\\.", 5)[0];
        String vcmlName = FilenameUtils.getBaseName(inputVcmlFile);		// platform independent, strips extension too

		File vcmlFilePath = new File(inputVcmlFile);
        // Create biomodel
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));
        
        int numSimulations = bioModel.getNumSimulations();
        if(outputBaseDir != null && numSimulations == 0) {
			writeSimErrorList(outputBaseDir, vcmlName + " has no simulations.", bForceLogFiles);
        	return false;
        }
        bioModel.refreshDependencies();

        // ========================================================
        try {
		SimulationContext scArray[] = bioModel.getSimulationContexts();
		if (scArray!=null) {
			MathDescription[] mathDescArray = new MathDescription[scArray.length];
			for (int i = 0; i < scArray.length; i++){
				//check if all structure sizes are specified
				scArray[i].checkValidity();
				//
				// compute Geometric Regions if necessary
				//
				cbit.vcell.geometry.surface.GeometrySurfaceDescription geoSurfaceDescription = scArray[i].getGeometry().getGeometrySurfaceDescription();
				if (geoSurfaceDescription!=null && geoSurfaceDescription.getGeometricRegions()==null){
					cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geoSurfaceDescription);
				}
				if (scArray[i].getModel() != bioModel.getModel()){
					throw new Exception("The BioModel's physiology doesn't match that for Application '"+scArray[i].getName()+"'");
				}
				//
				// create new MathDescription
				//
				MathDescription math = scArray[i].createNewMathMapping().getMathDescription();
				//
				// load MathDescription into SimulationContext 
				// (BioModel is responsible for propagating this to all applicable Simulations).
				//
				scArray[i].setMathDescription(math);
			}
		}
        } catch(Exception e) {
        	logger.error(e);
        }

        List<Simulation> simsToExport = Arrays.stream(bioModel.getSimulations()).filter(simulationExportFilter).collect(Collectors.toList());

		// we replace the obsolete solver with the fully supported equivalent
		for (Simulation simulation : simsToExport) {
			if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
				try {
					simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
				} catch (PropertyVetoException e) {
					logger.error("Failed to replace obsolete solver", e);
				}
			}
		}


		Version version = bioModel.getVersion();
        String versionKey = version.getVersionKey().toString();
        String sourcePath = "https://vcellapi-beta.cam.uchc.edu:8080/biomodel/" + versionKey + "/diagram";
        String destinationPath = Paths.get(outputDir, "diagram.png").toString();
        URL source = new URL(sourcePath);
        File destination = new File(destinationPath);
        int connectionTimeout = 10000;
        int readTimeout = 20000;
        try {
       	 	FileUtils.copyURLToFile(source, destination, connectionTimeout, readTimeout);		// diagram
        } catch(IOException e) {
        	logger.error("Diagram not present in source="+sourcePath, e);
        }

        String rdfString = getMetadata(vcmlName, bioModel, destination, bioModelInfo);
        XmlUtil.writeXMLStringToFile(rdfString, String.valueOf(Paths.get(outputDir, "metadata.rdf")), true);
        
        SEDMLExporter sedmlExporter = new SEDMLExporter(bioModel, sedmlLevel, sedmlVersion, simsToExport);
        SEDMLDocument sedmlDocument = sedmlExporter.getSEDMLFile0(outputDir, vcmlName,
				modelFormat.equals(ModelFormat.VCML), modelFormat.equals(ModelFormat.SBML), true);
        SedML sedmlModel = sedmlDocument.getSedMLModel();
        if(sedmlModel.getModels().size() == 0) {
            File dir = new File(outputDir);
            String[] files = dir.list();
            removeOtherFiles(outputDir, files);
			writeSimErrorList(outputBaseDir, vcmlName + ": the sedm is empty.", bForceLogFiles);
//        	String allSolverNames = "";
//        	for(String solverName : solverNames) {
//        		allSolverNames += (solverName + " ");
//        	}
//        	CLIStandalone.writeSimErrorList(outputBaseDir, "   " + allSolverNames);
        	return false;
        }
        
        String sedmlString = sedmlDocument.writeDocumentToString();
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
                            false
                    );
                } else if (sd.endsWith(".rdf")) {
                    archive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            "./" + sd,
                            "http://identifiers.org/combine.specifications/omex-metadata",
//                            KnownFormats.lookupFormat("xml"),
                            false
                    );
                } else if(sd.endsWith(".png")) {
                    archive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            "./" + sd,
                            "http://purl.org/NET/mediatypes/image/png",		// was "https://www.iana.org/assignments/media-types/image/png"
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
            
//            CaOmexManifest manifest = archive.getManifest();
//            // copy all the files unarchived in separate directories for easier tracking
//            String dest = Paths.get(outputDir, vcmlName).toString();
//            File destDir = new File(dest);
//            FileUtils.forceMkdir(destDir);
//            for (String sd : files) {
//                if (sd.endsWith(".sedml") || sd.endsWith(".sbml") || sd.endsWith("xml") || sd.endsWith("vcml") || sd.endsWith("rdf")) {
//                	String src = Paths.get(outputDir, sd).toString();
//                	File srcFile = new File(src);
//                	FileUtils.copyFileToDirectory(srcFile, destDir);
//                }
//            }
//        	File srcFile = new File(inputVcmlFile);
//        	FileUtils.copyFileToDirectory(srcFile, destDir);

			if (bValidate){
				logger.info("validating Omex file '"+omexFile+"'");
				List<VCDocument> reread_docs = XmlHelper.readOmex(omexFile, new CLIUtils.LocalLogger());
				logger.info("validated Omex file '"+omexFile+"'");
				BioModel reread_BioModel = (BioModel)reread_docs.get(0);
				reread_BioModel.refreshDependencies();
				SimulationContext.MathMappingCallback mathMappingCallback = new SimulationContext.MathMappingCallback() {
					@Override
					public void setMessage(String message) { }
					@Override
					public void setProgressFraction(float fractionDone) { }
					@Override
					public boolean isInterrupted() { return false; }
				};
				SimulationContext.NetworkGenerationRequirements networkGenerationRequirements =
						SimulationContext.NetworkGenerationRequirements.AllowTruncatedStandardTimeout;

				bioModel.getSimulationContext(0).refreshMathDescription(mathMappingCallback, networkGenerationRequirements);
				reread_BioModel.getSimulationContext(0).refreshMathDescription(mathMappingCallback, networkGenerationRequirements);
				MathDescription origMathDescription = bioModel.getSimulationContext(0).getMathDescription();
				MathDescription rereadMathDescription = reread_BioModel.getSimulationContext(0).getMathDescription();
				MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),origMathDescription, rereadMathDescription);
				if (!mathCompareResults.isEquivalent()){
					logger.error("MathDescriptions ARE NOT equivalent after VCML->SBML->VCML round-trip validation: "+mathCompareResults.toDatabaseStatus());
				} else {
					logger.info("MathDescriptions ARE equivalent after VCML->SBML->VCML round-trip validation: "+mathCompareResults.toDatabaseStatus());
				}
				logger.error("writing extra files ./orig_vcml.xml and ./reread_vcml.xml for debugging - remove later");
				Files.write(Paths.get(outputDir, "orig_vcml.xml"), XmlHelper.bioModelToXML(bioModel, false).getBytes(StandardCharsets.UTF_8));
				Files.write(Paths.get(outputDir, "reread_vcml.xml"), XmlHelper.bioModelToXML(reread_BioModel, false).getBytes(StandardCharsets.UTF_8));
			}

            // Removing all other files(like SEDML, XML, SBML) after archiving
            removeOtherFiles(outputDir, files);

        } catch (Exception e) {
			throw new RuntimeException("createZipArchive threw exception: " + e.getMessage(), e);
        }
        return isCreated;
    }
    
    private static void removeOtherFiles(String outputDir, String[] files) {
    	boolean isDeleted = false;
        for (String sd : files) {
            if (sd.endsWith(".sedml") || sd.endsWith(".sbml") || sd.endsWith("xml") || sd.endsWith("vcml") || sd.endsWith("rdf") || sd.endsWith("png")) {
                isDeleted = Paths.get(outputDir, sd).toFile().delete();
            }
        }
        if (isDeleted) logger.trace("Removed intermediary files in "+outputDir);
    }

	
    private static String getMetadata(String vcmlName, BioModel bioModel, File diagram, BioModelInfo bioModelInfo) {
    	String ret = "";
        String ns = DefaultNameSpaces.EX.uri;

		Graph graph = new HashGraph();
		Graph schema = new HashGraph();

        if(bioModelInfo == null) {								// perhaps it's not public, in which case is not in the map
        	String description = "http://omex-library.org/" + vcmlName + ".omex";	// make an empty rdf file
        	URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
    		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);
    		try {
    			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
    			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
    			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
    		} catch (RDFHandlerException e) {
    			logger.error(e);
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
    			logger.error(e);
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
			logger.error(e);
		}
		
		String end = "\n\n" + ret.substring(ret.indexOf(PubMet.EndDescription0));
		ret = ret.substring(0, ret.indexOf(PubMet.EndDescription0));

		// https://vcellapi-beta.cam.uchc.edu:8080/biomodel/200301683/diagram
		// <collex:thumbnail rdf:resource="http://omex-library.org/Monkeyflower_pigmentation_v2.omex/Figure1.png"/>
		if(diagram.exists()) {
			ret += PubMet.StartDiagram;
			ret += description;
			ret += "/diagram.png";
			ret += PubMet.EndDiagram;
		}
		
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
		
//		ret += PubMet.CommentLicense;
//		ret += PubMet.StartLicense;
//		ret += PubMet.StartDescription;
//		ret += PubMet.StartIdentifier;
//		ret += PubMet.ResourceIdentifier;
//		String lic = "spdx:" + "CC0-1.0";
//		ret += lic;
//		ret += PubMet.EndIdentifier;
//		ret += PubMet.StartLabel;
//		ret += "CC0-1.0";
//		ret += PubMet.EndLabel;
//		ret += PubMet.EndDescription;
//		ret+= PubMet.EndLicense;
		
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
		logger.trace(ret);
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
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
 

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

	// when creating the omex files from vcml, we write here the list of models that have spatial, non-spatial or both applications
	public static void writeLogForOmexCreation(String outputBaseDir, Set<String> hasNonSpatialSet, Set<String> hasSpatialSet, boolean bForceLogFiles) throws IOException {
		if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
			String s = "";
			s += "Only Non-spatial applications\n";
			for (String name : hasNonSpatialSet) {
				if (!hasSpatialSet.contains(name)) {
					s += (name + "\n");
				}
			}
			s += "\nOnly Spatial applications\n";
			for (String name : hasSpatialSet) {
				if (!hasNonSpatialSet.contains(name)) {
					s += (name + "\n");
				}
			}
			s += "\nBoth Spatial and Non-Spatial applications\n";
			Set<String> hasBothSet = new LinkedHashSet<>(hasSpatialSet);
			hasBothSet.addAll(hasNonSpatialSet);
			for (String name : hasBothSet) {
				s += (name + "\n");
			}
			String dest = outputBaseDir + File.separator + "omexCreationLog.txt";
			Files.write(Paths.get(dest), (s + "\n").getBytes(),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
	}
	public static void writeMultiModelPublications(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
		if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
			String dest = outputBaseDir + File.separator + "multiModelPublications.txt";
			Files.write(Paths.get(dest), (s + "\n").getBytes(),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
	}
	// biomodels with no simulations and biomodels with no sim results (fired when building the omex files)
	public static void writeSimErrorList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
		if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
			String dest = outputBaseDir + File.separator + "simsErrorLog.txt";
			Files.write(Paths.get(dest), (s + "\n").getBytes(),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
	}

}
