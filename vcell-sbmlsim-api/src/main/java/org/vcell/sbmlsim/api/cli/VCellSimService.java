package org.vcell.sbmlsim.api.cli;

import java.io.File;

import org.vcell.sbmlsim.api.common.MeshInfo;
import org.vcell.sbmlsim.api.common.SBMLModel;
import org.vcell.sbmlsim.api.common.SimData;
import org.vcell.sbmlsim.api.common.SimSubmitResult;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationStatus;
import org.vcell.sbmlsim.api.common.TimePoints;
import org.vcell.sbmlsim.api.common.VariableInfo;
import org.vcell.sbmlsim.api.common.VariableList;
import org.vcell.sbmlsim.api.common.VcmlToSbmlResults;

import com.google.gson.Gson;

public class VCellSimService {
	
	enum CLI_TYPE {
		bash,
		powershell,
		unknown
	}
	
	public final static CLI_TYPE cliType;
	public final static String scriptArgDashes;
	public final static String scriptExtension;
	
	static {
		final String system_osname = System.getProperty("os.name");
		final String system_osarch = System.getProperty("os.arch");
		boolean b64bit = system_osarch.endsWith("64");
		boolean bWindows = system_osname.contains("Windows");
		boolean bMac = system_osname.contains("Mac");
		boolean bLinux = system_osname.contains("Linux");
		if (b64bit && bWindows) {
			cliType = CLI_TYPE.powershell;
			scriptArgDashes = "-";
			scriptExtension = ".ps1";
		}else if (b64bit && (bMac || bLinux)) {
			cliType = CLI_TYPE.bash;
			scriptArgDashes = "--";
			scriptExtension = ".sh";
		}else {
			cliType = CLI_TYPE.unknown;
			scriptArgDashes = "";
			scriptExtension = "";
		}
	}
	
	
	
	public final static String PROPERTY_VCELLSIM_INSTALLDIR = "vcell.sbmlsim.installdir";
	
	private File command() throws ExecutableException {
		switch (cliType) {
			case powershell:
			case bash:
				break;
			default:
				throw new ExecutableException("unable to determine compatable Operating System type");
		}
		String installdirProperty = System.getProperty(PROPERTY_VCELLSIM_INSTALLDIR,"../vcell-sbmlsim");
		if (installdirProperty == null || !new File(installdirProperty).exists() || !new File(installdirProperty).isDirectory()) {
			throw new ExecutableException("system property "+PROPERTY_VCELLSIM_INSTALLDIR+" '"+installdirProperty+"' not set or is not a directory, expecting installation directory of VCell SBML Simulators");
		}
		File scriptsDir = new File(new File(installdirProperty),"scripts");
		if (!scriptsDir.exists() || !scriptsDir.isDirectory()) {
			throw new ExecutableException("vcell simulation command '"+scriptsDir.getAbsolutePath()+"' not found or is not a file, set system property "+PROPERTY_VCELLSIM_INSTALLDIR+" not VCell SBML Solver installation directory, scripts subdirectory not found");
		}
		File scriptFile = new File(scriptsDir,"cli"+scriptExtension);
		if (scriptFile.exists() && scriptFile.isFile()) {
			return scriptFile;
		}else {
			throw new ExecutableException("vcell simulation command '"+scriptFile.getAbsolutePath()+"' not found or is not a file, set system property "+PROPERTY_VCELLSIM_INSTALLDIR+" not VCell SBML Solver installation directory");
		}
	}
	
	public MeshInfo getMeshInfo(SimulationHandle simHandle) throws ExecutableException {
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"simhandle", Long.toString(simHandle.remoteId), 
				"meshinfo" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		Gson gson = new Gson();
		MeshInfo meshInfo = gson.fromJson(exe.getStdoutString(), MeshInfo.class);
		return meshInfo;
	}

	public SimulationHandle submit(SBMLModel model, SimulationSpec simSpec) throws ExecutableException {
		Gson gson = new Gson();
		String simSpecJson = gson.toJson(simSpec);
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(),
				scriptArgDashes+"sbmlfile", model.getFilepath().getAbsolutePath(), 
				scriptArgDashes+"simspec", simSpecJson,
				"submit" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		SimSubmitResult simSubmitResult = gson.fromJson(exe.getStdoutString(), SimSubmitResult.class);
		return simSubmitResult.getSimulationHandle();
	}

	public SimData getSimData(SimulationHandle simHandle, VariableInfo varInfo, int timeIndex) throws ExecutableException {
		Gson gson = new Gson();
		String varInfoJson = gson.toJson(varInfo);
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"simhandle", Long.toString(simHandle.remoteId), 
				scriptArgDashes+"timeindex", Integer.toString(timeIndex),  
				scriptArgDashes+"varinfo", varInfoJson,
				"simdata" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		SimData simData = gson.fromJson(exe.getStdoutString(), SimData.class);
		return simData;
	}

	public SimulationStatus getStatus(SimulationHandle simHandle) throws ExecutableException {
		Gson gson = new Gson();
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"simhandle", Long.toString(simHandle.remoteId), 
				"status" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		SimulationStatus simStatus = gson.fromJson(exe.getStdoutString(), SimulationStatus.class);
		return simStatus;
	}

	public TimePoints getTimePoints(SimulationHandle simHandle) throws ExecutableException {
		Gson gson = new Gson();
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"simhandle", Long.toString(simHandle.remoteId), 
				"timepoints" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		TimePoints timePoints = gson.fromJson(exe.getStdoutString(), TimePoints.class);
		return timePoints;
	}

	public VariableList getVariableList(SimulationHandle simHandle) throws ExecutableException {
		Gson gson = new Gson();
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"simhandle", Long.toString(simHandle.remoteId), 
				"varlist" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		VariableList varList = gson.fromJson(exe.getStdoutString(), VariableList.class);
		return varList;
	}

	public File getSBML(File vcmlInput, String applicationName, File sbmlOutput) throws ExecutableException {
		Gson gson = new Gson();
		Executable exe = new Executable(new String[] { 
				command().getAbsolutePath(), 
				scriptArgDashes+"vcmlfile", vcmlInput.getAbsolutePath(), 
				scriptArgDashes+"sbmlfile", sbmlOutput.getAbsolutePath(),
				"tosbml" });
		int[] expectedReturnCodes = new int[] { 0 };
		exe.start(expectedReturnCodes);
		VcmlToSbmlResults vcmlToSbmlResults = gson.fromJson(exe.getStdoutString(), VcmlToSbmlResults.class);
		return new File(vcmlToSbmlResults.getSbmlFilePath());
	}

}
