package org.vcell.cli.vcml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.simulation.Simulation;
import cbit.vcell.xml.XmlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.OmexPythonUtils;
import org.vcell.sedml.*;
import org.vcell.util.document.BioModelInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
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
