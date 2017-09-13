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
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;
import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.NoLogging;

import cbit.vcell.util.NativeLoader;

public class ResourceUtil {
	private static final String LOCALSOLVERS_DIR = "localsolvers";
	private static final String PYTHONSCRIPTS_DIR = "pythonScripts";
	private static final String VCELL_OPT_DIR = "VCell_Opt";
	private static final String VCELL_VISIT_DIR = "VCell_VisIt";
	private static final String VCELL_VTK_DIR = "VCell_VTK";
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
	private static File optimizationRootDir = null;

	/**
	 * normally set once; protected to allow test fixtures to access
	 */
	protected static File solversDirectory = null;
	/**
	 * directory to cache licensed files download from vcell.org
	 */
	private static File downloadDirectory = null;

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
	
	public static File getPerlExe() throws IOException {
		try {
			File perlExe = null;
			
			perlExe = ResourceUtil.getExecutable("perl", false);
			if (perlExe == null || !perlExe.exists()){
				throw new RuntimeException("failed to find installed perl - please install perl (see https://www.perl.org/)");
			}
			return perlExe;
		} catch (InterruptedException | FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException("failed to find perl executable: "+e.getMessage()+"\n\n please install perl (see https://www.perl.org/)");
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
	 */	public static File getExecutable(String name, boolean useBitSuffix/*, ExecutableFinder efinder*/) throws FileNotFoundException, InterruptedException	{
		String executableName = null;
//		try{
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );		executableName = getExecutableName(name, useBitSuffix, osi);
		File executable = VCellConfiguration.getFileProperty(executableName);
		if (executable!=null){
			return executable;
		}
		//		// check the system path first		//
		Collection<File> exes = FileUtils.findFileByName(executableName, getSystemPath());
		if (exes != null && !exes.isEmpty()) {
			return VCellConfiguration.setFileProperty(executableName, exes.iterator().next());
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
						return VCellConfiguration.setFileProperty(executableName, exes.iterator().next());
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
				env.put(LIBPATH,getLocalSolversDirectory().getAbsolutePath());
			}
			break;

		case WINDOWS:			break;
		case MAC:
			break;
		}
	}

	/**
	 * load solver executable from resources along with libraries
	 * @param basename name of executable without path or os specific extension
	 * @param vl VersionedLibrary, may not be null
	 * @return executable
	 * @throws IOException
	 */
	public static File findSolverExecutable(String basename) throws IOException {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		String name = basename + osi.getExeBitSuffix();
		return new File(getLocalSolversDirectory(),name);
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

	public static final String VCELL_HOME_DIR_NAME = ".vcell";
	public static final String VCELL_PROXY_VMOPTIONS = "proxy.vmoptions";
	@NoLogging
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
	public static File getLocalSolversDirectory()
	{
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		final File localSolversRootDir = new File(getVCellInstall(),LOCALSOLVERS_DIR);
		final File localSolversOSDir = new File(localSolversRootDir,osi.getNativeLibDirectory());
		return localSolversOSDir;
	}

	private static File getBNGRoot(){
		return new File(getVCellInstall(),"bionetgen");
	}
	
	public static String getBNG2_perl_file(){
		File bng2_file = new File(getBNGRoot(),"BNG2.pl");
		String bng2_path = bng2_file.getAbsolutePath();
//		if (bng2_path.contains(":")){
//			bng2_path = "/" + bng2_path.replace(":","").replace('\\','/');
//		}
		return bng2_path;
	}

	public static String getBNG2StandaloneWin64(){
		return new File(getBNGRoot(),"win64Standalone/BNG2.exe").getAbsolutePath();
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

	private static File getPythonScriptsDir() {
		return new File(getVCellInstall(),PYTHONSCRIPTS_DIR);
	}

	public static File getVCellOptPythonDir() {
		return new File(getPythonScriptsDir(),VCELL_OPT_DIR);
	}

	public static File getVCellVTKPythonDir() {
		return new File(getPythonScriptsDir(),VCELL_VTK_DIR);
	}

	public static File getVCellVisItPythonDir() {
		return new File(getPythonScriptsDir(),VCELL_VISIT_DIR);
	}

	public static File getOptimizationRootDir()
	{
		if(optimizationRootDir == null)
		{
			optimizationRootDir = new File(getVcellHome(), "optimization");
			if (!optimizationRootDir.exists()) {
				optimizationRootDir.mkdirs();
			}
		}

		return optimizationRootDir;
	}
	
	private static String getBioformatsJarDownloadURLString(){
		return PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarDownloadURL);
	}
	
	private static File getPluginFolder(){
		return new File(ResourceUtil.getVcellHome(),"plugins");
	}
		
	public static void downloadBioformatsJar() throws MalformedURLException, IOException{
		boolean bPluginFolderExists = false;
		
		if (!(bPluginFolderExists = getPluginFolder().exists())) {
			bPluginFolderExists = getPluginFolder().mkdirs();
			if (bPluginFolderExists) {
//				getPluginFolder().setWritable(true);
			}
			else {
				throw new RuntimeException("not able to create plugin directory: "+getPluginFolder().getAbsolutePath());
			}
		}
		File jarFile = new File(getPluginFolder(),PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName));
		FileUtils.saveUrlToFile(jarFile.getAbsolutePath(), getBioformatsJarDownloadURLString());
	}
	
	public static File getBioFormatsExecutableJarFile() throws Exception{
		String bioformatsJarFileName = PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName);
		File pluginDir = getPluginFolder();
		File bioformatsPluginFile = new File(pluginDir,bioformatsJarFileName);
		if (bioformatsPluginFile.exists()){
			return bioformatsPluginFile;
		}else{
			return null;
		}
	}


}