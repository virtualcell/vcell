package org.vcell.sbml.test;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;
import org.vcell.util.exe.Executable;

import org.sbml.libcombine.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.ode.CVodeFileWriter;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.stoch.StochFileWriter;
import cbit.vcell.solvers.FunctionFileGenerator;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

@Deprecated
public class VCellSedMLSolver {
	static String OUT_ROOT_STRING = "";
	static String IN_ROOT_STRING = "";

	// static String inString = "/usr/local/app/vcell/simulation";
	// static String outRootString = "/usr/local/app/vcell/simulation/out";

	public static void main(String[] args) {

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		// place the sedml file and the sbml file(s) in inDir directory
		Options options = getCommandLineOptions();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
			if (cmd.getOptions().length == 0) {
				System.out.println("usage: vcell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]");
				System.exit(1);
			}
			if (args[0].contains("-h") || args[0].contains("--help")) {
				formatter.printHelp("vcell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]",
						"\nBioSimulators-compliant command-line interface to the VCell simulation program <http://vcell.org>.\n\n" +
								"optional arguments:\n\n",
						options,
						"");
				System.exit(1);
			}
			IN_ROOT_STRING = cmd.getOptionValue("archive");
			OUT_ROOT_STRING = cmd.getOptionValue("out-dir");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
            System.exit(1);
		}

		if (IN_ROOT_STRING == null || OUT_ROOT_STRING == null) {
			formatter.printHelp("vcell", options);
			System.exit(1);
		}

//		CombineArchive omex = new CombineArchive();
//		boolean isInitialized = omex.initializeFromArchive(IN_ROOT_STRING);
//		boolean isExtracted = omex.extractTo(tempDir)

		File tempDir = Files.createTempDir();
		ArrayList<String> sedmlLocations = new ArrayList<>();
		try {

			System.loadLibrary("combinej");
			CombineArchive omex = new CombineArchive();
			boolean isInitialized = omex.initializeFromArchive(IN_ROOT_STRING);
			boolean isExtracted = omex.extractTo(tempDir.getAbsolutePath());
			CaOmexManifest manifest = omex.getManifest();
			CaListOfContents contents = manifest.getListOfContents();
			System.out.println("Contents fetched");
			for (int contentIndex = 0; contentIndex < contents.getNumContents(); contentIndex++) {
				CaContent content = (CaContent) contents.get(contentIndex);
				if (content.isFormat("sedml")) {
					sedmlLocations.add(content.getLocation());
				}
			}
			System.out.println("All SEDML locations fetched");
//			unzip(IN_ROOT_STRING, tempDir);
		} catch(Exception ex) {
			System.err.println("Cannot extract Omex");
			System.exit(1);
		}




		File inDir = tempDir;
		File outRootDir = new File(OUT_ROOT_STRING);

		//Scan directory for SEDMl
		// Run the current look with all tasks inside per SEDML
		// Rename the direcotry with task name instead of prefix+taskname
		//---
		
		// delete the output directory and all its content recursively
		if(outRootDir.exists()) {
			try {
				deleteRecursively(outRootDir);
			} catch (IOException e) {
				System.err.println("Failed to empty outRootDir.");
				System.exit(99);
			}
		}
		if(!outRootDir.exists()) {
			outRootDir.mkdirs();
		}
		
//		File[] directoryListing = inDir.listFiles();
//		if (directoryListing == null) {
//			System.err.println("Error while accessing temp directory");
//			System.exit(99);
//		}
//		
//		File sedmlFile = null;
//		for (File aFile : directoryListing) {		// look for a sedml file by extension
//			if(aFile.isDirectory()) {
//				continue;
//			}
//			String aFileName = aFile.getName();
//			if(!aFileName.contains(".")) {
//				continue;
//			}
//			int end = aFileName.indexOf(".");
//			String aExtension = aFileName.substring(end);
//			if(aExtension == null) {
//				continue;
//			}
//			if(aExtension.toLowerCase().contentEquals(".sedml")) {
//				sedmlFile = aFile;
//				break;
//			}
//		}
//		if(sedmlFile == null) {
//			System.err.println("no sedml file found");
//			System.exit(99);
//		}
		for (int sedmlIndex = 0; sedmlIndex < sedmlLocations.size(); sedmlIndex++) {
			try {
				String completeSedmlPath = tempDir.getAbsolutePath() + "/" + sedmlLocations.get(sedmlIndex);
				File sedmlFile = new File(completeSedmlPath);
				SedML sedml = Libsedml.readDocument(sedmlFile).getSedMLModel();
				if (sedml == null || sedml.getModels().isEmpty()) {
					System.err.println("the sedml file '" + sedmlFile.getName() + "'does not contain a valid document");
					System.exit(99);
				}
				VCellSedMLSolver vCellSedMLSolver = new VCellSedMLSolver();
				ExternalDocInfo externalDocInfo = new ExternalDocInfo(sedmlFile, true);
				for(AbstractTask at : sedml.getTasks()) {
					vCellSedMLSolver.doWork(externalDocInfo, at, sedml);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		
//		try {
//			SedML sedml = Libsedml.readDocument(sedmlFile).getSedMLModel();
//			if (sedml == null || sedml.getModels().isEmpty()) {
//				System.err.println("the sedml file '" + sedmlFile.getName() + "'does not contain a valid document");
//				System.exit(99);
//			}
//			VCellSedMLSolver vCellSedMLSolver = new VCellSedMLSolver();
//			ExternalDocInfo externalDocInfo = new ExternalDocInfo(sedmlFile, true);
//			for(AbstractTask at : sedml.getTasks()) {
//				vCellSedMLSolver.doWork(externalDocInfo, at, sedml);
//			}
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//		} finally {
			deleteDirectory(tempDir);
//		}

	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}


	public static void unzip(String archivePath, File destDir) throws IOException{
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
	
	private static Options getCommandLineOptions() {
		Option help = new Option("h",
				"help",
				false,
				"show this help message and exit");

		Option quiet = new Option("q",
				"quiet",
				false,
				"suppress all console output");

		Option input = new Option("i",
				"archive",
				true,
				"Path to OMEX file which contains one or more SED-ML-encoded simulation experiments");

		Option output = new Option("o",
				"out-dir",
				true,
				"Directory to save outputs");

		Option version = new Option("v",
				"version",
				false,
				"show program's version number and exit");

		Options options = new Options();

		options.addOption(help);
		options.addOption(quiet);
		options.addOption(input);
		options.addOption(output);
		options.addOption(version);
		return options;
	}

	// everything is done here
	public void doWork(ExternalDocInfo externalDocInfo, AbstractTask sedmlTask, SedML sedml) throws Exception {

		// create the VCDocument (bioModel + application + simulation), do sanity checks
		cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
		List<AbstractTask> tasks = new ArrayList<AbstractTask>();
		tasks.add(sedmlTask);
		List<VCDocument> docs = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, tasks, null, false);
		VCDocument doc = docs.get(0);
		sanityCheck(doc);
		
		// create the work directory for this task, invoke the solver
		String docName = doc.getName();
		String outString = VCellSedMLSolver.OUT_ROOT_STRING + "/" + docName + "/" + sedmlTask.getId();
		File outDir = new File(outString);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		
		BioModel bioModel = (BioModel)doc;
		SimulationContext simContext = bioModel.getSimulationContext(0);
		MathDescription mathDesc = simContext.getMathDescription();
		String vcml = mathDesc.getVCML();
		try (PrintWriter pw = new PrintWriter(outString + "/vcmlTrace.xml")) {
			pw.println(vcml);
		}
		
		Simulation sim = bioModel.getSimulation(0);
		SolverTaskDescription std = sim.getSolverTaskDescription();
		SolverDescription sd = std.getSolverDescription();
		String kisao = sd.getKisao();
		if(SolverDescription.CVODE.getKisao().contentEquals(kisao)) {
			ODESolverResultSet odeSolverResultSet = solveCvode(outDir, bioModel);
			System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
		} else if(SolverDescription.StochGibson.getKisao().contentEquals(kisao)) {
			ODESolverResultSet odeSolverResultSet = solveGibson(outDir, bioModel);
			System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
		} else if(SolverDescription.IDA.getKisao().contentEquals(kisao)) {
			ODESolverResultSet odeSolverResultSet = solveIDA(outDir, bioModel);
			System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
		} else {
			System.out.println("Unsupported solver: " + kisao);
		}
		System.out.println("-------------------------------------------------------------------------");
	}
	
	private static ODESolverResultSet solveCvode(File outDir, BioModel bioModel) throws Exception {
		String docName = bioModel.getName();
		Simulation sim = bioModel.getSimulation(0);
		File cvodeInputFile = new File(outDir, docName + SimDataConstants.CVODEINPUT_DATA_EXTENSION);
		PrintWriter cvodePW = new java.io.PrintWriter(cvodeInputFile);
		SimulationJob simJob = new SimulationJob(sim, 0, null);
		SimulationTask simTask = new SimulationTask(simJob, 0); 
		CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(cvodePW, simTask);
		cvodeFileWriter.write();
		cvodePW.close();
		// use the cvodeStandalone solver
		String outDirPath = outDir.getAbsolutePath();
		int indexOfLastSlash = outDirPath.lastIndexOf("/");
		String task_name = outDirPath.substring(indexOfLastSlash + 1);
		String idaFilePath = outDirPath.substring(0, indexOfLastSlash);
		File cvodeOutputFile = new File(idaFilePath, task_name + SimDataConstants.IDA_DATA_EXTENSION);
		String executableName = null;
		try {
			// we need to specify the vCell install dir in the Eclipse Debug configuration, as VM argument 
			// so that the code next knows where to look for the solver
			// -Dvcell.installDir=G:\dan\jprojects\git\vcell
			// OR
			// just type the string with the full path explicitly
			// OR
			// provide a .properties file in the working directory
			executableName = SolverUtilities.getExes(SolverDescription.CVODE)[0].getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("failed to get executable for solver "+SolverDescription.CVODE.getDisplayLabel()+": "+e.getMessage(),e);
		}
		Executable executable = new Executable(new String[]{executableName, cvodeInputFile.getAbsolutePath(), cvodeOutputFile.getAbsolutePath()});
		executable.start();
		ODESolverResultSet odeSolverResultSet = VCellSBMLSolver.getODESolverResultSet(simJob, cvodeOutputFile.getPath());
		return odeSolverResultSet;
	}
	private static ODESolverResultSet solveIDA(File outDir, BioModel bioModel) throws Exception {
		String docName = bioModel.getName();
		Simulation sim = bioModel.getSimulation(0);
		File idaInputFile = new File(outDir, docName + SimDataConstants.IDAINPUT_DATA_EXTENSION);
		PrintWriter idaPW = new java.io.PrintWriter(idaInputFile);
		SimulationJob simJob = new SimulationJob(sim, 0, null);
		SimulationTask simTask = new SimulationTask(simJob, 0);
	    IDAFileWriter idaFileWriter = new IDAFileWriter(idaPW, simTask);
	    idaFileWriter.write();
		idaPW.close();
		// use the idastandalone solver
		File idaOutputFile = new File(outDir, docName + SimDataConstants.IDA_DATA_EXTENSION);
		String executableName = null;
		try {
			executableName = SolverUtilities.getExes(SolverDescription.IDA)[0].getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("failed to get executable for solver "+SolverDescription.IDA.getDisplayLabel()+": "+e.getMessage(),e);
		}
		Executable executable = new Executable(new String[]{executableName, idaInputFile.getAbsolutePath(), idaOutputFile.getAbsolutePath()});
		executable.start();
		ODESolverResultSet odeSolverResultSet = VCellSBMLSolver.getODESolverResultSet(simJob, idaOutputFile.getPath());
		return odeSolverResultSet;
	}
	private static ODESolverResultSet solveGibson(File outDir, BioModel bioModel) throws Exception {
		String docName = bioModel.getName();
		Simulation sim = bioModel.getSimulation(0);
		File gibsonInputFile = new File(outDir, docName + SimDataConstants.STOCHINPUT_DATA_EXTENSION);
		PrintWriter gibsonPW = new java.io.PrintWriter(gibsonInputFile);
		SimulationJob simJob = new SimulationJob(sim, 0, null);
		SimulationTask simTask = new SimulationTask(simJob, 0);
		StochFileWriter stFileWriter = new StochFileWriter(gibsonPW, simTask, false);
		stFileWriter.write();
		gibsonPW.close();
		File gibsonOutputFile = new File(outDir, docName + SimDataConstants.IDA_DATA_EXTENSION);
		
		writeFunctionFile(outDir, docName, SimDataConstants.FUNCTIONFILE_EXTENSION, simTask);
		
		String executableName = null;
		try {
		executableName = SolverUtilities.getExes(SolverDescription.StochGibson)[0].getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("failed to get executable for solver "+SolverDescription.StochGibson.getDisplayLabel()+": "+e.getMessage(),e);
		}
		Executable executable = new Executable(new String[]{executableName, "gibson", gibsonInputFile.getAbsolutePath(), gibsonOutputFile.getAbsolutePath()});
		executable.start();
		ODESolverResultSet odeSolverResultSet = VCellSBMLSolver.getODESolverResultSet(simJob, gibsonOutputFile.getPath());
		return odeSolverResultSet;
	}

	// ---------------------------------------------------------------------- Utilities --------------------------------------
	private static void sanityCheck(VCDocument doc) {
		if(doc == null) {
			throw new RuntimeException("Imported VCDocument is null.");
		}
		String docName = doc.getName();
		if(docName == null || docName.isEmpty()) {
			throw new RuntimeException("The name of the imported VCDocument is null or empty.");
		}
		if(!(doc instanceof BioModel)) {
			throw new RuntimeException("The imported VCDocument '" + docName + "' is not a BioModel.");
		}
		BioModel bioModel = (BioModel)doc;
		if(bioModel.getSimulationContext(0) == null) {
			throw new RuntimeException("The imported VCDocument '" + docName + "' has no Application");
		}
		if(bioModel.getSimulation(0) == null) {
			throw new RuntimeException("The imported VCDocument '" + docName + "' has no Simulation");
		}
	}
	
	private static void deleteRecursively(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				deleteRecursively(c);
			}
		}
		if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + f);
		}
	}
	
	// needed for gibson
	private static void writeFunctionFile(File outDir, String docName, String ext, SimulationTask simTask) {
		String functionFileName = getBaseName(outDir.getAbsolutePath(), docName) + ext;
		Vector<AnnotatedFunction> funcList = createFunctionList(simTask);
		FunctionFileGenerator functionFileGenerator = new FunctionFileGenerator(functionFileName, funcList);

		try {
			functionFileGenerator.generateFunctionFile();		
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error creating .function file for "+functionFileGenerator.getBasefileName()+e.getMessage(),e);
		}		
	}
	private static Vector<AnnotatedFunction> createFunctionList(SimulationTask simTask) {
		try {
			return simTask.getSimulationJob().getSimulationSymbolTable().createAnnotatedFunctionsList(simTask.getSimulation().getMathDescription());
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Simulation '"+simTask.getSimulationInfo().getName()+"', error createFunctionList(): "+e.getMessage(),e);
		}
	}
	private static String getBaseName(String outDir, String docName) {
		String baseName = outDir + "/" + docName;
		return baseName;
	}

	private static HashMap<String, String> processArgs(String[] args) {
		HashMap<String, String> argMap = new HashMap<>();
		return argMap;
	}

	private class LocalLogger extends VCLogger {
		@Override
		public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
			System.out.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
			if (p==VCLogger.Priority.HighPriority) {
				SBMLImportException.Category cat = SBMLImportException.Category.UNSPECIFIED;
				if (message.contains(SBMLImporter.RESERVED_SPATIAL) ) {
					cat = SBMLImportException.Category.RESERVED_SPATIAL;
				}
				throw new SBMLImportException(message,cat);
			}
		}
		public void sendAllMessages() {
		}
		public boolean hasMessages() {
		return false;
		}
	};



}
