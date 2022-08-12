package cbit.vcell.resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.util.NativeLoader;

/**
 * Known / named collections of library
 * @author gweatherby
 */
public enum NativeLib {
	combinej("combinej", true), HDF5("jhdf5",false);

	private final String libName;
	/**
	 * indicate whether these libraries should be loaded automatically at startup
	 */
	public final boolean autoload;
	private boolean loaded = false;
	private static final Logger lg = LogManager.getLogger(NativeLib.class);

	private NativeLib(String libName) {
		this.libName = libName;
		this.autoload = true;
	}


	private NativeLib(String libName, boolean autoload) {
		this.libName = libName;
		this.autoload = autoload;
	}

	/**
	 *  commence load process but don't wait for results
	 */
	public void initLoad( ) {
		NativeLoader.load(libName);
	}

	public void load( ) {
		if (loaded) {
			return;
		}
		Future<Boolean> r = NativeLoader.load(libName);
		try {
			r.get( );
		} catch (InterruptedException | ExecutionException e) {
				if (lg.isWarnEnabled()) {
					lg.warn("Can't load " + libName,e);
				}
			throw new RuntimeException("Can't load " + toString(), e);
		}
		loaded = true;
	}

	public void directLoad(){
		Runtime.getRuntime().load(this.generatePath());
	}

	/**
	 * find whether underlying thread is complete
	 * @return
	 */
	public boolean isDone( ) {
		return NativeLoader.load(libName).isDone();
	}

	private String generatePath(){
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String installDir = ResourceUtil.getVCellInstall().getAbsolutePath();
		String nativeLibDir = "nativelibs";
		String osDir = osi.getNativeLibDirectory();
		String libFileName = this.getLibraryFileName(Paths.get(installDir, nativeLibDir, osDir));

		Path path = Paths.get(installDir, nativeLibDir, osDir, libFileName);
		return path.toAbsolutePath().toString();
	}

	private String getLibraryFileName(Path partialAbsPath){
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String fileName = osi.isWindows() ? "" : "lib" ; // unix library start with lib
		fileName += this.libName;
		return fileName + "." + this.getAppropraiteLibrarySuffix(partialAbsPath, fileName);
	}

	private String getAppropraiteLibrarySuffix(Path partialAbsPath, String fileName){
		String suff;
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		if (osi.isWindows()){
			suff = "dll";
		} else if (osi.isMac()){
			suff = this.getMacLibSuffix(partialAbsPath.toAbsolutePath().toString(), fileName);
		} else if (osi.isLinux()){
			suff = "so";
		} else {
			throw new RuntimeException("Unknown OS type encountered when trying to load dynamic library");
		}
		return suff;
	}
	
	private String getMacLibSuffix(String partialAbsPath, String fileName) {
		// Local list to keep track of:
		String[] validSuffixes = {"dylib", "jnilib"};
		for (String suff : validSuffixes) {
			Path p = Paths.get(partialAbsPath, fileName + "." + suff);
			if (Files.exists(p)) return suff;
		}
		// If execution gets here, we have a problem
		throw new RuntimeException("Was not able to find MacOS version of dynamic library: " + this.libName);
	}

}
