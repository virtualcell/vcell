package org.vcell.optimization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.exe.Executable2;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;
import org.vcell.vis.vtk.VtkService;

import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.PythonSupport.InstallStatus;
import cbit.vcell.resource.PythonSupport.PythonPackage;
import cbit.vcell.resource.ResourceUtil;

public class CopasiServicePython {
	
	public static CopasiServicePython copasiService = null;
	protected static final Logger lg = LogManager.getLogger(VtkService.class);


	public static void runCopasiPython(File copasiOptProblemFile, File copasiResultsFile) throws IOException {
		//It's 2015 -- forward slash works for all operating systems
		File PYTHON = PythonSupport.getPythonExe();
		InstallStatus copasiInstallStatus = PythonSupport.getPythonPackageStatus(PythonPackage.COPASI);
		if (copasiInstallStatus==InstallStatus.FAILED){
			throw new RuntimeException("failed to install COPASI python package, consider re-installing VCell-managed python\n ...see Preferences->Python->Re-install");
		}
		if (copasiInstallStatus==InstallStatus.INITIALIZING){
			throw new RuntimeException("VCell is currently installing or verifying the COPASI python package ... please try again in a minute");
		}
		File vcellOptDir = ResourceUtil.getVCellOptPythonDir();
		File optServicePythonFile = new File(vcellOptDir,"optService.py");
		if (PYTHON==null || !PYTHON.exists()){
			throw new RuntimeException("python executable not specified, set python location in VCell menu File->Preferences...->Python Properties");
		}
		String[] cmd = new String[] { PYTHON.getAbsolutePath(),optServicePythonFile.getAbsolutePath(),copasiOptProblemFile.getAbsolutePath(), copasiResultsFile.getAbsolutePath()};
		IExecutable exe = prepareExecutable(cmd);
		try {
			exe.start( new int[] { 0 });
			if (exe.getExitValue() != 0){
				throw new RuntimeException("copasi python solver (optService.py) failed with return code "+exe.getExitValue()+": "+exe.getStderrString());
			}
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("optService.py invocation failed: "+e.getMessage(),e);
		}
	}

	private static IExecutable prepareExecutable(String[] cmd) {
		if (lg.isInfoEnabled()) {
			lg.info("python command string:" + StringUtils.join(cmd," "));
		}
		System.out.println("python command string:" + StringUtils.join(cmd," "));
		Executable2 exe = new Executable2(cmd);
		return exe;
	}
}
