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
import java.util.StringTokenizer;
//import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;
import org.vcell.util.Executable;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.gui.ExecutableFinderDialog;
import org.vcell.vis.vtk.VisMeshUtils;

import cbit.vcell.client.pyvcellproxy.SimulationDataSetRef;

public class VisitSupport {

	public static final String VISIT_EXEC_NAME = "visit";
	
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

	public static String getVisitManualFindMessage(String visitURL,String executableName){
		//https://wci.llnl.gov/codes/visit/
		return "If VisIt is installed (from "+visitURL+") but not in the system path, then press press '"+ExecutableFinderDialog.FIND+"' and navigate to '"+executableName+"'.\nElse please install VisIt, restart VCell, and try again";

	}
	
	public static File getVisToolPythonScriptDirectory()
	{
		return new File(ResourceUtil.getVCellInstall(),"visTool");
	}
	public static File getVisToolPythonScript()
	{
		File visToolScriptDir = getVisToolPythonScriptDirectory();
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

	  
	  
	  private static void launchVisToolMac(File visitExecutable) throws IOException, URISyntaxException, InterruptedException {
		
		  if(visitExecutable == null){
			  File visitExecutableRoot = null;
			  List<File> visitList = doSearch(new String[] {"mdfind", "kMDItemContentType==com.apple.application-bundle", "&&","kMDItemFSName=='"+VISIT_EXEC_NAME+".app'c"});
			  if(visitList != null && visitList.size() == 1){
				  visitExecutableRoot = visitList.get(0);
			  }
			  if(visitExecutableRoot == null){
				  visitExecutableRoot = ResourceUtil.getExecutable(VISIT_EXEC_NAME,false);
			  }
			  visitExecutable = new File(visitExecutableRoot,"/Contents/Resources/bin/visit");
		  }
		  if (visitExecutable==null || !visitExecutable.exists() || !visitExecutable.isFile()){
			  throw new IOException("visit executable not found");
		  }
		  VCellConfiguration.setFileProperty(PropertyLoader.visitExe, visitExecutable);
		  
		File visMainCLI = getVisToolPythonScript();
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
	public static void launchVisToolLinux(File visitExecutable) throws ExecutableException,IOException{
		File mpirun = null;
		if(visitExecutable == null){
			  File userDir = new File(System.getProperty("user.home"));
			  System.out.println(userDir.getAbsolutePath()+" "+userDir.exists());
			  //find -L ~/ -name 'visit' -executable -type f -print
			  //'find -L /home/frm -name 'visit' -executable -type f -print'
			  Executable exec =
				new Executable(new String[] {"/bin/sh","-c","find -L "+userDir.getAbsolutePath()+" -maxdepth 4 -name 'visit' -executable -type f -print"});
//			  exec.setWorkingDir(userDir);
			  exec.start();
			  System.out.println(exec.getExitValue());
			  System.out.println(exec.getStdoutString());
				//		  System.out.println(exec.getStderrString());
			  if(exec.getExitValue() == 0){
				  visitExecutable = new File(exec.getStdoutString().trim());
			  }
			  
			  //look for mpirun starting 2 levels above visit executable dir location
			  exec =
				new Executable(new String[] {"/bin/sh","-c","find -L "+visitExecutable.getParentFile().getParent()+" -maxdepth 6 -name 'mpirun' -executable -type f -print"});
			  exec.start();
			  System.out.println(exec.getExitValue());
			  System.out.println(exec.getStdoutString());
			  System.err.println(exec.getStderrString());
				//		  System.out.println(exec.getStderrString());
			  if(exec.getExitValue() == 0){
				  String mpiStr = exec.getStdoutString();
				  if(mpiStr != null){
					  mpiStr = mpiStr.trim();
				  }
				  if(mpiStr != null && mpiStr.length() != 0){
					  mpirun = new File(mpiStr);
				  }else{
					  mpirun = null;
				  }
			  }
			  if(mpirun == null){
				  //See if any mpi run installed
				  exec = new Executable(new String[] {"/bin/sh","-c","which mpirun"});
				  exec.start();
				  System.out.println(exec.getExitValue());
				  System.out.println(exec.getStdoutString());
				  System.out.println(exec.getStderrString());
				  if(exec.getExitValue() == 0){
					  String mpiStr = exec.getStdoutString();
					  if(mpiStr != null){
						  mpiStr = mpiStr.trim();
					  }
					  if(mpiStr != null && mpiStr.length() != 0){
						  mpirun = new File(mpiStr);
					  }else{
						  mpirun = null;
					  }
				  }
			  }
		}
		System.out.println(visitExecutable.getAbsolutePath());
		if(visitExecutable == null || !visitExecutable.exists() || !visitExecutable.isFile()){
			throw new IOException("visit executable not found");
		}
		
		VCellConfiguration.setFileProperty(PropertyLoader.visitExe, visitExecutable);
		
		System.out.println(mpirun.getAbsolutePath());
		if(mpirun == null || !mpirun.exists() || !mpirun.isFile()){
			throw new IOException("mpirun executable not found");
		}
		File visMainCLI = getVisToolPythonScript();
		if (!visMainCLI.exists() || !visMainCLI.isFile()){
			throw new IOException("vcell/visit main python file not found, "+visMainCLI.getAbsolutePath());
		}
		
		File scriptFile = File.createTempFile("VCellVisitLaunch", ".command");
		System.out.println("Launch script location="+scriptFile.getAbsolutePath());
		Executable exec = new Executable(new String[] {/*"/bin/sh","-c",*/scriptFile.getAbsolutePath() });
		//
		// get existing environment variables and add visit command and python script to it.
		//
		Map<String,String> envVariables = System.getenv();
		ArrayList<String> envVarList = new ArrayList<String>();
		for (String varname : envVariables.keySet()){
			String value = envVariables.get(varname);
			if(varname.equals("PATH")){
				value = value+":"+visitExecutable.getParent();
			}
//			envVarList.add(varname+"="+value);
			exec.addEnvironmentVariable(varname, value);
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
		scriptFile.setExecutable(true);
		BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
	
//		export PATH="$HOME/opt/bin:$PATH"
		System.out.println("export PATH=\"$PATH:"+mpirun.getParent()+"\"\n");
		writer.append("export PATH=\"$PATH:"+mpirun.getParent()+"\"\n");
		System.out.println(visitCommandString+" -cli -uifile "+pythonScriptString+"\n");
		writer.append(visitCommandString+" -cli -uifile "+pythonScriptString+"\n");
		writer.close();

		//cli
		//mdserver
		//engine_ser
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					exec.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();

//		exec.start();
//		System.out.println(exec.getExitValue());
//		System.out.println(exec.getStdoutString());
//		System.out.println(exec.getStderrString());
		
//		Runtime.getRuntime().exec(
//				new String[] {/*"/bin/sh","-c",*/scriptFile.getAbsolutePath() },
//				envVarList.toArray(new String[0]));
		if (lg.isInfoEnabled()) {
			lg.info("Started VCellVisIt");
		}

	}
	public static void launchVisTool(File visitExecutable) throws IOException, ExecutableException, URISyntaxException, InterruptedException
	{
		launchVisTool(visitExecutable, null);
	}
	
	public static void launchVisTool(File visitExecutable, SimulationDataSetRef simulationDataSetRef) throws IOException, ExecutableException, URISyntaxException, InterruptedException
	{
		if (OperatingSystemInfo.getInstance().isMac()) {
			launchVisToolMac(visitExecutable);
			return;
		}else if (OperatingSystemInfo.getInstance().isLinux()) {
			launchVisToolLinux(visitExecutable);
			return;
		}
		//MSWindows
		if(visitExecutable == null){
			
			visitExecutable = ResourceUtil.getExecutable(VISIT_EXEC_NAME,false);
			
			if (visitExecutable==null || !visitExecutable.exists() || !visitExecutable.isFile()){
				throw new IOException("visit executable not found, "+visitExecutable.getAbsolutePath());
			}
		}
		
		VCellConfiguration.setFileProperty(PropertyLoader.visitExe, visitExecutable);
		
		File visMainCLI =           getVisToolPythonScript();
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
			
			envVarList.add("visitcmd="+/*"\""+*/visitExecutable.getPath()/*+"\""*/);
			envVarList.add("pythonscript="+vcellHomeVisMainCLI.getPath().replace("\\", "/"));
			//envVarList.add("pythonscript="+visMainCLI.toURI().toASCIIString());
			
			final String visEx = visitExecutable.getPath().replace("\\", "/");
			final String[] cmdStringArray = new String[] {"cmd", "/K","start","\"\"" ,visitExecutable.getPath(),"-cli" ,"-uifile", vcellHomeVisMainCLI.getPath().replace("\\", "/")};
			if (simulationDataSetRef!=null){
				File simDataSetRefFile = new File(ResourceUtil.getLocalVisDataDir(),"SimID_"+simulationDataSetRef.getSimId()+"_"+simulationDataSetRef.getJobIndex()+"_.simref");
				VisMeshUtils.writeSimulationDataSetRef(simDataSetRefFile, simulationDataSetRef);
				envVarList.add("INITIALSIMDATAREFFILE="+simDataSetRefFile.getPath().replace("\\","/"));
			}
			
			new Thread(new Runnable() {
				@Override
				public void run() {

					BufferedWriter writer = null;
					String location = getVisToolPythonScriptDirectory().getAbsolutePath();

					try {
					String visitBatchName = location + "\\visit.bat";
						
					File visitBatchFile = new File(visitBatchName);
//					System.out.println(visitBatchFile.getCanonicalPath());
					writer = new BufferedWriter(new FileWriter(visitBatchFile));
						
					for(String var:envVarList) {
						StringTokenizer st2 = new StringTokenizer(var, "=");
						writer.write("set " + st2.nextToken() + "=" + st2.nextToken() + "\n");
						writer.newLine();
					}
					
					String visPath = visEx.substring(0, visEx.lastIndexOf("/"));
					String visName = visEx.substring(visEx.lastIndexOf("/")+1);
					writer.write("cd " + visPath);
					writer.newLine();
					writer.write(visName + " -cli -uifile " + vcellHomeVisMainCLI.getPath().replace("\\", "/"));
//					writer.write("notepad.exe");
					
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						try {
							writer.close();
						} catch (Exception e) {
						}
					}
					String command = "cscript " + location + "\\visitscript.vbs \"" + location + "\"";
					try {
						Process p = Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			if (lg.isInfoEnabled()) {
				lg.info("Started VCellVisIt");
			}
			
			
//			@SuppressWarnings("unused")
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try{
//					Executable exec = new Executable(cmdStringArray);
////					Executable exec = new Executable(new String[] {st});
//					for(String var:envVarList){
//						StringTokenizer st2 = new StringTokenizer(var, "=");
//						exec.addEnvironmentVariable(st2.nextToken(), st2.nextToken());
//					}
//					exec.start();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}).start();
//			if (lg.isInfoEnabled()) {
//				lg.info("Started VCellVisIt");
//			}
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

	public static void main(String[] args) throws IOException {
/* myscript.vbs
Set objShell = WScript.CreateObject("WScript.Shell")
objShell.Run("mybatch.bat"), 0, True
 */
//		try {
			String command = "cscript c:\\TEMP\\aaa\\myscript.vbs";
			Process p = Runtime.getRuntime().exec(command);
//			p.waitFor();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		System.out.println("Done!");
	}
	// start "" fullPath/file.exe
}