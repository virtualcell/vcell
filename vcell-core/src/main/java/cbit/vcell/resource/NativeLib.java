package cbit.vcell.resource;

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
		String libFileName = this.getLibraryFileName();

		Path path = Paths.get(installDir, nativeLibDir, osDir, libFileName);
		return path.toAbsolutePath().toString();
	}

	private String getLibraryFileName(){
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String fileName = osi.isWindows() ? "" : "lib" ; // unix library start with lib
		fileName += this.libName;
		return fileName + "." + this.getAppropraiteLibrarySuffix();
	}

	private String getAppropraiteLibrarySuffix(){
		String suff;
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		if (osi.isWindows()){
			suff = "dll";
		} else if (osi.isMac()){
			suff = "jnilib";
		} else if (osi.isLinux()){
			suff = "so";
		} else {
			throw new RuntimeException("Unknown OS type encountered when trying to load dynamic library");
		}
		return suff;
	}

}
