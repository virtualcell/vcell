package org.vcell.sbml.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;
import org.vcell.util.exe.Executable;

import cbit.util.xml.VCLogger;
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


public class VCellSedMLSolver {

	static String inString = "/usr/local/app/vcell/simulation";
	static String outRootString = "/usr/local/app/vcell/simulation/out";

	public static void main(String[] args) {

		// place the sedml file and the sbml file(s) in inDir directory
		
		File inDir = new File(inString);
		File outRootDir = new File(outRootString);
		
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
		
		File[] directoryListing = inDir.listFiles();
		if (directoryListing == null) {
			System.err.println("inDir not a directory");
			System.exit(99);
		}
		
		File sedmlFile = null;
		for (File aFile : directoryListing) {		// look for a sedml file by extension
			if(aFile.isDirectory()) {
				continue;
			}
			String aFileName = aFile.getName();
			if(!aFileName.contains(".")) {
				continue;
			}
			int end = aFileName.indexOf(".");
			String aExtension = aFileName.substring(end);
			if(aExtension == null) {
				continue;
			}
			if(aExtension.toLowerCase().contentEquals(".sedml")) {
				sedmlFile = aFile;
				break;
			}
		}
		if(sedmlFile == null) {
			System.err.println("no sedml file found");
			System.exit(99);
		}
		
		try {
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
		} finally {
		}
		System.out.println("done");
	}

	// everything is done here
	public void doWork(ExternalDocInfo externalDocInfo, AbstractTask sedmlTask, SedML sedml) throws Exception {

		// create the VCDocument (bioModel + application + simulation), do sanity checks
		cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
		VCDocument doc = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, sedmlTask);
		sanityCheck(doc);
		
		// create the work directory for this task, invoke the solver
		String docName = doc.getName();
		String outString = outRootString + "/" + docName + "_" + sedmlTask.getId();
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
		File cvodeOutputFile = new File(outDir, docName + SimDataConstants.IDA_DATA_EXTENSION);
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

	private class LocalLogger extends VCLogger {
		@Override
		public void sendMessage(Priority p, ErrorType et, String message) throws Exception {
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
