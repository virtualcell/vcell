package cbit.vcell.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;

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
						if(true) {		// some verification here if we have a good installation?
							
							String arguments = "/InstallationType=JustMe /RegisterPython=0 /S /D=";
							p = Runtime.getRuntime().exec(minicondaArchive + " " + arguments + homeDir + "\\Miniconda");
							BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
							System.out.println("a "+in.readLine());		// returns null
							VCellConfiguration.setFileProperty(PropertyLoader.pythonExe, new File(homeDir + "\\Miniconda\\python.exe"));
						}
															
						/*
						 * Check packages now that we have conda installed properly
						 */
						String python = homeDir + "\\Miniconda\\python.exe";
						String location = ResourceUtil.getVCellInstall().getAbsolutePath() + "\\visTool\\inspectEnv.py";
						p = Runtime.getRuntime().exec(python + " " + location);
						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
						System.out.println("1 "+in.readLine());		// null means uninstalled
						System.out.println("2 "+in.readLine());
						System.out.println("3 "+in.readLine());
						System.out.println("4 "+in.readLine());
						
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

}
