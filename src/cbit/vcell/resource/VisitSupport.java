package cbit.vcell.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;

import cbit.vcell.resource.ResourceUtil.ExecutableFinder;

public class VisitSupport {

	public static class VisitFolderFileFilter implements FileFilter {

		ArrayList<String> excludeList = null;
		
		VisitFolderFileFilter(){
			excludeList = new ArrayList<String>();
			excludeList.add("env");
		}
		
		@Override
		public boolean accept(File pathname) {
			if (excludeList.contains(pathname.getName())) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	private static final Logger lg = Logger.getLogger(VisitSupport.class);
	public static final String visitUserMessage = "If VisIt is installed (from https://wci.llnl.gov/codes/visit/) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";

	
	public static File getVisToolPythonScript()
	{
		File visToolScriptDir = new File(ResourceUtil.getVCellInstall(),"visTool");
		File visToolScript = new File(visToolScriptDir, "visMainCLI.py");
		return visToolScript;
	}

	public static File getVisToolShellScript() throws URISyntaxException
	{
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String vcellvisitScript = null;
		if (osi.isWindows()) {
			vcellvisitScript = "vcellvisit.cmd";
		}else{
			vcellvisitScript = "vcellvisit.sh";
		}
		//java.net.URL url = ResourceUtil.class.getResource(ResourceUtil.RES_PACKAGE + "/" + vcellvisitScript);
		java.net.URL url = ResourceUtil.class.getResource(osi.getResourcePackage() + vcellvisitScript);
		return new File(url.toURI());
	}

	  private static List<File> doSearch(String command[]) throws IOException{
	    Process process = Runtime.getRuntime().exec(command);
	    BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    ArrayList<File> results = new ArrayList<File>();
	    String line;
	    while ((line = out.readLine()) != null)
	      results.add(new File(line));
	    return results;
	  }

	  public static Map<String,String> getMetadata(File file) throws IOException{
	    Process process = Runtime.getRuntime().exec(new String[] {"mdls", file.getAbsolutePath()});
	    BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    HashMap<String,String> results = new HashMap<String,String>();
	    String line;
	    while ((line = out.readLine()) != null)
	    {
	      int equals = line.indexOf('=');
	      if (equals > -1)
	      {
	        String key = line.substring(0, equals).trim();
	        String value = line.substring(equals+1).trim();
	        results.put(key, value);
	      }
	    }
	    return results;
	  }

	  
	  
	  private static void launchVisToolMac(ExecutableFinder gef) throws IOException, URISyntaxException, BackingStoreException {
		
		String vname = "visit";
		File visitExecutableRoot = null;
		//mdfind "kMDItemContentType==com.apple.application-bundle && kMDItemFSName=='visit.app'c"
		List<File> visitList = doSearch(new String[] {"mdfind", "kMDItemContentType==com.apple.application-bundle", "&&","kMDItemFSName=='"+vname+".app'c"});
		if(visitList != null && visitList.size() == 1){
			visitExecutableRoot = visitList.get(0);
		}
//		if(true){return;}
		
		File visMainCLI = getVisToolPythonScript();

//		File visitExecutableRoot = ResourceUtil.getExecutable(vname,false,gef);
		if(visitExecutableRoot == null){
			visitExecutableRoot = ResourceUtil.getExecutable(vname,false,gef);
		}
		
		File visitExecutable = null;
		visitExecutable = new File(visitExecutableRoot,"/Contents/Resources/bin/visit");
		
		if (visitExecutable==null || !visitExecutable.exists() || !visitExecutable.isFile()){
			throw new IOException("visit executable not found, "+visitExecutable.getAbsolutePath());
		}
		if (!visMainCLI.exists() || !visMainCLI.isFile()){
			throw new IOException("vcell/visit main python file not found, "+visMainCLI.getAbsolutePath());
		}
		
		//
		// get existing environment variables and add visit command and python script to it.
		//
		Map<String,String> envVariables = System.getenv();
		ArrayList<String> envVarList = new ArrayList<String>();
		for (String varname : envVariables.keySet()){
			String value = envVariables.get(varname);
			envVarList.add(varname+"="+value);
			System.out.println(varname+"="+value);
		}
		
		String visitCommandString = visitExecutable.getPath().replace("\"", "");
		
		System.out.println("Visit Command String = "+visitCommandString);
		//envVarList.add(visitCommandString);
		if (lg.isInfoEnabled()) {
			lg.info("visitcmd="+visitExecutable.getPath().replace("\"", ""));
		}
		//envVarList.add("pythonscript="+visMainCLI.getPath());
		String pythonScriptString = visMainCLI.getPath();
		System.out.println("Python script = "+pythonScriptString);
		File scriptFile = File.createTempFile("VCellVisitLaunch", ".command");

		scriptFile.setExecutable(true);
		BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
	
		writer.write(visitCommandString+" -cli -uifile "+pythonScriptString+"\n");
		writer.close();
		
		Runtime.getRuntime().exec(
				new String[] {"open", "-a", "Terminal.app", scriptFile.getAbsolutePath() },
				envVarList.toArray(new String[0]));
		if (lg.isInfoEnabled()) {
			lg.info("Started VCellVisIt");
		}
	}
	
	public static void launchVisTool(ExecutableFinder executableFinder) throws IOException, ExecutableException, URISyntaxException, BackingStoreException
	{
		
		if (OperatingSystemInfo.getInstance().isMac()) {
			launchVisToolMac(executableFinder);
			return;
		}
		
			File script = 				getVisToolShellScript();
			File visMainCLI =           getVisToolPythonScript();
			File visitExecutable = null;
			
			String vname = "visit";
			//String msg = "<html>If VisIt is installed (from <a href=\"https://wci.llnl.gov/codes/visit/\">https://wci.llnl.gov/codes/visit/</a>) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";
			visitExecutable = ResourceUtil.getExecutable(vname,false,executableFinder);
			
			if (!script.exists() || !script.isFile()){
				throw new IOException("script not found, "+script.getAbsolutePath()); 
			}
			if (visitExecutable==null || !visitExecutable.exists() || !visitExecutable.isFile()){
				throw new IOException("visit executable not found, "+visitExecutable.getAbsolutePath());
			}
			if (!visMainCLI.exists() || !visMainCLI.isFile()){
				throw new IOException("vcell/visit main python file not found, "+visMainCLI.getAbsolutePath());
			}
			if (lg.isInfoEnabled()) {
				lg.info("Starting VCellVisIt as a sub-process");
			}
			//
			// get existing environment variables and add visit command and python script to it.
			//
			Map<String,String> envVariables = System.getenv();
			ArrayList<String> envVarList = new ArrayList<String>();
			for (String varname : envVariables.keySet()){
				String value = envVariables.get(varname);
				envVarList.add(varname+"="+value);
			}
			
			String siteName = ResourceUtil.getSiteName();
			File vcellHomeVisFolder = new File(new File(ResourceUtil.getVcellHome(), "Vis"),siteName);
			if (!vcellHomeVisFolder.exists()){
				if (!vcellHomeVisFolder.mkdirs()){
					throw new IOException("Cannot create directory "+vcellHomeVisFolder.getCanonicalPath());
				}
			}
			
			
			
			
			//FileUtils.copyDirectoryShallow(visMainCLI.getParentFile(), vcellHomeVisFolder);
			FileUtils.copyDirectory(visMainCLI.getParentFile(), vcellHomeVisFolder, true, new VisitFolderFileFilter());
			File vcellHomeVisMainCLI=new File(vcellHomeVisFolder, visMainCLI.getName());

			vcellHomeVisMainCLI.setExecutable(true);
			
			
			envVarList.add("visitcmd="+visitExecutable.getPath());
			envVarList.add("pythonscript="+vcellHomeVisMainCLI.getPath().replace("\\", "/"));
			//envVarList.add("pythonscript="+visMainCLI.toURI().toASCIIString());
			String st = "\""+script.getCanonicalPath()+"\"";
			System.out.println(st);
			

			
			String[] cmdStringArray = new String[] {"cmd", "/K", "start", "\""+"\"", st};
			@SuppressWarnings("unused")			
			Process process = Runtime.getRuntime().exec(
					cmdStringArray, 
					envVarList.toArray(new String[0]));
			if (lg.isInfoEnabled()) {
				lg.info("Started VCellVisIt");
			}
			/*
			List<String> cmds = new ArrayList<String>();
			cmds.add(visitExecutable.getAbsolutePath());
			cmds.add("-cli");
			cmds.add("-ufile");
			cmds.add(script.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder(cmds);
			final Process p = pb.start();
			Runnable mon = new Runnable() {
				
				@Override
				public void run() {
					try {
						System.err.println("waiting for visit");
						p.waitFor();
						int rcode = p.exitValue();
						System.err.println("visit exit code " + rcode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			new Thread(mon).start();
			*/
		}

}