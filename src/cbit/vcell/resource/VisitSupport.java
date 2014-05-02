package cbit.vcell.resource;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;
import org.vcell.util.ExecutableException;
import org.vcell.util.gui.ExecutableFinderDialog;

public class VisitSupport {

	private static final Logger lg = Logger.getLogger(VisitSupport.class);
	private static final String visitUserMessage = "If VisIt is installed (from https://wci.llnl.gov/codes/visit/) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";

	
	public static File getVisToolPythonScript()
	{
		File visToolScriptDir = new File(ResourceUtil.getVCellInstall(),"visTool");
		File visToolScript = new File(visToolScriptDir, "visMainCLI.py");
		return visToolScript;
	}

	public static File getVisToolShellScript() throws URISyntaxException
	{
		String vcellvisitScript = null;
		if (ResourceUtil.bWindows){
			vcellvisitScript = "vcellvisit.cmd";
		}else{
			vcellvisitScript = "vcellvisit.sh";
		}
		java.net.URL url = ResourceUtil.class.getResource(ResourceUtil.RES_PACKAGE + "/" + vcellvisitScript);
		return new File(url.toURI());
	}


	private static void launchVisToolMac(Component parent) throws IOException, URISyntaxException, BackingStoreException {
		
		File visMainCLI = getVisToolPythonScript();
		File visitExecutable = null;

		String vname = "visit";
		
		ExecutableFinderDialog gef = new ExecutableFinderDialog(parent, visitUserMessage); 
		File visitExecutableRoot = ResourceUtil.getExecutable(vname,false,gef);
		
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
	
	public static void launchVisTool(Component parent) throws IOException, ExecutableException, URISyntaxException, BackingStoreException
	{
		
		if (ResourceUtil.bMac) {
			launchVisToolMac(parent);
			return;
		}
		
			File script = 				getVisToolShellScript();
			File visMainCLI =           getVisToolPythonScript();
			File visitExecutable = null;
			
			String vname = "visit";
			//String msg = "<html>If VisIt is installed (from <a href=\"https://wci.llnl.gov/codes/visit/\">https://wci.llnl.gov/codes/visit/</a>) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";
			ExecutableFinderDialog gef = new ExecutableFinderDialog(parent, visitUserMessage); 
			visitExecutable = ResourceUtil.getExecutable(vname,false,gef);
			
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
			envVarList.add("visitcmd=\""+visitExecutable.getPath()+"\"");
			envVarList.add("pythonscript="+visMainCLI.getPath().replace("\\", "/"));
			
			@SuppressWarnings("unused")
			Process process = Runtime.getRuntime().exec(
					new String[] {"cmd", "/C", "start", script.getPath()}, 
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