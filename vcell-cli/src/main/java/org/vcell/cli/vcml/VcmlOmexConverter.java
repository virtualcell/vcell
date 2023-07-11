package org.vcell.cli.vcml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XmlHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.sbml.OmexPythonUtils;
import org.vcell.sbml.UnsupportedSbmlExportException;
import org.vcell.sedml.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCInfoContainer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VcmlOmexConverter {
	private static final Logger logger = LogManager.getLogger(VcmlOmexConverter.class);

	public static final String jobConfigFile = "jobConfig.txt";
	public static final String jobLogFileName = "jobLog.txt";

	public static void convertOneFile(File input,
									  File outputDir,
									  ModelFormat modelFormat,
									  boolean bWriteLogFiles,
									  boolean bValidateOmex,
									  boolean bSkipUnsupportedApps)
			throws SEDMLExporter.SEDMLExportException, OmexPythonUtils.OmexValidationException, IOException {

		if (input == null || !input.isFile() || !input.toString().endsWith(".vcml")) {
			throw new RuntimeException("expecting inputFilePath '"+input+"' to be an existing .vcml file");
		}
		logger.debug("Beginning conversion of `" + input + "`");
		Predicate<Simulation> simulationExportFilter = simulation -> true;
		BioModelInfo bioModelInfo = null;
		final SEDMLEventLog sedmlEventLog;
		if (bWriteLogFiles) {
			sedmlEventLog = new SEDMLEventLogFile(new File(outputDir, jobLogFileName));
		} else {
			sedmlEventLog = (String entry) -> {};
		}
		boolean bHasPython = true;
		List<SEDMLTaskRecord> sedmlTaskRecords = SEDMLExporter.writeBioModel(
				input,
				bioModelInfo,
				outputDir,
				simulationExportFilter,
				modelFormat,
				sedmlEventLog,
				bSkipUnsupportedApps,
				bHasPython,
				bValidateOmex);
		if (!sedmlTaskRecords.stream().anyMatch((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)) {
			logger.info("Combine archive created for `" + input + "`");
		} else {
			List<String> errorList = sedmlTaskRecords.stream()
					.filter((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)
					.map((SEDMLTaskRecord r) -> r.getCSV())
					.collect(Collectors.toList());
			String msg = "Failed converting VCML to OMEX archive for `" + input + "`, errors: "+errorList;
			logger.error(msg);
			throw new RuntimeException(msg);
		}
	}

	public static void convertFiles(CLIDatabaseService cliDatabaseService,
									File inputDir,
									File outputDir,
									ModelFormat modelFormat,
									boolean bHasDataOnly,
									boolean bMakeLogsOnly,
									boolean bNonSpatialOnly,
									boolean bWriteLogFiles,
									boolean bValidateOmex,
									boolean bSkipUnsupportedApps)
			throws IOException, SQLException, DataAccessException {

		final SEDMLEventLog sedmlEventLog;
		if (bWriteLogFiles) {
			sedmlEventLog = new SEDMLEventLogFile(new File(outputDir, jobLogFileName));
		} else {
			sedmlEventLog = (String entry) -> {};
		}

		if (inputDir == null || !inputDir.isDirectory()) {
			throw new RuntimeException("expecting inputFilePath to be an existing directory");
		}

		FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
		String[] inputFileNames = inputDir.list(filterVcmlFiles);		// jusr a list of vcml names, like biomodel-185577495.vcml, ...
		if (inputFileNames == null || inputFileNames.length == 0) {
			throw new RuntimeException("No VCML files found in the directory `" + inputDir + "`");
		}
		
		logger.debug("Beginning conversion of `" + inputDir + "`");

		writeFileEntry(outputDir.getAbsolutePath(), "bForceVCML is " + modelFormat.equals(ModelFormat.VCML), jobConfigFile, bWriteLogFiles);
		writeFileEntry(outputDir.getAbsolutePath(), "bForceSBML is " + modelFormat.equals(ModelFormat.SBML), jobConfigFile, bWriteLogFiles);
		writeFileEntry(outputDir.getAbsolutePath(), "hasDataOnly is " + bHasDataOnly, jobConfigFile, bWriteLogFiles);
		writeFileEntry(outputDir.getAbsolutePath(), "makeLogsOnly is " + bMakeLogsOnly, jobConfigFile, bWriteLogFiles);
		writeFileEntry(outputDir.getAbsolutePath(), "nonSpatialOnly is " + bNonSpatialOnly, jobConfigFile, bWriteLogFiles);

		// get the bioModelInfos from database
		List<BioModelInfo> publicBioModelInfos = cliDatabaseService.queryPublicBioModels();
		for (String inputFileName : inputFileNames) {
			File inputFile = new File(inputDir, inputFileName);
			logger.info(" ============== start: " + inputFileName);
			if (inputFileName.endsWith(".vcml")) {
				Predicate<Simulation> simulationExportFilter = simulation -> keepSimulation(simulation, bHasDataOnly, bNonSpatialOnly, cliDatabaseService);
				BioModelInfo bioModelInfo = null;
				String vcmlName = FilenameUtils.getBaseName(inputFileName);
				for (BioModelInfo bmi : publicBioModelInfos){
					if (vcmlName.equals("biomodel_"+bmi.getVersion().getVersionKey()) || vcmlName.equals(bmi.getVersion().getName())){
						bioModelInfo = bmi;
					}
				}
				if (bioModelInfo == null) {
					logger.error("No BioModelInfo found for `" + inputFileName + "`");
					continue;
				}
				try {
					boolean bHasPython = true;
					List<SEDMLTaskRecord> sedmlTaskRecords = SEDMLExporter.writeBioModel(
							inputFile,
							bioModelInfo,
							outputDir,
							simulationExportFilter,
							modelFormat,
							sedmlEventLog,
							bSkipUnsupportedApps,
							bHasPython,
							bValidateOmex);
					if (!sedmlTaskRecords.stream().anyMatch((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)) {
						logger.info("Combine archive created for `" + inputFileName + "`");
					} else {
						List<String> errorList = sedmlTaskRecords.stream()
								.filter((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)
								.map((SEDMLTaskRecord r) -> r.getCSV())
								.collect(Collectors.toList());
						String msg = "Failed converting VCML to OMEX archive for `" + inputFileName + "`, errors: " + errorList;
						logger.error(msg);
						throw new RuntimeException(msg);
					}
				} catch (SEDMLExporter.SEDMLExportException | OmexPythonUtils.OmexValidationException e) {
					logger.error("Failed converting VCML to OMEX archive for `" + inputFileName + "`", e);
				}
			} else {
				logger.error("No VCML files found in the directory `" + inputDir + "`");
			}
		}
	}

	public static void convertFilesNoDatabase(File inputDir, File outputDir, ModelFormat modelFormat,
											  boolean bWriteLogFiles, boolean bValidateOmex, boolean bSkipUnsupportedApps)
			throws IOException {
		// Start
		if (inputDir == null || !inputDir.isDirectory()) throw new RuntimeException("expecting inputFilePath to be an existing directory");
		final SEDMLEventLog sedmlEventLog;
		if (bWriteLogFiles) {
			sedmlEventLog = new SEDMLEventLogFile(new File(outputDir, jobLogFileName));
		} else {
			sedmlEventLog = (String entry) -> {};
		}
		FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
		String[] inputFileNames = inputDir.list(filterVcmlFiles);		// jusr a list of vcml names, like biomodel-185577495.vcml, ...
		if (inputFileNames == null) throw new RuntimeException("No VCML files found in the directory `" + inputDir + "`");
		
		logger.debug("Beginning conversion of files in `" + inputDir + "`");
		for (String inputFileName : inputFileNames) {
			logger.debug("Beginning conversion of `" + inputFileName + "`");
			File inputFile = new File(inputDir, inputFileName);
			logger.info(" ============== start: " + inputFileName);
				Predicate<Simulation> simulationExportFilter = simulation -> true;
				BioModelInfo bioModelInfo = null;
			try {
				boolean bHasPython = true;
				List<SEDMLTaskRecord> sedmlTaskRecords = SEDMLExporter.writeBioModel(
						inputFile,
						bioModelInfo,
						outputDir,
						simulationExportFilter,
						modelFormat,
						sedmlEventLog,
						bSkipUnsupportedApps,
						bHasPython,
						bValidateOmex);
				if (!sedmlTaskRecords.stream().anyMatch((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)) {
					logger.info("Combine archive created for `" + inputFileName + "`");
				} else {
					List<String> errorList = sedmlTaskRecords.stream()
							.filter((SEDMLTaskRecord r) -> r.getTaskResult() == TaskResult.FAILED)
							.map((SEDMLTaskRecord r) -> r.getCSV())
							.collect(Collectors.toList());
					String msg = "Failed converting VCML to OMEX archive for `" + inputFileName + "`, errors: " + errorList;
					logger.error(msg);
					throw new RuntimeException(msg);
				}
			} catch (SEDMLExporter.SEDMLExportException | OmexPythonUtils.OmexValidationException e) {
				logger.error("Failed converting VCML to OMEX archive for `" + inputFileName + "`", e);
			}
		}
		logger.debug("Completed conversion of files in `" + inputDir + "`");
	}

	private static boolean keepSimulation(Simulation simulation, boolean bHasDataOnly, boolean bNonSpatialOnly, CLIDatabaseService cliDatabaseService) {

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
				String msg = "failed to retrieve status for simulation '"+simulation.getSimulationVersion()+"'";
				logger.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		return true;
	}

	public static void queryVCellDbPublishedModels(CLIDatabaseService cliDatabaseService, File outputDir, boolean bWriteLogFiles) throws SQLException, DataAccessException, IOException {
		VCInfoContainer vcic;
		Map<String, List<String>> publicationToModelMap = new LinkedHashMap<>();
		Map<String, List<String>> modelToPublicationMap = new LinkedHashMap<>();
		int count = 0;		// number of biomodels with publication info
//
//		TODO: call one of the other 2 following functions to bet the published / public biomodel lists
//
//		List<BioModelInfo> bioModelInfos = cliDatabaseService.queryPublishedBioModels();
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
					if(modelToPublicationMap.containsKey(biomodelId)) {
						List<String> biomodelPiTitles = modelToPublicationMap.get(biomodelId);
						biomodelPiTitles.add(pi.getTitle());
						modelToPublicationMap.put(biomodelId, biomodelPiTitles);
					} else {
						List<String> biomodelPiTitles = new ArrayList<> ();
						biomodelPiTitles.add(pi.getTitle());
						modelToPublicationMap.put(biomodelId, biomodelPiTitles);
					}
				}
			}
		}
		logger.info("counted published biomodels: " + count);

		for( Map.Entry<String,List<String>> entry : publicationToModelMap.entrySet()) {
			String fileName = "multiModelPublications.txt";
			String pubTitle = entry.getKey();
			List<String> models = entry.getValue();
			if(models.size() > 1) {
				String row = "";
				row += pubTitle;
				for(String model : models) {
					row += ("; " + model);
				}
				writeFileEntry(outputDir.getAbsolutePath(), row, fileName, bWriteLogFiles);
				logger.trace("publication :"+row);
			}
		}
		for( Map.Entry<String,List<String>> entry : modelToPublicationMap.entrySet()) {
			String fileName = "multiPublicationModels.txt";
			String model = entry.getKey();
			List<String> publications = entry.getValue();
			if(publications.size() > 1) {
				String row = "";
				row += model;
				for(String publication : publications) {
					row += ("; " + publication);
				}
				writeFileEntry(outputDir.getAbsolutePath(), row, fileName, bWriteLogFiles);
				logger.trace("model :"+row);
			}
		}
	}

	private static void writeFileEntry(String outputBaseDir, String entry, String fileName, boolean bWriteLogFiles) throws IOException {
		if (!bWriteLogFiles){
			return;
		}
		Path path = Paths.get(outputBaseDir);
		if (!Files.isDirectory(path)){
			throw new RuntimeException("outputBaseDir '"+outputBaseDir+"' is not a directory, cannot write log file here");
		}
		String dest = outputBaseDir + File.separator + fileName;
		Files.write(Paths.get(dest), (entry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	public static void importOmexFiles(File inputDirectory, File outputDirectory, boolean bWriteLogFile) throws IOException {
		if (inputDirectory == null || !inputDirectory.isDirectory()) {
			throw new RuntimeException("expecting inputFilePath to be an existing directory");
		}

		FilenameFilter filterOmexFiles = (f, name) -> name.endsWith(".omex");
		String[] inputFiles = inputDirectory.list(filterOmexFiles);
		if (inputFiles == null || inputFiles.length == 0) {
			throw new RuntimeException("No OMEX files found in the directory `" + inputDirectory + "`");
		}
		
		writeFileEntry(outputDirectory.getAbsolutePath(), "inputDirectory is " + inputDirectory.getAbsolutePath(), jobConfigFile, bWriteLogFile);
		for (String inputFileName : inputFiles) {
			File inputFile = Paths.get(inputDirectory.getAbsolutePath()).resolve(inputFileName).toFile();
			importOneOmexFile(inputFile, outputDirectory, bWriteLogFile);
		}
	}

	public static void importOneOmexFile(File inputFile, File outputDirectory, boolean bWriteLogFiles) throws IOException {
		logger.debug("Beginning import of `" + inputFile.getName() +"`");
		try {
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            @Override
				public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException{
	                System.err.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
	                if (p == VCLogger.Priority.HighPriority) {
	                	throw new VCLoggerException("Import failed : " + message);
	                }
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };
	        List<BioModel> biomodels = XmlHelper.readOmex(inputFile, logger);
			int i = 0;
			for (BioModel bm : biomodels) {
				String vcmlString = XmlHelper.bioModelToXML(bm);
				File vcmlFile = Paths.get(outputDirectory.getAbsolutePath()).resolve(inputFile.getName()+"_"+i+".vcml").toFile();
				XmlUtil.writeXMLStringToFile(vcmlString, vcmlFile.getAbsolutePath(), true);
				i++;
			}
			writeFileEntry(outputDirectory.getAbsolutePath(), inputFile.getName()+",SUCCEEDED,"+i , jobLogFileName, bWriteLogFiles);
		} catch (Exception e) {
			String loggedString = inputFile.getName()+",FAILED,";
			if (e.getCause() !=null) {
				loggedString += e.getCause();
			} else {
				loggedString += e;
			}
			logger.error(loggedString, e);
			writeFileEntry(outputDirectory.getAbsolutePath(), loggedString, jobLogFileName, bWriteLogFiles);
		} finally {
			logger.debug("Import of `" + inputFile.getName() +"` completed");
		}
	}
}
