package cbit.vcell.util;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;

/**
 * Class to load native dynamic libraries.
 * 
 */
public class NativeLoader {

	private final static Logger logger = LogManager.getLogger(NativeLoader.class);

	public final static int NUM_ATTEMPTS = 50; // maximum number of times to try loading libraries before giving up and throwing exeption
	private final static String libPrefix = "lib"; //default for Mac and linux
	
	public static void load(String libName){
		String path = NativeLoader.generatePath(libName);
		System.setProperty("ncsa.hdf.hdf5lib.H5.hdf5lib", path);
		System.load(path);
		Runtime.getRuntime().load(path);
	}


	private static String generatePath(String libName){
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String installDir = ResourceUtil.getVCellInstall().getAbsolutePath();
		String nativeLibDir = "nativelibs";
		String osDir = osi.getNativeLibDirectory();
		String libFileName = NativeLoader.getLibraryFileName(Paths.get(installDir, nativeLibDir, osDir), libName);

		Path path = Paths.get(installDir, nativeLibDir, osDir, libFileName);
		return path.toAbsolutePath().toString();
	}

	private static String getLibraryFileName(Path partialAbsPath, String libName){
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String fileName = osi.isWindows() ? "" : libPrefix ; // unix library start with lib
		fileName += libName;
		return fileName + "." + NativeLoader.getAppropraiteLibrarySuffix(partialAbsPath, fileName);
	}

	private static String getAppropraiteLibrarySuffix(Path partialAbsPath, String fileName){
		String suff;
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		if (osi.isWindows()){
			suff = "dll";
		} else if (osi.isMac()){
			suff = NativeLoader.getMacLibSuffix(partialAbsPath.toAbsolutePath().toString(), fileName);
		} else if (osi.isLinux()){
			suff = "so";
		} else {
			throw new RuntimeException("Unknown OS type encountered when trying to load dynamic library");
		}
		return suff;
	}
	
	private static String getMacLibSuffix(String partialAbsPath, String fileName) {
		// Local list to keep track of:
		String[] validSuffixes = {"dylib", "jnilib"};
		for (String suff : validSuffixes) {
			Path p = Paths.get(partialAbsPath, fileName + "." + suff);
			if (Files.exists(p)) return suff;
		}
		// If execution gets here, we have a problem
		String[] parts =  fileName.split("(\\|/)+");
		String libName = parts[(parts.length) - 1];
		throw new RuntimeException("Was not able to find MacOS version of dynamic library: " + libName);
	}
}
