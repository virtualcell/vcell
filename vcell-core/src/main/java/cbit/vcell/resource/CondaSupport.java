package cbit.vcell.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;

public class CondaSupport {
		
	public static enum PythonPackage {
		COPASI	("COPASI python binding",	"python-copasi",	"fbergmann",		"COPASI"),
		THRIFT	("apache thrift",			"thrift",			"conda-forge",		"thrift"),
		VTK		("Visualization Toolkit",	"vtk",				"conda-forge",		"vtk"),
		LIBSBML	("libSBML for python",		"python-libsbml",	"sbmlteam",			"libsbml");
		
		final String description;
		final String condaName;
		final String condaRepo;
		final String pythonModuleName;
		
		private PythonPackage(String description, String condaName, String condaRepo, String pythonModuleName){
			this.description = description;
			this.condaName = condaName;
			this.condaRepo = condaRepo;
			this.pythonModuleName = pythonModuleName;
		}
	}
	
	public static class AnacondaInstallation {
		public final File installDir;
		public final File pythonExe;
		public final File condaExe;
		
		public AnacondaInstallation(File installDir, File pythonExe, File condaExe) {
			this.installDir = installDir;
			this.pythonExe = pythonExe;
			this.condaExe = condaExe;
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
		// if anaconda installation provided via System properties, then assume it is ok
		//
		File providedAnacondaInstallDir = VCellConfiguration.getFileProperty(PropertyLoader.anacondaInstallDir);
		if (providedAnacondaInstallDir!=null){
			AnacondaInstallation installation = getAnacondaInstallation(providedAnacondaInstallDir);
			return installation.pythonExe;
		}
		throw new RuntimeException("managed python not yet installed or verified, "
				+ "CondaSupport.verifyInstall() should be called before first use of managed python installation");
	}
	
	private static String minicondaWeb = "https://repo.continuum.io/miniconda/";
	private static String win64py27 = "Miniconda2-latest-Windows-x86_64.exe";
	private static String osx64py27 = "Miniconda2-latest-MacOSX-x86_64.sh";
	private static String lin64py27 = "Miniconda2-latest-Linux-x86_64.sh";

	
	public static void installInBackground() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					boolean bForceDownload = false;
					boolean bForceInstallPython = false;
					boolean bForceInstallPackages = false;
					
					verifyInstall(bForceDownload, bForceInstallPython, bForceInstallPackages);
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		};
		Thread pythonInstallThread = new Thread(runnable,"Python Install Thread");
		pythonInstallThread.setDaemon(true);
		pythonInstallThread.start();
	}
	
	public static File getAnacondaInstallDir(){
		//
		// first check the provided anaconda installation
		//
		File anacondaInstallDir = VCellConfiguration.getFileProperty(PropertyLoader.anacondaInstallDir);
		if (anacondaInstallDir!=null){
			if (!anacondaInstallDir.exists()){
				throw new RuntimeException("provided python anaconda/miniconda installation directory '"+anacondaInstallDir.getAbsolutePath()+"' not found");
			}
			if (!anacondaInstallDir.isDirectory()){
				throw new RuntimeException("provided python anaconda/miniconda installation directory '"+anacondaInstallDir.getAbsolutePath()+"' is not a directory");
			}
		}
		
		if (anacondaInstallDir == null){
			File homeDir = ResourceUtil.getVcellHome();
			anacondaInstallDir = new File(homeDir,"Miniconda");
		}
		return anacondaInstallDir;
	}
	
	private static AnacondaInstallation getAnacondaInstallation(File anacondaInstallDir){
		File pythonExeFile = null;
		File condaExeFile = null;
		OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();
		if (operatingSystemInfo.isWindows()){
			pythonExeFile = new File(anacondaInstallDir,"python.exe");
			condaExeFile = new File(anacondaInstallDir,"Scripts/conda.exe");
		}else if (operatingSystemInfo.isLinux()){
			pythonExeFile = new File(anacondaInstallDir,"bin/python");
			condaExeFile = new File(anacondaInstallDir+"bin/conda");
		}else if (operatingSystemInfo.isMac()){
			pythonExeFile = new File(anacondaInstallDir,"bin/python");
			condaExeFile = new File(anacondaInstallDir,"bin/conda");
		}else{
			throw new RuntimeException("local miniconda python installation now supported on this platform");
		}
		
		return new AnacondaInstallation(anacondaInstallDir, pythonExeFile, condaExeFile);
	}
	
	public static synchronized void verifyInstall(boolean bForceDownload, boolean bForceInstallPython, boolean bForceInstallPackages) throws IOException {
		isInstallingOrVerifying = true;

		for (PythonPackage pkg : PythonPackage.values()){
			getPackageStatusMap().put(pkg, InstallStatus.INITIALIZING);
		}

		try {
			//
			// if anaconda directory not specified using vcell.anaconda.installdir property, then use a managed Miniconda installation
			//
			final AnacondaInstallation pythonInstallation;
			File providedAnacondaDir = VCellConfiguration.getFileProperty(PropertyLoader.anacondaInstallDir);
			File managedMinicondaInstallDir = new File(ResourceUtil.getVcellHome(),"Miniconda");
			if (providedAnacondaDir == null){
				/*
				 *	download from here:  https://conda.io/miniconda.html
				 */
				final File downloadDir = new File(ResourceUtil.getVcellHome(),"download");
				final File archive;
				final String[] installMinicondaCommand;
				
				/*
				 * Windows
				 * 			/InstallationType=AllUsers
				 * 			/AddToPath=[0|1], default: 1
				 * 			/RegisterPython=[0|1], make this the system’s default Python, default: 0 (Just me), 1 (All users)
				 * 			/S (silent) must be followed by installation path (last argument)
				 * 			start /wait "" Miniconda4-latest-Windows-x86_64.exe /InstallationType=JustMe /RegisterPython=0 /S /D=%UserProfile%\Miniconda3
				 * 
				 * Linux, OS X
				 * 			look here:  https://conda.io/docs/help/silent.html
				 */
				OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();
				if (operatingSystemInfo.isWindows()){
					if (operatingSystemInfo.is64bit()){
						archive = new File(downloadDir,win64py27);
						installMinicondaCommand = new String[] { archive.getAbsolutePath(), "/InstallationType=JustMe", "/AddToPath=0", "/RegisterPython=0", "/S", "/D="+managedMinicondaInstallDir.getAbsolutePath() };
					}else{
						throw new RuntimeException("python installation not yet supported on 32 bit Windows");
					}
				}else if (operatingSystemInfo.isLinux()){
					if (operatingSystemInfo.is64bit()){
						archive = new File(downloadDir,lin64py27);
						installMinicondaCommand = new String[] { "bash", archive.getAbsolutePath(), "-b", "-f", "-p", managedMinicondaInstallDir.getAbsolutePath() };
					}else{
						throw new RuntimeException("python installation not yet supported on 32 bit Linux");
					}
				}else if (operatingSystemInfo.isMac()){
					archive = new File(downloadDir,osx64py27);
					installMinicondaCommand = new String[] { "bash", archive.getAbsolutePath(), "-b", "-f", "-p", managedMinicondaInstallDir.getAbsolutePath() };
				}else{
					throw new RuntimeException("python installation now supported on this platform");
				}
	
	
				//
				// verify installation of managed Miniconda - install if necessary
				//
				
				//
				// Step 1: check if miniconda python installation already exists
				//
				AnacondaInstallation managedMiniconda = getAnacondaInstallation(managedMinicondaInstallDir);
				boolean bPythonExists = false;
				if (managedMiniconda.pythonExe.exists()){
					String[] cmd = new String[] {managedMiniconda.pythonExe.getAbsolutePath(), "--version"};
					try {
						boolean ret = checkPython(cmd);
						if (ret){
							bPythonExists = true;
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				
				//
				// Step 2: download installer archive if doesn't exist
				//
				URL url = new URL(minicondaWeb + archive.getName());
				if (!bPythonExists || !archive.exists() || bForceDownload) {
					FileUtils.copyURLToFile(url, archive);		// download the archive
					if (operatingSystemInfo.isLinux() || operatingSystemInfo.isMac()){
						archive.setExecutable(true);
					}
				}
				
				//
				// Step 3: install a managed Miniconda 
				//
				if(!bPythonExists || bForceInstallPython) {
					// if python is not present or not desired version, install it
					installMiniconda(installMinicondaCommand);
				}
				
				pythonInstallation = getAnacondaInstallation(managedMinicondaInstallDir);
			}else{
				pythonInstallation = getAnacondaInstallation(providedAnacondaDir);
			}
			
			//
			// verify python installation (either provided or managed)
			//
			if (pythonInstallation.pythonExe.exists()){
				String[] cmd = new String[] {pythonInstallation.pythonExe.getAbsolutePath(), "--version"};
				try {
					if (!checkPython(cmd)){
						throw new RuntimeException("python installation "+pythonInstallation.pythonExe.getAbsolutePath()+" could not be verified");
					}
				}catch (Exception e){
					e.printStackTrace();
					throw new RuntimeException("failure while verifying python installation "+pythonInstallation.pythonExe.getAbsolutePath()+": "+e.getMessage(),e);
				}
			}
			
			verifiedPythonExe = pythonInstallation.pythonExe;

			//
			// check packages early and set status so that dont have to wait until all install/verify before use.
			//
			for (PythonPackage pkg : PythonPackage.values()){
				boolean bFound = checkPackage(pythonInstallation.pythonExe,pkg);
				if (bFound){
					getPackageStatusMap().put(pkg, InstallStatus.INSTALLED);
				}
			}
			
			//
			// check/install packages
			//
			for (PythonPackage pkg : PythonPackage.values()){
				InstallStatus status = getPackageStatusMap().get(pkg);
				if (status != InstallStatus.INSTALLED || bForceInstallPackages){
					getPackageStatusMap().put(pkg, InstallStatus.INITIALIZING);
					removePackage(pythonInstallation.condaExe,pkg);
					installPackage(pythonInstallation.condaExe,pkg);
					boolean bFound = checkPackage(pythonInstallation.pythonExe,pkg);
					if (bFound){
						getPackageStatusMap().put(pkg, InstallStatus.INSTALLED);
					}else{
						getPackageStatusMap().put(pkg, InstallStatus.FAILED);
					}
				}
			}						
		} finally {
			isInstallingOrVerifying = false;
		}
	}
	
	private static boolean checkPython(String[] cmd) {
		IExecutable exe = new Executable(cmd);
		try {
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println("stdout=\""+exe.getStdoutString()+"\"");
			System.out.println("stderr=\""+exe.getStderrString()+"\"");
			if (exe.getExitValue() != 0){
				throw new RuntimeException("Python test failed with return code "+exe.getExitValue()+": "+exe.getStderrString());
			}
			if(!exe.getStderrString().contains("Continuum Analytics, Inc")) {
				throw new RuntimeException("Wrong python version present :" + exe.getStderrString());
			} else {
				return true;
			}
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("Python test invocation failed: " + e.getMessage(), e);
		}
	}
	
	private static boolean checkPackage(File condaExe, PythonPackage pythonPackage) {
		String[] cmd;
		if (OperatingSystemInfo.getInstance().isWindows()){
			cmd = new String[] { condaExe.getAbsolutePath(), "-c", "'import "+pythonPackage.pythonModuleName+"'"};
		}else{
			cmd = new String[] { "bash", "-c", condaExe.getAbsolutePath()+" -c  'import "+pythonPackage.pythonModuleName+"'"};
		}
		System.out.println(Arrays.asList(cmd).toString());
		IExecutable exe = new Executable(cmd);
		try {
			System.out.println("checking package "+pythonPackage.condaName);
			exe.start( new int[] { 0 });
//			System.out.println("Exit value: " + exe.getExitValue());
//			System.out.println(exe.getStdoutString());
//			System.out.println(exe.getStderrString());
			return true;
		} catch (ExecutableException e) {
//			e.printStackTrace();
			return false;
		}
	}
		
	private static void installMiniconda(String[] cmd) {
		IExecutable exe = new Executable(cmd);
		try {
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());

		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("Python installation failed: " + e.getMessage(), e);
		}
	}

	private static void installPackage(File condaExe, PythonPackage pythonPackage) {
		String[] cmd = new String[] { condaExe.getAbsolutePath(), "install", "-y", "-c", pythonPackage.condaRepo, pythonPackage.condaName };
		IExecutable exe = new Executable(cmd);
		try {
			System.out.println("installing package "+pythonPackage.condaName);
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to install python package "+pythonPackage.condaName+": "+e.getMessage(), e);
		}
	}
		
	private static void removePackage(File condaExe, PythonPackage pythonPackage) {
		String[] cmd = new String[] { condaExe.getAbsolutePath(), "remove", "-y", "-v", pythonPackage.condaName };
		IExecutable exe = new Executable(cmd);
		try {
			System.out.println("removing package "+pythonPackage.condaName);
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());
		} catch (ExecutableException e) {
			e.printStackTrace();
			//throw new RuntimeException("Failed to remove python package "+packageName+": "+e.getMessage(), e);
		}
	}
		
	public static void main(String[] args){
		try {
			PropertyLoader.loadProperties();
			
			try {
				getPythonExe();
			}catch (Exception e){
				System.out.println("failed before verification ... this is supposed to happen");
				e.printStackTrace(System.out);
			}
			
			boolean bForceDownload = false;
			boolean bForceInstallPython = false;
			boolean bForceInstallPackages = false;
			
			verifyInstall(bForceDownload, bForceInstallPython, bForceInstallPackages);
			
			System.out.println("verified Python = " + getPythonExe());
		
		}catch (IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
