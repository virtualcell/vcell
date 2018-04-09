package cbit.vcell.util;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.FileUtils;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.OperatingSystemInfo.OsType;

/**
 * class to load native libraries. Requires external class to set
 * {@link #setNativeLibraryDirectory(String)} and {@link #setOsType(OsType)}
 * prior to creating NativeLoader object.
 * 
 * @author gweatherby
 *
 */
public class NativeLoader {

	/**
	 * maximum number of times to try loading libraries before giving up
	 * and throwing exeption
	 */
	public static final int NUM_ATTEMPTS = 50;
	
	/**
	 * regex for linux shared libraries
	 */
	private final static String LINUX_REGEX = ".*so[.\\d]*$";
	/**
	 * regex for Windows shared libraries
	 */
	private final static String WINDOWS_REGEX = ".*dll$";
	/**
	 * regex for Mac OS X shared libraries
	 */
	private final static String MAC_REGEX = ".*[jni|dy]lib";
	
	private static String nativeLibraryDirectory  = null;
	
	private static final String NATIVE_PATH_PROP = "java.library.path";
	
	/**
	 * pool for executing loads. Javadocs indicate should not consume resources when not runnning
	 */
	private static ExecutorService executor = Executors.newCachedThreadPool(new NameTheThreads());
	
	/** 
	 * store names previously called
	 */
	private static Map<String,Future<Boolean> > cache = new HashMap<String, Future<Boolean>>( );
	
	private static Logger lg = LogManager.getLogger(NativeLoader.class);
	
	/**
	 * library name path (see {@link NativeGroup} )
	 */
	static String systemLibRegex = null;
	
	/**
	 * library name prefix (see {@link NativeGroup} )
	 */
	static String libPrefix = "lib"; //default for Mac and linux

	/**
	 * preferences to use (see {@link NativeGroup} )
	 */
	static Preferences pref = Preferences.userNodeForPackage(NativeLoader.class);
	/**
	 * file separator for os (see {@link NativeGroup} )
	 */
	
	final static String FILESEP = "/" ;
	
	/**
	 * set native library directory for this OS
	 * @param nativeLibraryDirectory
	 */
	public static void setNativeLibraryDirectory(String nativeLibraryDirectory) {
		NativeLoader.nativeLibraryDirectory = nativeLibraryDirectory;
		try {
			setSystemPath();
		} catch (Exception e) {
			throw new RuntimeException("Exception setting support library path, some functionality may be lost",e);
		}
	}
	
	/**
	 * set os type
	 * @param osType
	 */
	public static void setOsType(OperatingSystemInfo.OsType osType) {
		switch (osType) {
		case LINUX:
			systemLibRegex = LINUX_REGEX;
			break;
		case WINDOWS:
			systemLibRegex = WINDOWS_REGEX; 
			libPrefix = "";
			break;
		case MAC:
			systemLibRegex = MAC_REGEX; 
			final String DFLP = "DYLD_FALLBACK_LIBRARY_PATH";
			String dflp = System.getenv(DFLP);
			if (dflp == null) {
				lg.warn(DFLP + " not set");
			}
			else if (lg.isInfoEnabled()) {
				lg.info(DFLP + " set to " + dflp);
			}
			break;
		default:
			throw new IllegalStateException("unknown os type " + osType);
		}
	}
	
	/**
	 * commence loading libraries matching pattern from previously set directory. If load already started, return prior value
	 * @param namePattern see {@link NativeGroup#NativeGroup(String, String)}
	 * @return new or existing future
	 */
	public static Future<Boolean> load(String namePattern) {
		if (nativeLibraryDirectory == null) {
			throw new IllegalStateException("load called before setNativeLibraryDirectory");
		}
		if (systemLibRegex == null) {
			throw new IllegalStateException("load called before setOsType");
		}
		if (cache.containsKey(namePattern)) {
			return cache.get(namePattern);
		}
		NativeGroup ng = new NativeGroup(namePattern, nativeLibraryDirectory);
		Future<Boolean> f = executor.submit(ng);
		cache.put(namePattern, f);
		return f;
	}
	
	/**
	 * verify native directory on system native lib path, in case other code searches for it (e.g. H5)
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void setSystemPath() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		boolean found = false;
		File nativeDir = new File(nativeLibraryDirectory);
		String jlp = System.getProperty(NATIVE_PATH_PROP);
		Collection<File> files = FileUtils.toFiles(FileUtils.splitPathString(jlp), true);
		for (File f: files) {
			if (lg.isDebugEnabled()) {
				lg.debug("native path directory " + f.getAbsolutePath());
			}
			if (nativeDir.equals(f)) {
				found = true;
				if (lg.isDebugEnabled()) {
					lg.debug(nativeLibraryDirectory + " found in directory " + f.getAbsolutePath() + " of " + NATIVE_PATH_PROP + " " + jlp);
				}
				break;
			}
		}
		if (!found) {
			files.add(nativeDir);
			String newPath = FileUtils.pathJoinFiles(files);
			System.setProperty(NATIVE_PATH_PROP,newPath);
			if (lg.isDebugEnabled()) {
				lg.debug("adding " + nativeLibraryDirectory + " to " + NATIVE_PATH_PROP + " " + jlp);
			}
			 
			//clear paths cached in JVM to trigger reparsing of NATIVE_PATH_PROP
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		}
	}
	
	/**
	 *  factory to name threads sequentially, set as daemon, and set as
	 *  minimum priority
	 */
	private static class NameTheThreads implements ThreadFactory {
		private static AtomicLong counter = new AtomicLong(0); 

		@Override
		public Thread newThread(Runnable r) {
			Thread t= new Thread(r);
			t.setName("NativeLoader " + counter.getAndAdd(1));
			t.setDaemon(true);
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}
	}
	
	/**
	 * clean (clear) preferences
	 * @throws BackingStoreException
	 */
	static void clean( ) throws BackingStoreException {
		Preferences pref = Preferences.userNodeForPackage(NativeLoader.class);
		for (String c : pref.childrenNames()) {
			pref.node(c).clear();
		}
		pref.clear( );
	}
}
