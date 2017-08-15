package cbit.vcell.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.vcell.util.exe.Executable2;
import org.vcell.util.exe.ExecutableException;

public class CondaSupport {
	
	private static String minicondaWeb = "https://repo.continuum.io/miniconda/";
	private static String win64py27 = "Miniconda2-latest-Windows-x86_64.exe";
	private static String osx64py27 = "Miniconda2-latest-MacOSX-x86_64.sh";
	private static String lin64py27 = "Miniconda2-latest-Linux-x86_64.sh";

	
	public static void doWork() {
		if(OperatingSystemInfo.getInstance().isWindows()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Process p;
					try {
						/*
						 *	download from here:  https://conda.io/miniconda.html
						 */
						String homeDir = ResourceUtil.getVcellHome().getAbsolutePath();			// C:\Users\vasilescu\.vcell
						String minicondaArchive = homeDir + "\\download\\" + win64py27;

						URL url = new URL(minicondaWeb + win64py27);
						File archive = new File(minicondaArchive);
						if(!archive.exists()) {
							FileUtils.copyURLToFile(url, archive);		// download the archive
						}
						
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
						
						String python = homeDir + "\\Miniconda\\python.exe";
						String[] cmd = new String[] {python, "--version"};
						boolean ret = isPythonPresent(cmd);
						if(!ret) {
							// if python is not present or not desired version, install it
							String arguments = "/InstallationType=JustMe /RegisterPython=0 /S /D=";
							p = Runtime.getRuntime().exec(minicondaArchive + " " + arguments + homeDir + "\\Miniconda");
							BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
							System.out.println("a "+in.readLine());		// returns null
							VCellConfiguration.setFileProperty(PropertyLoader.pythonExe, new File(homeDir + "\\Miniconda\\python.exe"));

							// install the packages we want
							python = homeDir + "\\Miniconda\\python.exe";
							String location = ResourceUtil.getVCellInstall().getAbsolutePath() + "\\visTool\\inspectEnv.py";
							cmd = new String[] {python, location};
							installPackages(cmd);
						}

					}  catch(Exception e) {
						System.err.println("Exception occurred while trying to configure python support");
						e.printStackTrace(System.out);
					}
				}
			}).start();
		} else if(OperatingSystemInfo.getInstance( ).isMac()) {
				
				
				
		} else if(OperatingSystemInfo.getInstance( ).isLinux()) {
				
				
				
				
		}
	}
	
	private static boolean isPythonPresent(String[] cmd) {
		Executable2 exe = new Executable2(cmd);
		try {
			exe.start( new int[] { 0 });
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
	
	private static void installPackages(String[] cmd) {
		Executable2 exe = new Executable2(cmd);
		try {
			exe.start( new int[] { 0 });
			System.out.println("Exit value: " + exe.getExitValue());
			System.out.println(exe.getStdoutString());
			System.out.println(exe.getStderrString());

		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("Python inspect environment invocation failed: " + e.getMessage(), e);
		}
	}

}
