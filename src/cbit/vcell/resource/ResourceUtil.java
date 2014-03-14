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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;

import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.SpecialLicense;
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
	public final static String EXE_BIT_SUFFIX;
	public final static String EXE_SUFFIX;
	public final static String NATIVELIB_SUFFIX;
	public final static String RES_PACKAGE; 
	/**
	 * name of native library directory (leaf only)
	 */
	public final static String NATIVE_LIB_DIR; 
	/**
	 * has the user accepted special license? 
	 */
	private static Boolean specialLicenseAccepted[] = new Boolean[SpecialLicense.size];
	/**
	 * preferences key prefix 
	 */
	private static final String LICENSE_ACCEPTED = "licenseAccepted";
	static {
		String BIT_SUFFIX = "";
		if (b64bit) {
			BIT_SUFFIX ="_x64";
		}
		if (bWindows) {
			osname_prefix = "win";
			EXE_SUFFIX = ".exe";
			NativeLoader.setOsType(NativeLoader.OsType.WINDOWS);
		} else if (bMac) {
			osname_prefix = "mac";
			EXE_SUFFIX = "";
			NativeLoader.setOsType(NativeLoader.OsType.MAC);
		} else if (bLinux) {
			osname_prefix = "linux";
			EXE_SUFFIX = "";
			NativeLoader.setOsType(NativeLoader.OsType.LINUX);
		} else {
			throw new RuntimeException(system_osname + " is not supported by the Virtual Cell.");
		}
	    EXE_BIT_SUFFIX = BIT_SUFFIX + EXE_SUFFIX;
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

	private static List<File>  librariesLoaded = new ArrayList<File>();
	private static boolean nativeLoaded = false; 
	/**
	 * @param basename name of executable without path or os specific extension
	 * @param sl special license, may be null 
	 * @param firstLoad is the first exe loaded?
	 * @return executable
	 * @throws IOException, {@link UnsupportedOperationException} if license not accepted
	 */
	private static File loadExecutable(String basename, SpecialLicense sl) throws IOException {
		if (!isLicensed(sl)) {
			throw new UnsupportedOperationException("Unable to run " + basename + " because " + sl.toString( ) + " software license not accepted.");
		}
		String name = basename + EXE_BIT_SUFFIX;
		String res = RES_PACKAGE + "/" + name;
		File exe = new java.io.File(getSolversDirectory(), name);
		if (!exe.exists()) {
			ResourceUtil.writeFileFromResource(res, exe);
		}
		ArrayList<String> requiredLibraries = new ArrayList<String>();
		if (bWindows) {
			if (b64bit){
				requiredLibraries.add("glut64.dll");
			}else{
				requiredLibraries.add("glut32.dll");
			}

			if (sl == SpecialLicense.CYGWIN) {
				requiredLibraries.add("cyggcc_s-seh-1.dll");
				requiredLibraries.add("cygstdc++-6.dll");
				requiredLibraries.add("cyggfortran-3.dll");
				requiredLibraries.add("cygquadmath-0.dll");
				requiredLibraries.add("cygwin1.dll");
			}
		}
		else {
			requiredLibraries.add("libgfortran.so.3");
		}
		for (String dllName : requiredLibraries){
			String RES_DLL = RES_PACKAGE + "/" + dllName;
			File file_dll = new java.io.File(getSolversDirectory(), dllName);
			if (!librariesLoaded.contains(file_dll)) {
				if (!file_dll.exists()) {
					ResourceUtil.writeFileFromResource(RES_DLL, file_dll);
				}
			librariesLoaded.add(file_dll);
			}
		}
		return exe;
	}
	
	/**
	 * determine if licensed is required and user has accepted 
	 * @return true if has
	 */
	public static boolean isLicensed(SpecialLicense license) {
		if (license == null) {
			return true;
		}
		//change to case statement if additional licenses added
		if (license == SpecialLicense.CYGWIN) {
			if (!bWindows) {
				return true;
			}
		}
			
		int index = license.ordinal();
		if (specialLicenseAccepted[index] == null) {
			Preferences uprefs = Preferences.userNodeForPackage(ResourceUtil.class);
			specialLicenseAccepted[index] = uprefs.getBoolean(LICENSE_ACCEPTED + license.name(),false);
		}
		return specialLicenseAccepted[index];
	}
	
	/**
	 * 
	 * @param license
	 * @throws IllegalArgumentException if license null
	 */
	public static void acceptLicense(SpecialLicense license) {
		if (license == null) {
			throw new IllegalArgumentException("null license object");
		}
		int index = license.ordinal();
		specialLicenseAccepted[index] = true; 
		Preferences uprefs = Preferences.userNodeForPackage(ResourceUtil.class);
		uprefs.putBoolean(LICENSE_ACCEPTED + license.name(),true);
	}
	
	//for junit testing
	static void clearLicense(SpecialLicense license) {
		assert license != null;
		int index = license.ordinal();
		specialLicenseAccepted[index] = null; 
		Preferences uprefs = Preferences.userNodeForPackage(ResourceUtil.class);
		uprefs.remove(LICENSE_ACCEPTED + license.name());
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
			try {
				NativeLoader nl = new NativeLoader();
				nl.loadNativeLibraries();
			}
			catch (Exception e) {
				throw new Error("Error setting up native libraries ",e);
			}
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
				File exe = loadExecutable(ni.exeName, sd.specialLicense);
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

	public static File getVCellInstall()
	{
		File installDirectory = new File(PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot));
		if (!installDirectory.exists() || !installDirectory.isDirectory()){			throw new RuntimeException("ResourceUtil::getVCellInstall() : failed to read install directory from property");		}		return installDirectory;	}
	
	/**
	 * class which can help find executable via some means
	 */
	public interface ExecutableFinder {
		File find(String executableName);
	}
	/**
	 * get executable based on name; will try stored values, common program names and optional finder 
	 * @param name
	 * @param useBitSuffix whether to use VCell rules for naming executable
	 * @param efinder extra steps to find executable, may be null
	 * @return executable file if it can be found
	 * @throws FileNotFoundException if it can't
	 */	public static File getExecutable(String name, boolean useBitSuffix, ExecutableFinder efinder) throws FileNotFoundException	{		String executableName = name;
		if (useBitSuffix) {
			executableName += EXE_BIT_SUFFIX;
		}
		else {
			executableName += EXE_SUFFIX;
		}
		if (ExeCache.isCached(executableName)) {
			return ExeCache.get(executableName);
		}
		//		// check the system path first		//
		Collection<File> exes = FileUtils.findFileByName(executableName, getSystemPath());
		if (!exes.isEmpty()) {
			return ExeCache.store(executableName, exes.iterator().next());
		}
		//		// not in path, look in common places		//		if (bWindows){
			//use set to eliminate duplicates
			Set<String> searchDirs = new HashSet<String>( );
			String envs[] = {"ProgramFiles", "ProgramFiles(x86)", "ProgramW6432"};
			for (String e : envs) {
				String d = System.getenv(e);
				if (d != null) {
					searchDirs.add(d);
				}
			}
			for (String pf :searchDirs) {
				File programFiles = new File(pf);				if (programFiles.isDirectory()){					exes = FileUtils.findFileByName(executableName,FileUtils.getAllDirectoriesCollection(programFiles));					if (!exes.isEmpty()) {
						return ExeCache.store(executableName, exes.iterator().next());
					}
				}
			}		}
		if (efinder != null) {
			File f = efinder.find(executableName);
			if (f != null) {
				return ExeCache.store(executableName, f);
			}
		}		throw new FileNotFoundException("cannot find " + name + " executable file " + executableName);	}	/**
	 * @return system path directories
	 * @throws RuntimeException if PATH environmental not set
	 */ 	public static Collection<File>  getSystemPath( ) {		final String PATH = System.getenv("PATH");		if (PATH==null || PATH.length() == 0){			throw new RuntimeException("PATH environment variable not set");		}
		return FileUtils.toFiles(FileUtils.splitPathString(PATH), true);
	}
	
	/**
	 * add system specific environment settings
	 * @param envs
	 */
	public static void setEnvForOperatingSystem(Map<String,String> env) {
		if (bLinux) {
			final String LIBPATH="LD_LIBRARY_PATH";
			String existing = env.get(LIBPATH);
			if (existing == null) {
				env.put(LIBPATH,getSolversDirectory().getAbsolutePath());
				return;
			}
			else {
				existing += FileUtils.PATHSEP + getSolversDirectory().getAbsolutePath(); 
				env.put(LIBPATH,existing);
			}
		}
	}

	/**
	 * store and retrieve executable locations in user preferences
	 * make separate class to isolate implementation and to have distinct preferences
	 */
	static class ExeCache { //package level access for testing
		private static Preferences prefs = Preferences.userNodeForPackage(ExeCache.class);
		private static Map<String,File>  cache = new HashMap<String, File>( );

		static boolean isCached(String name) {
			if (cache.containsKey(name)) {
				return true;
			}
			String stored = prefs.get(name,null);
			if (stored != null) {
				File f = new File(stored);
				if (f.canExecute()) {
					cache.put(name, f);
					return true;
				}
				//stored value is bad, so clear it
				prefs.remove(name);
			}
			return false;
		}

		/**
		 * get cached executable; call {@link #isCached(String)} to verify in cache
		 * before calling
		 * @param name
		 * @return executable
		 * @throws IllegalStateException if name not cached
		 */
		static File get(String name) {
			if (!isCached(name)) {
				throw new IllegalStateException(name + " not cached");
			}
			return cache.get(name);
		}

		/**
		 * cache newly found file
		 * @param name
		 * @param f
		 * @return f
		 */
		static File store(String name, File f) {
			cache.put(name, f);
			prefs.put(name, f.getAbsolutePath());
			return f;
		}
		
		/**
		 * remove stored locations from cache
		 * @throws BackingStoreException 
		 */
		static void forgetExecutableLocations( ) throws BackingStoreException {
			prefs.clear();
		}
	}
}