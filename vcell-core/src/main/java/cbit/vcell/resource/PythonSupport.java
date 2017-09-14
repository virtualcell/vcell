package cbit.vcell.resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;

public class PythonSupport {
		
	public static enum PythonPackage {
		COPASI	("COPASI python binding",	"python-copasi",	"fbergmann",		"COPASI"),
		THRIFT	("apache thrift",			"thrift",			"conda-forge",		"thrift"),
		VTK		("Visualization Toolkit",	"vtk",				"conda-forge",		"vtk"),
		LIBSBML	("libSBML for python",		"python-libsbml",	"sbmlteam",			"libsbml");
		
		public final String description;
		public final String condaName;
		public final String condaRepo;
		public final String pythonModuleName;
		
		private PythonPackage(String description, String condaName, String condaRepo, String pythonModuleName){
			this.description = description;
			this.condaName = condaName;
			this.condaRepo = condaRepo;
			this.pythonModuleName = pythonModuleName;
		}
	}
	
	public static enum InstallStatus {
		INITIALIZING,
		INSTALLED,
		FAILED
	}
		
	private static HashMap<PythonPackage,InstallStatus> packageStatusMap = null;
	private static File verifiedPythonExe = null;
	private static boolean isInstallingOrVerifying = false;
	
	private static Map<PythonPackage, InstallStatus> getPackageStatusMap(){
		if (packageStatusMap==null){
			packageStatusMap = new HashMap<PythonPackage, InstallStatus>();
			for (PythonPackage pkg : PythonPackage.values()){
				packageStatusMap.put(pkg, InstallStatus.INITIALIZING);
			}
		}
		return packageStatusMap;
	}
	
	public static InstallStatus getPythonPackageStatus(PythonPackage pythonPackage){
		return getPackageStatusMap().get(pythonPackage);
	}
	
	public static File getPythonExe(){
		if (verifiedPythonExe!=null){
			return verifiedPythonExe;
		}
		if (isInstallingOrVerifying){
			throw new RuntimeException("Python installation/verification in progress, please try again later");
		}
		//
		// if pythonExe provided via System properties, then assume it is ok
		//
		File pythonExe = VCellConfiguration.getFileProperty(PropertyLoader.pythonExe);
		if (pythonExe!=null){
			return pythonExe;
		}
		throw new RuntimeException("python not yet installed or verified, "
				+ "CondaSupport.verifyInstall() should be called before first use of python installation");
	}
	
	
	public static synchronized void verifyInstallation() throws IOException {
		isInstallingOrVerifying = true;

		try {
			for (PythonPackage pkg : PythonPackage.values()){
				getPackageStatusMap().put(pkg, InstallStatus.INITIALIZING);
			}
			File pythonExe = null;
			String pythonExeString = PropertyLoader.getProperty(PropertyLoader.pythonExe,null);
			if (pythonExeString!=null && pythonExeString.length()>0){
				pythonExe = new File(pythonExeString);
			}
			if (pythonExe == null){
				pythonExe = VCellConfiguration.getFileProperty(PropertyLoader.pythonExe);
			}
			if (pythonExe != null){
				// check if miniconda python installation already exists
				boolean bPythonExists = false;
				if (pythonExe.exists()) {
					boolean ret = checkPython(pythonExe);
					if (ret) {
						bPythonExists = true;
					}
				}
					
				if(!bPythonExists) {
					//	We are just verifying. Since it's missing, we produce a good message and exit
					throw new RuntimeException("The vCell python component is missing. To get access to full vCell features we recommend installing it");
				}
				verifiedPythonExe = pythonExe;
	
				// check packages early and set status so that dont have to wait until all install/verify before use.
				boolean packageInstallFailed = false;
				int count = 0;
				String failedPackageName = "";
				for (PythonPackage pkg : PythonPackage.values()) {
					try {
						boolean bFound = checkPackage(pythonExe,pkg);
						if (bFound) {
							getPackageStatusMap().put(pkg, InstallStatus.INSTALLED);
						} else {
							getPackageStatusMap().put(pkg, InstallStatus.FAILED);
							packageInstallFailed = true;
							if(count > 0) {
								failedPackageName += ",";
							}
							failedPackageName += " " + pkg.condaName;
							count++;
						}
					} catch(Exception e) {
						packageInstallFailed = true;
						if(count > 0) {
							failedPackageName += ",";
						}
						failedPackageName += " " + pkg.condaName;
						count++;
					}
				}
				if(packageInstallFailed) {
					throw new RuntimeException("The following package(s) are missing:" + failedPackageName + ".");
				}
			} else {
				throw new RuntimeException("No python provided, please install python and set directory in File->preferences");
			}
		}finally{
			isInstallingOrVerifying = false;
		}
	}
	
	
	private static boolean checkPython(File pythonExe) {
		String[] cmd;
		if (OperatingSystemInfo.getInstance().isWindows()){
			//cmd = new String[] {"cmd", "/C", managedMiniconda.pythonExe.getAbsolutePath(), "--version"};
			cmd = new String[] {pythonExe.getAbsolutePath(), "--version"};
		}else{
			cmd = new String[] {pythonExe.getAbsolutePath(), "--version"};
		}
		IExecutable exe = new Executable(cmd);
		try {
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println("stdout=\""+exe.getStdoutString()+"\"");
			System.out.println("stderr=\""+exe.getStderrString()+"\"");
			if (exe.getExitValue() != 0){
				throw new RuntimeException("Python test failed with return code "+exe.getExitValue()+": "+exe.getStderrString());
			}
//			if(!exe.getStderrString().contains("Continuum Analytics, Inc") && !exe.getStderrString().contains("Anaconda")) {
//				throw new RuntimeException("Wrong python version present :" + exe.getStderrString());
//			} else {
//				return true;
//			}
			return true;
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("Python test invocation failed: " + e.getMessage(), e);
		}
	}
	
	private static boolean checkPackage(File pythonExe, PythonPackage pythonPackage) {
		String[] cmd;
		if (OperatingSystemInfo.getInstance().isWindows()){
			//cmd = new String[] { "cmd", "/C", condaExe.getAbsolutePath(), "-c", "'import "+pythonPackage.pythonModuleName+"'"};
			cmd = new String[] { pythonExe.getAbsolutePath(), "-c", "\"import "+pythonPackage.pythonModuleName+"\""};
		}else{
			cmd = new String[] { "bash", "-c", pythonExe.getAbsolutePath()+" -c  'import "+pythonPackage.pythonModuleName+"'"};
		}
		System.out.println(Arrays.asList(cmd).toString());
		IExecutable exe = new Executable(cmd);
		try {
			System.out.println("checking package "+pythonPackage.condaName);
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());
			return true;
		} catch (ExecutableException e) {
//			e.printStackTrace();
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());
			return false;
		}
	}
		
		
	public static void main(String[] args){
		try {
			PropertyLoader.loadProperties();
			verifyInstallation();
			try {
				getPythonExe();
			}catch (Exception e){
				System.out.println("failed before verification ... this is supposed to happen");
				e.printStackTrace(System.out);
			}
			
			System.out.println("verified Python = " + getPythonExe());
		
		}catch (IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
