package cbit.vcell.resource;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vcell.util.ExecutableException;
import org.vcell.util.gui.ExecutableFinderDialog;

public class VisitSupport {

	public static File getVisToolPythonScript()
	{
		File visToolScriptDir = new File(ResourceUtil.getVCellInstall(),"visTool");
		File visToolScript = new File(visToolScriptDir, "visMainCLI.py");
		return visToolScript;
	}

	public static File getVisToolShellScript()
	{
		String vcellvisitScript = null;
		if (ResourceUtil.bWindows){
			vcellvisitScript = "vcellvisit.cmd";
		}else{
			vcellvisitScript = "vcellvisit.sh";
		}
		java.net.URL url = ResourceUtil.class.getResource(ResourceUtil.RES_PACKAGE + "/" + vcellvisitScript);
		return new File(url.getFile());
	}

	public static <E> void launchVisTool(Component parent) throws IOException, ExecutableException
	{
		File script = 				getVisToolShellScript();
		File visMainCLI =           getVisToolPythonScript();
		File visitExecutable = null;
		
		String vname = "visit";
		//String msg = "<html>If VisIt is installed (from <a href=\"https://wci.llnl.gov/codes/visit/\">https://wci.llnl.gov/codes/visit/</a>) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";
		String msg = "If VisIt is installed (from https://wci.llnl.gov/codes/visit/) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again";
		ExecutableFinderDialog gef = new ExecutableFinderDialog(parent, msg); 
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
		
		System.out.println("Starting VCellVisIt as a sub-process");
		
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
		
		/*
		@SuppressWarnings("unused")
		Process process = Runtime.getRuntime().exec(
				new String[] {"cmd", "/K", "start", script.getPath()}, 
				envVarList.toArray(new String[0]));
		
		System.out.println("Started VCellVisIt");
		*/
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
	}

}
