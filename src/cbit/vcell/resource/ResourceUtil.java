/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.resource;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFileChooser;

import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SimpleUserMessage;
import org.vcell.util.gui.VCFileChooser;

import com.ibm.icu.util.StringTokenizer;

import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverExecutable;
import cbit.vcell.util.NativeLoader;

public class ResourceUtil {
	private static final String system_osname = System.getProperty("os.name");
	private final static String system_osarch = System.getProperty("os.arch");
	private final static boolean b64bit = system_osarch.endsWith("64");
	public final static boolean bWindows = system_osname.contains("Windows");
	public final static boolean bMac = system_osname.contains("Mac");
	public final static boolean bLinux = system_osname.contains("Linux");

	public static enum JavaVersion  {FIVE,SIX,SEVEN};

	// temporary : until a more permanent, robust solution is thought out for running vcell locally.
	private static String lastUserLocalDir = null;


	private final static String osname_prefix;
	public final static String EXE_SUFFIX;
	public final static String NATIVELIB_SUFFIX;
	public final static String RES_PACKAGE; 
	/**
	 * name of native library directory (leaf only)
	 */
	public final static String NATIVE_LIB_DIR; 
	static {
		String BIT_SUFFIX = "";
		if (b64bit) {
			BIT_SUFFIX ="_x64";
		}
		if (bWindows) {
			osname_prefix = "win";
			EXE_SUFFIX = BIT_SUFFIX + ".exe";
			NativeLoader.setOsType(NativeLoader.OsType.WINDOWS);
		} else if (bMac) {
			osname_prefix = "mac";
			EXE_SUFFIX = BIT_SUFFIX;
			NativeLoader.setOsType(NativeLoader.OsType.MAC);
		} else if (bLinux) {
			osname_prefix = "linux";
			EXE_SUFFIX = BIT_SUFFIX;
			NativeLoader.setOsType(NativeLoader.OsType.LINUX);
		} else {
			throw new RuntimeException(system_osname + " is not supported by the Virtual Cell.");
		}
		NATIVE_LIB_DIR =   osname_prefix + (b64bit ? "64" : "32");
		RES_PACKAGE = "/cbit/vcell/resource/" + NATIVE_LIB_DIR; 
		NATIVELIB_SUFFIX = BIT_SUFFIX;
	}
	//public final static String EXE_SUFFIX = bMacPpc ? "_ppc" : (b64bit ? "_x64" : "") + (bWindows ? ".exe" : "");
	//public final static String NATIVELIB_SUFFIX = b64bit ? "_x64" : (bMacPpc ? "_ppc" : "");

	private static File userHome = null;
	private static File vcellHome = null;
	private static File localSimDir = null;
	private static File localRootDir = null;

	private static File solversDirectory = null;

	private static boolean windowsDllsLoaded = false;
	private static boolean nativeLoaded = false; 
	/**
	 * @param basename name of executable without path or os specific extension
	 * @param firstLoad is the first exe loaded?
	 * @return executable
	 * @throws IOException
	 */
	private static File loadExecutable(String basename) throws IOException {
		String name = basename + EXE_SUFFIX;
		String res = RES_PACKAGE + "/" + name;
		File exe = new java.io.File(getSolversDirectory(), name);
		if (!exe.exists()) {
			ResourceUtil.writeFileFromResource(res, exe);
		}
		if (bWindows && !windowsDllsLoaded) {
			String requiredDlls[] = {
					null, //slot for glut dll
					"cyggcc_s-seh-1.dll",
					"cygstdc++-6.dll",
					"cyggfortran-3.dll",
					"cygquadmath-0.dll"
			};
			if (b64bit){
				requiredDlls[0] = "glut64.dll";
			}else{
				requiredDlls[0] = "glut32.dll";
			}

			for (String dllName : requiredDlls){
				String RES_DLL = RES_PACKAGE + "/" + dllName;
				File file_dll = new java.io.File(getSolversDirectory(), dllName);
				if (!file_dll.exists()) {
					ResourceUtil.writeFileFromResource(RES_DLL, file_dll);
				}
			}
			windowsDllsLoaded = true;
		}
		return exe;
	}

	private static Map<SolverExecutable,File[]>  loaded = new Hashtable<SolverExecutable,File[]>( );

	public static JavaVersion getJavaVersion() {
		if ((System.getProperty("java.version")).contains("1.5")) {
			return JavaVersion.FIVE;
		} 
		else if ((System.getProperty("java.version")).contains("1.6")) {
			return JavaVersion.SIX;
		} 
		else if ((System.getProperty("java.version")).contains("1.7")) {
			return JavaVersion.SEVEN;
		} 
		else {
			System.err.println("Whoa... VCell only runs on JVM versions 1.5, 1.6 or 1.7 and can't determine that its running on one of these.  Assuming 1.5 as a default for safety");
			return JavaVersion.FIVE;
		}

	}

	/**
	 * set path to native library directory and load them up
	 * @throws Error if load fails
	 */
	public static void loadNativeLibraries( ) throws Error {
		if (!nativeLoaded) {
			String iRoot = PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot);
			String nativeDir = iRoot + "/nativelibs/" + NATIVE_LIB_DIR;
			NativeLoader.setNativeLibraryDirectory(nativeDir);
			NativeLoader nl = new NativeLoader();
			nl.loadNativeLibraries();
			}
		nativeLoaded = true;
	}

	// getter and setter for lastUserLocalDir - temporary : until a more permanent, robust solution is thought out for running vcell locally.
	public static String getLastUserLocalDir() {
		return lastUserLocalDir;
	}

	public static void setLastUserLocalDir(String lastUserLocalDir) {
		ResourceUtil.lastUserLocalDir = lastUserLocalDir;
	}

	public static File getUserHomeDir()
	{
		if(userHome == null)
		{
			userHome = new File(System.getProperty("user.home"));
			if (!userHome.exists()) {
				userHome = new File(".");
			}
		}

		return userHome; 
	}

	public static File getLocalRootDir()
	{
		if(localRootDir == null)
		{
			localRootDir = new File(getVcellHome(), "simdata");
			if (!localRootDir.exists()) {
				localRootDir.mkdirs();
			}
		}

		return localRootDir; 
	}

	public static File getLocalSimDir(String userSubDirName)
	{
		if(localSimDir == null)
		{
			localSimDir = new File(getLocalRootDir(), userSubDirName);
			if (localSimDir.exists()) {
				for (File file : localSimDir.listFiles()) {
					file.delete();
				}
			} else {
				localSimDir.mkdirs();
			}
		}

		return localSimDir; 
	}

	public static void writeResourceToFile(String resname, File file) throws IOException{
		java.net.URL url = ResourceUtil.class.getResource(resname);
		if (url == null) {
			throw new RuntimeException("ResourceUtil::writeFileFromResource() : Can't get resource for " + resname);
		}

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(url.openConnection().getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte byteArray[] = new byte[10000];
			while (true) {
				int numRead = bis.read(byteArray, 0, byteArray.length);
				if (numRead == -1) {
					break;
				}

				bos.write(byteArray, 0, numRead);
			}
		} finally {
			if(bis != null){try{bis.close();}catch(Exception e){e.printStackTrace();}}
			if(bos != null){try{bos.close();}catch(Exception e){e.printStackTrace();}}
		}			

	}
	public static void writeFileFromResource(String resname, File file) throws IOException {
		writeResourceToFile(resname,file);
		if (!bWindows) {
			System.out.println("Make " + file + " executable");
			Process p = Runtime.getRuntime().exec("chmod 755 " + file);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static File getVcellHome() 
	{
		if(vcellHome == null)
		{
			vcellHome = new File(getUserHomeDir(), ".vcell");
			if (!vcellHome.exists()) {
				vcellHome.mkdirs();
			}
		}
		return vcellHome;
	}	

	public static File getSolversDirectory() 
	{
		if(solversDirectory == null)
		{
			solversDirectory = new File(getVcellHome(), "solvers");
			if (!solversDirectory.exists()) {
				solversDirectory.mkdirs();
			}
		}
		return solversDirectory;
	}	

	/**
	 * Ensure solvers extracted from resources and registered as property
	 * @param cf
	 * @return array of exes used by provided solver
	 * @throws IOException, {@link UnsupportedOperationException} if no exe for this solver
	 */
	public static File[] getExes(SolverDescription sd) throws IOException {
		SolverExecutable se = sd.getSolverExecutable(); 
		if (se != null) {
			if (loaded.containsKey(se)) {
				return loaded.get(se);
			}
			SolverExecutable.NameInfo nameInfos[] = se.getNameInfo(); 
			File files[] = new File[nameInfos.length];
			for (int i = 0; i < nameInfos.length; ++i) {
				SolverExecutable.NameInfo ni = nameInfos[i];
				File exe = loadExecutable(ni.exeName);
				System.getProperties().put(ni.propertyName,exe.getAbsolutePath());
				files[i] = exe; 
			}
			loaded.put(se, files);
			return files;
		}
		throw new UnsupportedOperationException("SolverDescription " + sd + " has no executable");
	}

	/**
	 * calls {@link #getExes(SolverConfig)} if solver requires executables,
	 * no-op otherwise
	 */
	public static void prepareSolverExecutable(SolverDescription solverDescription) throws IOException {
		if (solverDescription.getSolverExecutable() != null) {
			if (!bWindows && !bMac && !bLinux) {
				throw new RuntimeException("Native solvers are supported on Windows, Linux and Mac OS X.");
			}
			getExes(solverDescription);
		}
	}
	//HERE
	
		public static File getVCellInstall()//HERE
		{
   		File installDirectory = new File(PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot));
    		if (!installDirectory.exists() || !installDirectory.isDirectory()){    			throw new RuntimeException("ResourceUtil::getVCellInstall() : failed to read install directory from property");    		}    		return installDirectory;    	}    	    	public static File getVisToolPythonScript()    	{    		File visToolScriptDir = new File(getVCellInstall(),"visTool");    		File visToolScript = new File(visToolScriptDir, "visMainCLI.py");    		return visToolScript;    	}    	    	public static File getVisitExecutable() throws FileNotFoundException    	{    		//    		// check the system path first    		//    		String execuableName = "visit";    		if (bWindows){    			execuableName += ".exe";    		}    		File[] visitExe = FileUtils.findFileByName(execuableName, ResourceUtil.getSystemPath());    		if (visitExe!=null && visitExe.length>0){    			return visitExe[0];    		}    		//    		// not in path, look in common places    		//    		if (bWindows){    			File programFiles = new File("C:\\Program Files");    			if (programFiles.isDirectory()){    				visitExe = FileUtils.findFileByName("visit.exe",FileUtils.getAllDirectories(programFiles));    				if (visitExe != null && visitExe.length > 0){    					return visitExe[0];    				}    			}    		}    		throw new FileNotFoundException("cannot find VisIt executable file");    	}    	    	public static File getVisToolShellScript()    	{    		String vcellvisitScript = null;    		if (bWindows){    			vcellvisitScript = "vcellvisit.cmd";    		}else{    			vcellvisitScript = "vcellvisit.sh";    		}    		java.net.URL url = ResourceUtil.class.getResource(RES_PACKAGE + "/" + vcellvisitScript);    		return new File(url.getFile());    	}    	    	public static void launchVisTool(Component parent) throws IOException, ExecutableException    	{    		File script = 				ResourceUtil.getVisToolShellScript();    		File visMainCLI =           ResourceUtil.getVisToolPythonScript();    		File visitExecutable = null;    		    		try {    			visitExecutable = ResourceUtil.getVisitExecutable();    		} catch (FileNotFoundException e){    			String retcode = DialogUtils.showOKCancelWarningDialog(parent, "VisIt executable not found", "if VisIt is installed (from https://wci.llnl.gov/codes/visit/) but not in the system path, then press press OK and navigate to the executable.\n Else, install VisIt, restart VCell, and try again");    			if (!retcode.equals(SimpleUserMessage.OPTION_OK)){    				return;    			}    			//    			// ask user for file location    			//    			VCFileChooser fileChooser = new VCFileChooser(getVCellInstall());    			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);    			fileChooser.setMultiSelectionEnabled(false);    			fileChooser.setDialogTitle("Choose VisIt executable file");    			if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {    				// user didn't choose save    				throw UserCancelException.CANCEL_FILE_SELECTION;    			} else {    				File selectedFile = fileChooser.getSelectedFile();    				if (selectedFile.exists()) {    					visitExecutable = selectedFile;    				}    			}    		}    		    		if (!script.exists() || !script.isFile()){    			throw new IOException("script not found, "+script.getAbsolutePath());    		}    		if (visitExecutable==null || !visitExecutable.exists() || !visitExecutable.isFile()){    			throw new IOException("visit executable not found, "+visitExecutable.getAbsolutePath());    		}    		if (!visMainCLI.exists() || !visMainCLI.isFile()){    			throw new IOException("vcell/visit main python file not found, "+visMainCLI.getAbsolutePath());    		}    		    		System.out.println("Starting VCellVisIt as a sub-process");    		    		//    		// get existing environment variables and add visit command and python script to it.    		//    		Map<String,String> envVariables = System.getenv();    		ArrayList<String> envVarList = new ArrayList<String>();    		for (String varname : envVariables.keySet()){    			String value = envVariables.get(varname);    			envVarList.add(varname+"="+value);    		}    		envVarList.add("visitcmd=\""+visitExecutable.getPath()+"\"");    		envVarList.add("pythonscript="+visMainCLI.getPath().replace("\\", "/"));    		    		Process process = Runtime.getRuntime().exec(    				new String[] {"cmd", "/K", "start", script.getPath()},     				envVarList.toArray(new String[0]));    		    		System.out.println("Started VCellVisIt");    	}
    	    public static File[] getSystemPath(){    		String PATH = System.getenv("PATH");    		if (PATH==null || PATH.length() == 0){    			throw new RuntimeException("PATH environment variable not set");    		}    		ArrayList<File> directories = new ArrayList<File>();    		final String delimiter;    		if (bWindows){    			delimiter = ";";    		}else{    			delimiter = ":";    		}    		StringTokenizer tokens = new StringTokenizer(PATH, delimiter);    		while (tokens.hasMoreTokens()){    			File dir = new File(tokens.nextToken());    			if (dir.exists() && dir.isDirectory()){    				directories.add(dir);    			}else{    				System.err.println("directory '"+dir.getAbsolutePath()+"' in system path is not found");    			}    		}    		return directories.toArray(new File[0]);    	}
}