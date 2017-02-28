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
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.vcell.util.NativeLoader;

public class ResourceUtil {
	private static final String MANIFEST_FILE_NAME = ".versionManifest.txt";
	private static final String system_osname = System.getProperty("os.name");
	private final static String system_osarch = System.getProperty("os.arch");
	private final static boolean b64bit = system_osarch.endsWith("64");
	public final static boolean bWindows = system_osname.contains("Windows");
	public final static boolean bMac = system_osname.contains("Mac");
	public final static boolean bLinux = system_osname.contains("Linux");

	public static enum JavaVersion  {
		SEVEN("1.7"),
		EIGHT("1.8");
		final String versionIdentifier;

		private JavaVersion(String versionIdentifier) {
			this.versionIdentifier = versionIdentifier;
		}

	};
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
	private static File logDir = null;

	private static File solversDirectory = null;
	/**
	 * directory to cache licensed files download from vcell.org
	 */
	private static File downloadDirectory = null;

	private static List<File>  librariesLoaded = new ArrayList<File>();
	private static boolean nativeLibrariesSetup = false;
	private static final boolean  IS_DEBUG = java.lang.management.ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    private static final Logger lg = Logger.getLogger(ResourceUtil.class);

    /**
     * ensure class loaded so static initialization executes
     */
    public static void init( ) {

    }

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
	 * @throws BackingStoreException
	 */	public static File getExecutable(String name, boolean useBitSuffix, ExecutableFinder efinder) throws FileNotFoundException, BackingStoreException	{		String executableName = name;
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
		if (bMac){
			File macVisApp = new File("/Applications/VisIt.app");
			if (macVisApp.canRead() && (new File(macVisApp, "/Contents/Resources/bin/visit")).canExecute()) {
				return macVisApp;
			}
		}
		if (efinder != null) {
			File f = efinder.find(executableName);
			if (f != null) {
				return ExeCache.store(executableName, f);
			}
		}		throw new FileNotFoundException("cannot find " + name + " executable file " + executableName);	}	/**
	 * @return system path directories
	 * @throws RuntimeException if PATH environmental not set
	 */	public static Collection<File>  getSystemPath( ) {		final String PATH = System.getenv("PATH");		if (PATH==null || PATH.length() == 0){			throw new RuntimeException("PATH environment variable not set");		}
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
				env.put(LIBPATH,existing);
			}
		}
		if (bWindows) {
			//The 32 windows bit BNG2 compiled Perl program used for BioNetGen
			//calls a cygwin compile "run_network" program. If run_network prints
			//anything to standard error,The BNG script aborts
			//The setting below prevents the cygwin "MS-DOS style path detected" warning from
			//being issued
			env.put("CYGWIN","nodosfilewarning");
		}
	}

	/**
	 * is application running inside a debugger?
	 * @return true if it is
	 */
	public static boolean isRunningInDebugger( ) {
		return IS_DEBUG;
	}

	/**
	 * store and retrieve executable locations in user preferences
	 * make separate class to isolate implementation and to have distinct preferences
	 */
	static class ExeCache { //package level access for testing
		private static Preferences prefs = Preferences.userNodeForPackage(ExeCache.class);
		private static Map<String,File>  cache = new HashMap<String, File>( );

		static boolean isCached(String name) throws BackingStoreException {
			if (cache.containsKey(name)) {
				if (cache.get(name).canRead()){
					return true;
				}
				System.out.println("ExeCache thought it knew the location of executable "+name+" but it isn't readable at location: "+cache.get(name).getAbsolutePath());
			}
			String stored = prefs.get(name,null);
			if (stored != null) {
				File f = new File(stored);
				if (f.canExecute()) {
					cache.put(name, f);
					return true;
				}
				//stored value is bad, so clear it
				System.out.println("Clearing "+name+" from the ExeCache");
				prefs.remove(name);
				prefs.flush();
			}
			return false;
		}

		/**
		 * get cached executable; call {@link #isCached(String)} to verify in cache
		 * before calling
		 * @param name
		 * @return executable
		 * @throws BackingStoreException
		 * @throws IllegalStateException if name not cached
		 */
		static File get(String name) throws BackingStoreException {
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

	/**
	 * @param basename name of executable without path or os specific extension
	 * @param ll LicensedLibrary, may not be null
	 * @param firstLoad is the first exe loaded?
	 * @return executable
	 * @throws IOException, {@link UnsupportedOperationException} if license not accepted
	 */
	public static File loadSolverExecutable(String basename, LicensedLibrary ll) throws IOException {
		if (!ll.isLicensed()) {
			throw new UnsupportedOperationException("Unable to run " + basename + " because " + ll.toString( ) + " software license not accepted.");
		}
		if (!ll.isInstalled()) {
			LicenseManager.install(ll);
		}
		ll.makePresentIn(getSolversDirectory());

		String name = basename + EXE_BIT_SUFFIX;
		String res = RES_PACKAGE + "/" + name;
		File exe = new java.io.File(getSolversDirectory(), name);
		if (!exe.exists()) {
			ResourceUtil.writeFileFromResource(res, exe);
		}
		ArrayList<String> fromResourceLibraries = new ArrayList<String>();
		for (String libName : ll.bundledLibraryNames()) {
			fromResourceLibraries.add(libName);
		}
		if (bWindows) {
			if (b64bit){
				fromResourceLibraries.add("glut64.dll");
			}else{
				fromResourceLibraries.add("glut32.dll");
			}
		}
		else if (bLinux) {
			fromResourceLibraries.add("libgfortran.so.3");
		}
		for (String dllName : fromResourceLibraries){
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

	static {
		if (LibraryLicense.size != 1) {
			//if a new license is added, we need to update the routine below
			throw new Error("Update ResourceUtil.loadLicensedLibraries");
		}
	}
	/**
	 * determine java version from system property
	 * @return current version, or default to first enum value
	 */
	public static JavaVersion getJavaVersion() {
		final String vers = System.getProperty("java.version");
		for (JavaVersion jv: JavaVersion.values()) {
			if (vers.contains(jv.versionIdentifier) ) {
				return jv;
			}
		}
		JavaVersion dflt = JavaVersion.values( )[0];
		System.err.print("Supported JVM versions ");
		for (JavaVersion jv: JavaVersion.values()) {
			System.err.print(jv.versionIdentifier + " ");
		}
		System.err.print("and can't determine that its running on one of these.  Assuming " + dflt.versionIdentifier + " as a default for safety");
		return dflt;
	}



	/**
	 * set path to native library directory
	 * @throws Error if load fails
	 */
	public static void setNativeLibraryDirectory( ) throws Error {
		if (!nativeLibrariesSetup) {
			String iRoot = PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot) ;
			if (iRoot.equals("cwd")) {
				File f = Paths.get("").toAbsolutePath().toFile();
				if (!f.isDirectory()) {
					throw new RuntimeException("Can't convert 'cwd' into directory");
				}
				iRoot = f.getAbsolutePath();
			}

			String nativeDir = iRoot + "/nativelibs/" + NATIVE_LIB_DIR;
			NativeLoader.setNativeLibraryDirectory(nativeDir);
		}
		nativeLibrariesSetup = true;
	}
	/**
	 * set path to native library directory and load them up
	 * @param from directory to load file
	 * @throws Error if load fails
	 */
	public static void loadNativeLibraries(String from ) throws Error {
		if (!nativeLibrariesSetup) {
			NativeLoader.setNativeLibraryDirectory(from);
		}
		nativeLibrariesSetup = true;
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

	public static final String VCELL_HOME_DIR_NAME = ".vcell";
	public static final String VCELL_PROXY_VMOPTIONS = "proxy.vmoptions";
	public static File getVcellHome()
	{
		if(vcellHome == null)
		{
			vcellHome = new File(getUserHomeDir(), VCELL_HOME_DIR_NAME);
			if (!vcellHome.exists()) {
				vcellHome.mkdirs();
			}
		}
		return vcellHome;
	}

	/**
	 * directory to cache licensed files download from vcell.org
	 */
	public static File getDownloadDirectory()
	{
		if(downloadDirectory == null)
		{
			downloadDirectory = new File(getVcellHome(), "download");
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}
		}
		return downloadDirectory;
	}

	/**
	 * create Solvers Directory, if necessary
	 * check last version of software which used directory, delete contents of directory if different
	 * @return directory of locally run solvers
	 */
	public static File getSolversDirectory()
	{
		if(solversDirectory == null)
		{
			solversDirectory = new File(getVcellHome(), "solvers");
			if (!solversDirectory.exists()) {
				solversDirectory.mkdirs();
			}
			else {
				if (!validManifest(solversDirectory)) {
					try {
						//delete existing files
						DirectoryStream<Path> ds = Files.newDirectoryStream(solversDirectory.toPath());
						for (Path entry : ds) {
							entry.toFile().delete();
						}
						//write manifest
						String versionString = VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString();
						Files.write(new File(solversDirectory,MANIFEST_FILE_NAME).toPath(),versionString.getBytes());
					} catch (IOException e) {
						lg.warn("Error cleaning solvers directory",e);
					}
				}
			}
		}
		return solversDirectory;
	}

	/**
	 * see if a directory has a readable manifest file and if it matches current software version
	 * @param testDir
	 * @return true if all conditions met
	 */
	private static boolean validManifest(File testDir) {
		try {
			File existingManifest = new File(testDir,MANIFEST_FILE_NAME);
			if (existingManifest.canRead()) {
				List<String> lines = Files.readAllLines(existingManifest.toPath(), StandardCharsets.UTF_8);
				if (!lines.isEmpty()) {
					String manifest = lines.get(0);
					VCellSoftwareVersion sv = VCellSoftwareVersion.fromSystemProperty();
					return sv.getSoftwareVersionString().equals(manifest);
				}
			}
		} catch (IOException e) {
			lg.warn("Error getting manifest", e);
		}
		return false;
	}


	public static File getVCellInstall()
	{
		File installDirectory = new File(PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot));
		if (!installDirectory.exists() || !installDirectory.isDirectory()){			throw new RuntimeException("ResourceUtil::getVCellInstall() : failed to read install directory from property");		}		return installDirectory;	}

	public static String getSiteName() {
		return VCellSoftwareVersion.fromSystemProperty().getSite().name().toLowerCase();
	}


}