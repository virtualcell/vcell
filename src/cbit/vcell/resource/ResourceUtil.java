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
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.NoLogging;

import cbit.vcell.util.NativeLoader;

public class ResourceUtil {
	private static final String MANIFEST_FILE_NAME = ".versionManifest.txt";

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


	//public final static String EXE_SUFFIX = bMacPpc ? "_ppc" : (b64bit ? "_x64" : "") + (bWindows ? ".exe" : "");
	//public final static String NATIVELIB_SUFFIX = b64bit ? "_x64" : (bMacPpc ? "_ppc" : "");

	private static File userHome = null;
	private static File vcellHome = null;
	private static File localSimDir = null;
	private static File localVisDataDir = null;
	private static File localRootDir = null;
	private static File logDir = null;

	/**
	 * normally set once; protected to allow test fixtures to access
	 */
	protected static File solversDirectory = null;
	/**
	 * directory to cache licensed files download from vcell.org
	 */
	private static File downloadDirectory = null;

	private static Map<File, ProvidedLibrary>  librariesLoaded = new HashMap<>();
	private static boolean nativeLibrariesSetup = false;
	/**
	 * uniquely identify version and variant (OperatingSystemInfo)
	 */
	private static String ourManifest = null;
	private static final Logger LG = Logger.getLogger(ResourceUtil.class);

    /**
     * ensure class loaded so static initialization executes
     */
     public static void init( ) { }

    /**
	 * class which can help find executable via some means
	 */
	public interface ExecutableFinder {
		File find(String executableName) throws UserCancelException;
	}
	public static String getExecutableName(String baseName,boolean useBitSuffix,OperatingSystemInfo osi){
		if (useBitSuffix) {
			return baseName + osi.getExeBitSuffix();
		}else {
			return baseName + osi.getExeSuffix();
		}
	}
	/**
	 * get executable based on name; will try stored values, common program names and optional finder
	 * @param name
	 * @param useBitSuffix whether to use VCell rules for naming executable
	 * @param efinder extra steps to find executable, may be null
	 * @return executable file if it can be found
	 * @throws FileNotFoundException if it can't
	 * @throws BackingStoreException
	 * @throws InterruptedException
	 */	public static File getExecutable(String name, boolean useBitSuffix/*, ExecutableFinder efinder*/) throws FileNotFoundException, BackingStoreException, InterruptedException	{
		String executableName = null;
//		try{
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );		executableName = getExecutableName(name, useBitSuffix, osi);
		if (ExeCache.isCached(executableName)) {
			return ExeCache.get(executableName);
		}
		//		// check the system path first		//
		Collection<File> exes = FileUtils.findFileByName(executableName, getSystemPath());
		if (exes != null && !exes.isEmpty()) {
			return ExeCache.store(executableName, exes.iterator().next());
		}
		//		// not in path, look in common places		//		if (osi.isWindows()){
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
		throw new FileNotFoundException("cannot find " + name + " executable file " + executableName);	}	/**
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
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		switch (osi.getOsType()) {
		case LINUX:
			final String LIBPATH="LD_LIBRARY_PATH";
			String existing = env.get(LIBPATH);
			if (existing == null) {
				env.put(LIBPATH,getSolversDirectory().getAbsolutePath());
			}
			break;

		case WINDOWS:			//The 32 windows bit BNG2 compiled Perl program used for BioNetGen
			//calls a cygwin compile "run_network" program. If run_network prints
			//anything to standard error,The BNG script aborts
			//The setting below prevents the cygwin "MS-DOS style path detected" warning from
			//being issued
			env.put("CYGWIN","nodosfilewarning");
			break;
		case MAC:
			break;
		}
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
			cache.clear();
		}

	}
	/**
	 * load solver executable from resources along with libraries
	 * @param basename name of executable without path or os specific extension
	 * @param vl VersionedLibrary, may not be null
	 * @return executable
	 * @throws IOException
	 */
	public static File loadSolverExecutable(String basename, VersionedLibrary vl) throws IOException {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );		//File solverDest = new java.io.File(getSolversDirectory());
		String name = basename + osi.getExeBitSuffix();
		return loadExecutable(name, vl, getSolversDirectory());
	}
	/**
	 * load arbitrary executable from resources along with libraries
	 * @param filename full name (without path) of executable
	 * @param vl may not be null
	 * @param destination where to install executable and libraries if appropriate
	 * @return executable
	 * @throws IOException
	 */
	public static File loadExecutable(String filename, VersionedLibrary vl, File destination) throws IOException {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		final String pkgName = osi.getResourcePackage();

		String res = pkgName + filename;
		File exe = new java.io.File(destination, filename);
		if (!exe.exists()) {
			ResourceUtil.writeResourceToExecutableFile(res, exe);
		}
		ArrayList<ProvidedLibrary> fromResourceLibraries = new ArrayList<>();
		fromResourceLibraries.addAll(vl.getLibraries());

		if (osi.isWindows()) {
			if (osi.is64bit()) {
				fromResourceLibraries.add(new ProvidedLibrary("glut64.dll"));
			} else {
				fromResourceLibraries.add(new ProvidedLibrary("glut32.dll"));
			}
		} else if (osi.isLinux()) {
			fromResourceLibraries.add(new ProvidedLibrary("libgfortran.so.3"));
		}
		for (ProvidedLibrary pl : fromResourceLibraries) {
			String RES_DLL = pkgName + pl.resourceUrl;
			File file_dll = new java.io.File(destination, pl.libraryName);
			ProvidedLibrary previous = librariesLoaded.get(file_dll);
			if (previous == null) {
				if (!pl.isCacheable() || !file_dll.exists()) {
					ResourceUtil.writeResourceToExecutableFile(RES_DLL, file_dll);
				}
				librariesLoaded.put(file_dll,pl);
			}
			else {
				VCAssert.assertTrue(previous.equals(pl),"clash between " + previous.resourceUrl  + " and "
						+ pl.resourceUrl + " destination " + file_dll);
				}
		}
		return exe;
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
		System.err.print("Whoa... VCell only runs on JVM versions ");
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
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );		if (!nativeLibrariesSetup) {
			String iRoot = getVCellInstall().getAbsolutePath();
			String nativeDir = iRoot + "/nativelibs/" + osi.getNativeLibDirectory();
			NativeLoader.setNativeLibraryDirectory(nativeDir);
		}
		nativeLibrariesSetup = true;
	}
	/**
	 * set path to native library directory
	 * @param from directory to load file
	 * @throws Error if load fails
	 */
	public static void setNativeLibraryDirectory(String from ) throws Error {
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

	@NoLogging
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

	public static File getLocalVisDataDir(){
		if(localVisDataDir == null)
		{
			localVisDataDir = new File(getVcellHome(), "visdata");
			if (!localVisDataDir.exists()) {
				localVisDataDir.mkdirs();
			}
		}

		return localVisDataDir;
	}

	@NoLogging
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

		try (BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));) {
			byte byteArray[] = new byte[10000];
			while (true) {
				int numRead = bis.read(byteArray, 0, byteArray.length);
				if (numRead == -1) {
					break;
				}

				bos.write(byteArray, 0, numRead);
			}
		}
	}

	public static void writeResourceToExecutableFile(String resname, File file) throws IOException {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );		writeResourceToFile(resname,file);
		if (osi.getOsType().isUnixLike()) {
			System.out.println("Make " + file + " executable");
			Process p = Runtime.getRuntime().exec("chmod 755 " + file);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * convert embedded resource (e.g. file) to String
	 * @param resname
	 * @return String or error
	 * @throws IOException
	 */
	public static String resourceToString(String resname) {
		java.net.URL url = ResourceUtil.class.getResource(resname);
		if (url == null) {
			throw new RuntimeException("ResourceUtil::resourceToString() : Can't get resource for " + resname);
		}
		try (BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream()) ) {
		if (bis != null) {
			try (Reader r = new InputStreamReader(bis,"UTF-8")) {
				StringBuilder sb = new StringBuilder();
				final int BSIZE = 1024;
				char buffer[] = new char[BSIZE];
				int bytes = r.read(buffer,0,buffer.length);
				while (bytes > 0) {
					sb.append(buffer,0,bytes);
					bytes = r.read(buffer,0,buffer.length);
				}
				return sb.toString();
			} catch (IOException e) {
				LG.warn("Can't extract " + resname, e);
				e.printStackTrace();
			}
		}
		} catch (IOException e1) {
			LG.warn("Can't get " + resname, e1);
		}
		return "not found";
	}

	@NoLogging
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
			solversDirectory = new File(getVcellHome(), "solvers_"+TokenMangler.mangleToSName(VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString()));
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
						String manifestString = getManifest();
						Files.write(new File(solversDirectory,MANIFEST_FILE_NAME).toPath(),manifestString.getBytes());
					} catch (IOException e) {
						LG.warn("Error cleaning solvers directory",e);
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
					String storedManifest = lines.get(0);
					return storedManifest.equals(getManifest());
				}
			}
		} catch (IOException e) {
			LG.warn("Error getting manifest", e);
		}
		return false;
	}

	private static String getManifest( ) {
		if (ourManifest != null) {
			return ourManifest;
		}
		VCellSoftwareVersion sv = VCellSoftwareVersion.fromSystemProperty();
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		ourManifest = sv.getSoftwareVersionString() + osi.toString( );		return ourManifest;
	}

	public static File getVCellInstall()
	{
		return PropertyLoader.getRequiredDirectory(PropertyLoader.installationRoot);	}

	public static String getSiteName() {
		return VCellSoftwareVersion.fromSystemProperty().getSite().name().toLowerCase();
	}

	/**
	 * convert arbitrary path string to unix style
	 * @param filePath not null
	 * @return unix / linux style path
	 */
	public static String forceUnixPath(String filePath){
		return filePath.replace("C:","").replace("D:","").replace("\\","/");
	}

}