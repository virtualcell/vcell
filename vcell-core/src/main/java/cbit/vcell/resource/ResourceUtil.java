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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.NoLogging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Map;
import java.util.Collection;
import java.util.prefs.BackingStoreException;
import java.util.stream.Stream;

public class ResourceUtil {
	private static final Logger logger = LogManager.getLogger(ResourceUtil.class);

	public static final String LOCAL_SOLVER_LIB_LINK_SUFFIX = "_link";
	public static final String batchResultsDirName = "batchResults";
	public static final String VCELL_HOME_DIR_NAME = ".vcell";
	public static final String VCELL_DOWNLOAD_DIR_NAME = "download";
	public static final String VCELL_PROXY_VMOPTIONS = "proxy.vmoptions";
	private static final String LOCALSOLVERS_DIR = "localsolvers";

	public enum JavaVersion  {
		SEVENTEEN("17");
		final String versionIdentifier;

		JavaVersion(String versionIdentifier) {
			this.versionIdentifier = versionIdentifier;
		}

		};

	// temporary : until a more permanent, robust solution is thought out for running vcell locally.
	private static String lastUserLocalDir = null;

	private static File userHome = null;
	private static File vcellHome = null;
	private static File localSimDir = null;
	private static File localVisDataDir = null;
	private static File localRootDir = null;
	private static File localBatchDir = null;
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

	/**
	 * uniquely identify version and variant (OperatingSystemInfo)
	 */
	private static String ourManifest = null;

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

	public static String getExecutableName(String baseName, boolean useBitSuffix, OperatingSystemInfo osi){
		return baseName + (useBitSuffix? osi.getExeBitSuffix() : osi.getExeSuffix());
	}
	
	public static File getPerlExe() throws IOException {
		try {
			File perlExe = ResourceUtil.getExecutable("perl", false);
			if (perlExe != null && perlExe.exists()) return perlExe;
			throw new RuntimeException("failed to find installed perl - please install perl (see https://www.perl.org/)");
		} catch (InterruptedException | FileNotFoundException e) {
			throw new IOException("failed to find perl executable: "+e.getMessage()+"\n\n please install perl (see https://www.perl.org/)", e);
		}
	}
	/**
	 * get executable based on name; will try stored values, common program names and optional finder
	 * @param name
	 * @param useBitSuffix whether to use VCell rules for naming executable
	 * @return executable file if it can be found
	 * @throws FileNotFoundException if it can't
	 * @throws BackingStoreException
	 * @throws InterruptedException
	 */
	public static File getExecutable(String name, boolean useBitSuffix) throws FileNotFoundException, InterruptedException {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		String executableName = getExecutableName(name, useBitSuffix, osi);
		File executable = VCellConfiguration.getFileProperty(executableName);

		if (executable != null) return executable;

		// check the system path first
		Collection<File> exes = FileUtils.findFileByName(executableName, getSystemPath());
		if (!exes.isEmpty()) return VCellConfiguration.setFileProperty(executableName, exes.iterator().next());

		// not in path, but if it's in windows, look in common places
		if (osi.isWindows()){
			//use set to eliminate duplicates
			Iterable<File> programFilesDirsToSearch = Stream.of("ProgramFiles", "ProgramFiles(x86)", "ProgramW6432")
													.map(System::getenv).filter(Objects::nonNull)
													.map(File::new).filter(File::isDirectory)::iterator;
			for (File programFilesDir : programFilesDirsToSearch) {
				exes = FileUtils.findFileByName(executableName, FileUtils.getAllDirectoriesCollection(programFilesDir));
				if (exes.isEmpty()) continue;
				return VCellConfiguration.setFileProperty(executableName, exes.iterator().next());
			}
		}
		throw new FileNotFoundException("cannot find " + name + " executable file " + executableName);
	}

	/**
	 * @return system path directories
	 * @throws RuntimeException if PATH environmental not set
	 */
	public static Collection<File>  getSystemPath( ) {
		final String pathEnvVar = System.getenv("PATH");
		if (pathEnvVar != null && !pathEnvVar.isEmpty()) return FileUtils.toFiles(FileUtils.splitPathString(pathEnvVar), true);
		throw new RuntimeException("PATH environment variable not set");
	}

	/**
	 * add system specific environment settings
	 * @param env
	 */
	public static void setEnvForOperatingSystem(Map<String,String> env) {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		switch (osi.getOsType()){
			case LINUX -> {
				final String LIBPATH="LD_LIBRARY_PATH";
				String existing = env.get(LIBPATH);
				if (existing == null) env.put(LIBPATH, ResourceUtil.getLocalSolversDirectory().getAbsolutePath());
			}
			case WINDOWS, MAC-> {}
		}
	}

	/**
	 * determine java version from system property
	 * @return current version, or default to first enum value
	 */
	public static JavaVersion getJavaVersion() {
		final String vers = System.getProperty("java.version");
		for (JavaVersion jv: JavaVersion.values()) {
			if (!vers.contains(jv.versionIdentifier)) continue;
			return jv;
		}
		StringBuilder errorStr = new StringBuilder();
		JavaVersion defaultVersion = JavaVersion.values()[0];
		errorStr.append("Whoa... VCell only runs on JVM versions: ");
		for (JavaVersion jv: JavaVersion.values()) {
			errorStr.append(jv.versionIdentifier).append(" ");
		}
		errorStr.append("and can't determine that its running on one of these. We found version: ").append(vers).append(" in the system. ");
		errorStr.append("Assuming ").append(defaultVersion.versionIdentifier).append(" as a default for safety\n");
		logger.error(errorStr.toString());
		return defaultVersion;
	}

	// getter and setter for lastUserLocalDir - temporary : until a more permanent, robust solution is thought out for running vcell locally.
	public static String getLastUserLocalDir() {
		return ResourceUtil.lastUserLocalDir;
	}

	public static void setLastUserLocalDir(String lastUserLocalDir) {
		ResourceUtil.lastUserLocalDir = lastUserLocalDir;
	}

	@NoLogging
	public static File getUserHomeDir() {
		if (ResourceUtil.userHome != null) return ResourceUtil.userHome;
		ResourceUtil.userHome = new File(System.getProperty("user.home"));
		if (ResourceUtil.userHome.exists()) return ResourceUtil.userHome;
		return ResourceUtil.userHome = new File(".");
	}

	public static File getLocalRootDir() {
		if (ResourceUtil.localRootDir != null) return ResourceUtil.localRootDir;
		ResourceUtil.localRootDir = new File(ResourceUtil.getVcellHome(), "simdata");
		if (!ResourceUtil.localRootDir.exists() && ResourceUtil.localRootDir.mkdirs()) logger.warn("could not create local root dir {}", ResourceUtil.localRootDir.getAbsolutePath());
		return ResourceUtil.localRootDir;
	}
	
	private static void deleteRecursively(File f) throws IOException {
		// Note: `f.listFiles()` does **not** have a chance of throwing NPE, because `f.isDirectory()` is the proper guard
		if (f.isDirectory()) for (File c : f.listFiles()) ResourceUtil.deleteRecursively(c);
		if (ResourceUtil.batchResultsDirName.contentEquals(f.getName())) return; // don't delete the batch results directory
		if (!f.delete()) throw new FileNotFoundException("Failed to delete file: " + f);
	}


	public static File getLocalBatchDir() {
		File batchResultsDir = new File(ResourceUtil.getVcellHome(), ResourceUtil.batchResultsDirName);
		if(batchResultsDir.exists()) {
			try {
				ResourceUtil.deleteRecursively(batchResultsDir);	// delete the output directory and all its content recursively
			} catch (IOException e) {
				throw new RuntimeException("Failed to empty the output batch directory '" + batchResultsDirName + "'");
			}
		}

		if(!batchResultsDir.exists() && !batchResultsDir.mkdirs()) throw new RuntimeException("Error initializing the output results batch directory '" + batchResultsDir + "'");
		ResourceUtil.localBatchDir = batchResultsDir;
		if (!ResourceUtil.localBatchDir.isDirectory()) throw new RuntimeException("The output batch file '" + batchResultsDir + "' exists, but is not a directory");
		String[] batchDirectoryContents = ResourceUtil.localBatchDir.list();
		if (batchDirectoryContents == null)
			throw new IOError(new IllegalStateException("OS declares `" + ResourceUtil.localBatchDir + "` is a directory, but java.io cannot list said directory!"));
		if (batchDirectoryContents.length != 0)
			throw new RuntimeException("The output batch directory '" + batchResultsDir + "' contains contents despite attempt to clean! Are multiple VCells running?");
		if (!ResourceUtil.localBatchDir.toString().endsWith(ResourceUtil.batchResultsDirName))
			throw new RuntimeException("Error initializing the output batch directory '" + ResourceUtil.batchResultsDirName + "'");
		return ResourceUtil.localBatchDir;
	}
	
	public static File getLocalVisDataDir(){
		if (ResourceUtil.localVisDataDir != null) return ResourceUtil.localVisDataDir;
		ResourceUtil.localVisDataDir = new File(ResourceUtil.getVcellHome(), "visdata");
		if (!ResourceUtil.localVisDataDir.exists() && !ResourceUtil.localVisDataDir.mkdirs()) logger.warn("could not create local vis data dir {}", ResourceUtil.localVisDataDir.getAbsolutePath());
		return localVisDataDir;
	}

	@NoLogging
	public static File getLogDir(){
		if (ResourceUtil.logDir != null) return ResourceUtil.logDir;
		ResourceUtil.logDir = new File(ResourceUtil.getVcellHome(), "logs");
		if (!ResourceUtil.logDir.exists() && !ResourceUtil.logDir.mkdirs()) logger.warn("could not create log dir {}", ResourceUtil.logDir.getAbsolutePath());
		return ResourceUtil.logDir;
	}

	public static File getLocalSimDir(String userSubDirName){
		if (ResourceUtil.localSimDir != null) return ResourceUtil.localSimDir;

		ResourceUtil.localSimDir = new File(ResourceUtil.getLocalRootDir(), userSubDirName);
		if (!ResourceUtil.localSimDir.exists() && localSimDir.mkdirs()) throw new RuntimeException("Error initializing the sim directory '" + localSimDir + "'");

		File[] localFiles = localSimDir.listFiles();
		if (localFiles == null) throw new IOError(new IllegalStateException("OS declares `" + ResourceUtil.localSimDir + "` is a directory, but java.io cannot list said directory!"));

		for (File file : localFiles) {
			if(file.isDirectory() && file.getName().endsWith(LOCAL_SOLVER_LIB_LINK_SUFFIX)) {
				File[] links = file.listFiles();
				if (links == null) throw new IOError(new IllegalStateException("OS declares `" + file + "` is a directory, but java.io cannot list said directory!"));
				for (File link : links) {
					if (link.delete()) continue;
					logger.warn("Unable to delete `{}` in directory: `{}`", link.getName(), file.getAbsolutePath());
				}
			}
			if (!file.delete()) logger.warn("Unable to delete `{}`", file.getAbsolutePath());
		}

		return ResourceUtil.localSimDir;
	}

	public static void writeResourceToFile(String resname, File file) throws IOException{
		java.net.URL url = ResourceUtil.class.getResource(resname);
		if (url == null) throw new RuntimeException("ResourceUtil::writeFileFromResource() : Can't get resource for " + resname);

		try (BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));) {
			byte[] byteArray = new byte[10000];
			while (true) {
				int numRead = bis.read(byteArray, 0, byteArray.length);
				if (numRead == -1) break;
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
		if (url == null) throw new RuntimeException("ResourceUtil::resourceToString() : Can't get resource for " + resname);

		try (BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream()) ) {
			try (Reader r = new InputStreamReader(bis, StandardCharsets.UTF_8)) {
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[1024];
				int bytes = r.read(buffer, 0, buffer.length);
				while (bytes > 0) {
					sb.append(buffer, 0, bytes);
					bytes = r.read(buffer, 0, buffer.length);
				}
				return sb.toString();
			} catch (IOException e) {
				logger.warn("Can't extract " + resname, e);
			}
		} catch (IOException e1) {
			logger.warn("Can't get " + resname, e1);
		}
		return "not found";
	}

	@NoLogging
	public static File getVcellHome() {
		if (ResourceUtil.vcellHome != null) return ResourceUtil.vcellHome;
		ResourceUtil.vcellHome = new File(getUserHomeDir(), VCELL_HOME_DIR_NAME);
		if (!ResourceUtil.vcellHome.exists() && !ResourceUtil.vcellHome.mkdirs())
			throw new RuntimeException("Error initializing the VCell home directory: " + VCELL_HOME_DIR_NAME);
		return vcellHome;
	}

	/**
	 * directory to cache licensed files download from vcell.org
	 */
	public static File getDownloadDirectory(){
		if(ResourceUtil.downloadDirectory != null) return ResourceUtil.downloadDirectory;
		ResourceUtil.downloadDirectory = new File(getVcellHome(), VCELL_DOWNLOAD_DIR_NAME);
		if (!ResourceUtil.downloadDirectory.exists() && !ResourceUtil.downloadDirectory.mkdirs())
			throw new RuntimeException("Error initializing the download directory: " + VCELL_DOWNLOAD_DIR_NAME);
		return ResourceUtil.downloadDirectory;
	}

	/**
	 * create Solvers Directory, if necessary
	 * check last version of software which used directory, delete contents of directory if different
	 * @return directory of locally run solvers
	 */
	public static File getLocalSolversDirectory() {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance( );
		final File localSolversRootDir = new File(getVCellInstall(), LOCALSOLVERS_DIR);
		final File localSolversOSDir = new File(localSolversRootDir, osi.getNativeLibDirectory());
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

	public static String getBNG2StandaloneWin(boolean b64bit){
		if(b64bit){
			return new File(getBNGRoot(),"win64Standalone/BNG2.exe").getAbsolutePath();
		}
		return new File(getBNGRoot(),"win32Standalone/BNG2_32bit.exe").getAbsolutePath();
	}
	
	public static File getVCellInstall() {
		return PropertyLoader.getRequiredDirectory(PropertyLoader.installationRoot);
	}
	
	public static String getVCellJava() {
		final String defaultJavaCmd = "java";
		String javaCmd = defaultJavaCmd;
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		if(osi.isMac()) {
			File searchThis = getVCellInstall();
			while(!(searchThis.getName().startsWith("VCell") && searchThis.getName().endsWith(".app"))) {
				searchThis = searchThis.getParentFile();
				if (searchThis == null) break;
			}
			if (searchThis != null) {
				File vcellJava = new File(searchThis,"Contents/PlugIns/jre.bundle/Contents/Home/jre/bin/java"+osi.getExeSuffix());
				if (vcellJava.exists()) javaCmd = vcellJava.getAbsolutePath();
			}
			// /Applications/VCell_Rel.app/Contents/PlugIns/jre.bundle/Contents/Home/jre/bin/java
		}else if(osi.isWindows() || osi.isLinux()) {
			File vcellJava = new File(ResourceUtil.getVCellInstall(),"jre/bin/java"+osi.getExeSuffix());
			if(vcellJava.exists()) javaCmd = vcellJava.getAbsolutePath();
		}
		if(javaCmd.equals(defaultJavaCmd)) {
			Exception e = new Exception("Failed to find java executable in installation dir '"+ResourceUtil.getVCellInstall()+"'");
			logger.error(e.getMessage(), e);
		}
		return javaCmd;
	}

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