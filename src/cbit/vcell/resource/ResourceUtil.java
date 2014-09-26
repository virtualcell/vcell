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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.util.PropertyLoader;

import cbit.vcell.solver.SolverDescription;

public class ResourceUtil {
	private static final String system_osname = System.getProperty("os.name");
	public final static boolean bWindows = system_osname.contains("Windows");
	public final static boolean bMac = system_osname.contains("Mac");
	public final static boolean bLinux = system_osname.contains("Linux");
	private final static String system_osarch = System.getProperty("os.arch");
	private final static boolean b64bit = system_osarch.endsWith("64");
	public final static boolean bMacPpc = bMac && system_osarch.contains("ppc");
	
	public static enum JavaVersion  {FIVE,SIX,SEVEN};
	
	// temporary : until a more permanent, robust solution is thought out for running vcell locally.
	private static String lastUserLocalDir = null;
	

	private final static String osname_prefix;
	static {
		if (bWindows) {
			osname_prefix = "win";
		} else if (bMac) {
			osname_prefix = "mac";
		} else if (bLinux) {
			osname_prefix = "linux";
		} else {
			throw new RuntimeException(system_osname + " is not supported by the Virtual Cell.");
		}
	}
	public final static String EXE_SUFFIX = bMacPpc ? "_ppc" : (b64bit ? "_x64" : "") + (bWindows ? ".exe" : "");
	public final static String NATIVELIB_SUFFIX = b64bit ? "_x64" : (bMacPpc ? "_ppc" : "");
	public final static String RES_PACKAGE = "/cbit/vcell/resource/" + osname_prefix + (b64bit ? "64" : "32");
	
	private static File userHome = null;
	private static File vcellHome = null;
	private static File libDir = null;
	private static File localSimDir = null;
	private static File localRootDir = null;
	private static File logDir = null;

	private static File solversDirectory = null;
	
	private static final boolean  IS_DEBUG = java.lang.management.ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	public enum SolverExecutable {
		FiniteVolume("FiniteVolume" + EXE_SUFFIX),
		SundialsOde("SundialsSolverStandalone" + EXE_SUFFIX),
		Gibson("VCellStoch" + EXE_SUFFIX),
		Hybrid_EM("Hybrid_EM" + EXE_SUFFIX),
		Hybrid_Mil("Hybrid_MIL" + EXE_SUFFIX),
		Hybrid_Mil_Adaptive("Hybrid_MIL_Adaptive" + EXE_SUFFIX),
		Smoldyn("smoldyn" + EXE_SUFFIX);
		
		private boolean bFirstTime = true;
		private String name;
		private String res;
		SolverExecutable(String n) {
			this.name = n;
			this.res = RES_PACKAGE + "/" + name;
		}
		
		public File getExecutable() throws IOException {
			File exe = new java.io.File(getSolversDirectory(), name);
			if (bFirstTime || !exe.exists()) {
				ResourceUtil.writeFileFromResource(res, exe);
			}
			if (this == Smoldyn || this == FiniteVolume) {
				if (bWindows) {
					String DLL_GLUT = b64bit ? "glut64.dll" : "glut32.dll";
					String RES_DLL_GLUT = RES_PACKAGE + "/" + DLL_GLUT;
					File file_glut_dll = new java.io.File(getSolversDirectory(), DLL_GLUT);
					if (bWindows && (bFirstTime || !file_glut_dll.exists())) {
						ResourceUtil.writeFileFromResource(RES_DLL_GLUT, file_glut_dll);
					}
				}
			}
			bFirstTime = false;			
			return exe;
		}
	}
	
	private static List<String> libList = null;

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
	
	public static File getLogDir()
	{
		if(logDir == null)
		{
			logDir = new File(getVcellHome(), "logs");
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
		}

		return logDir; 
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
	
	public static void loadLibrary(String libname) {
		if (libDir == null) {
			libDir = new File(vcellHome, "lib");
			if (!libDir.exists()) {
				libDir.mkdirs();
			}
			libList = Collections.synchronizedList(new ArrayList<String>());
		}
		
		if (libList.contains(libname)) {
			return;
		}
		String lib = System.mapLibraryName(libname);	// platform dependent dynamic library name
		File libfile = new File(libDir, lib);
		try {
			writeFileFromResource(RES_PACKAGE + "/" + lib, libfile);
			System.out.println("loading " + libfile);
			System.load(libfile.getAbsolutePath());
			libList.add(libname);
		} catch (IOException ex) {
			throw new RuntimeException("ResourceUtil::loadLibrary() : failed to write library " + libname + " (" + ex.getMessage() + "). Please try again.");
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
	
	public static void loadNativeSolverLibrary () {
		try {
			if (!bWindows && !bMac && !bLinux) {
				throw new RuntimeException("Native solvers are supported on Windows, Linux and Mac OS X at this time.");
			}
	        System.loadLibrary("NativeSolvers" + NATIVELIB_SUFFIX);
	    } catch (Throwable ex1) {
    		throw new RuntimeException("ResourceUtil::loadNativeSolverLibrary(): " + ex1.getMessage());
		}
	}
	
	public static boolean isRunningInDebugger( ) {
		return IS_DEBUG;
	}

	public static void loadCopasiSolverLibrary () {
		if (!bWindows && !bMac && !bLinux || bMacPpc) {
			throw new RuntimeException("Parameter Estimation is supported on Windows, Linux and Mac OS X (excluding PowerPC) at this time.");
		}
		try {
	        System.loadLibrary("vcellCopasiOptDriver" + NATIVELIB_SUFFIX);
	    } catch (Throwable ex1) {
    		throw new RuntimeException("ResourceUtil::loadCopasiSolverLibrary(): " + ex1.getMessage());
		}
	}
	
	public static void loadlibSbmlLibray () {
		try {
			if (!bWindows && !bMac && !bLinux) {
				throw new RuntimeException("libSBML is supported on Windows, Linux and Mac OS X at this time.");
			}
			System.loadLibrary("sbmlj" + NATIVELIB_SUFFIX);
		} catch (Throwable ex1){
			throw new RuntimeException("ResourceUtil::loadlibSbmlLibray(): " + ex1.getMessage());
		}
	}
		
	public static void prepareSolverExecutable(SolverDescription solverDescription) throws IOException {
		if (!bWindows && !bMac && !bLinux) {
			throw new RuntimeException("Native solvers are supported on Windows, Linux and Mac OS X.");
		}
		if (bMacPpc && (solverDescription.equals(SolverDescription.HybridEuler)
				|| solverDescription.equals(SolverDescription.HybridMilstein)
				|| solverDescription.equals(SolverDescription.HybridMilAdaptive))) {
			throw new RuntimeException(solverDescription.getDisplayLabel() + " is supported on Windows, Linux and Mac OS X (excluding PowerPC).");
		}
		if (solverDescription.equals(SolverDescription.CombinedSundials)
				|| solverDescription.equals(SolverDescription.CVODE)
				|| solverDescription.equals(SolverDescription.IDA)) {
			File exe = SolverExecutable.SundialsOde.getExecutable();
			System.getProperties().put(PropertyLoader.sundialsSolverExecutableProperty, exe.getAbsolutePath());
		} else	if (solverDescription.equals(SolverDescription.StochGibson)) {
			File exe = SolverExecutable.Gibson.getExecutable();
			System.getProperties().put(PropertyLoader.stochExecutableProperty, exe.getAbsolutePath());
		} else if (solverDescription.equals(SolverDescription.HybridEuler)) {
			File exe = SolverExecutable.Hybrid_EM.getExecutable();
			System.getProperties().put(PropertyLoader.hybridEMExecutableProperty, exe.getAbsolutePath());
		} if (solverDescription.equals(SolverDescription.HybridMilstein)) {
			File exe = SolverExecutable.Hybrid_Mil.getExecutable();
			System.getProperties().put(PropertyLoader.hybridMilExecutableProperty, exe.getAbsolutePath());
		} if (solverDescription.equals(SolverDescription.HybridMilAdaptive)) {
			File exe = SolverExecutable.Hybrid_Mil_Adaptive.getExecutable();
			System.getProperties().put(PropertyLoader.hybridMilAdaptiveExecutableProperty, exe.getAbsolutePath());
		} else if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone)
				|| solverDescription.equals(SolverDescription.SundialsPDE)) {
			File exe = SolverExecutable.FiniteVolume.getExecutable();
			System.getProperties().put(PropertyLoader.finiteVolumeExecutableProperty, exe.getAbsolutePath());		
		} else if (solverDescription.isSpatialStochasticSolver()) {
			File exe = SolverExecutable.Smoldyn.getExecutable();
			System.getProperties().put(PropertyLoader.smoldynExecutableProperty, exe.getAbsolutePath());
		} 
	}
}
