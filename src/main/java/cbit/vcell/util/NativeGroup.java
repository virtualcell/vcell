package cbit.vcell.util;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.vcell.util.FileUtils;

/**
 * 
 * represents a group of one or more native libraries
 * Not necessarily threadsafe
 * @author gweatherby
 *
 */
class NativeGroup implements Callable<Boolean> {

	/**
	 * maximum number of times to try loading libraries before giving up
	 * and throwing exeption
	 */
	public static final int NUM_ATTEMPTS = 50;

	
	private static Logger lg = Logger.getLogger(NativeGroup.class);

	/**
	 * preferences to use
	 */
	private Preferences pref = Preferences.userNodeForPackage(NativeGroup.class);
	/**
	 * map of files in native lib directory
	 */
	private Map<String,Boolean> libsPresentMap = new HashMap<String,Boolean>( );
	/**
	 * ordered list of libraries
	 */
	private List<String>  storedLoadOrder = new LinkedList<String>( );
	/**
	 * if true, stored list is out of date
	 */
	private boolean listDirty = false;
	/**
	 * path to native library directory
	 */
	private String dirPath;
	/**
	 * last recorded load error, for messaging
	 */
	private List<Error> failErrors = null;
	
	private String namePattern;

	/**
	 * our preferences key
	 */
	final static String PREFS_KEY = "nativeLibs" ;


	/*
	 * our preferences key
	 */
	final static String NUM_PREFS = "nativeLibPrefCount";

	/**
	 * @param namePattern Pattern to match beginning of (not regex)
	 * @param dirPath directory to look
	 */
	public NativeGroup(String namePattern, String dirPath ) {
		this.namePattern = NativeLoader.libPrefix + namePattern;
		this.dirPath = dirPath;
		pref = NativeLoader.pref.node(namePattern);
	}

	/**
	 * attempt to load specified libraries in nativelibs directory.
	 * try multiple times in case some libraries are dependent on others
	 * if successful, record order of libraries in local preferences file
	 * for faster subsequent startups; 
	 * rebuild local list if libraries added / removed from nativelibs directory
	 * @throws Exception if something goes wrong
	 */
	@Override
	public Boolean call() throws Exception {
		assert NativeLoader.FILESEP != null : "bad file sep";
		assert FileUtils.PATHSEP != null : "bad path sep";
		loadMap( );
		loadListFromPreferences();
		Iterator<String> iter = storedLoadOrder.iterator();
		List<String> failed = new LinkedList<String>( );
		//try to load names stored in preferences
		while (iter.hasNext()) {
			String lName = iter.next( );
			//make sure still present
			if (!libsPresentMap.containsKey(lName)) {
				listDirty = true; //library removed
				iter.remove();
				continue;
			}
			if (!attemptLoad(lName)) {
				listDirty = true; //stored order no longer works
				iter.remove();
				failed.add(lName);
				continue;
			}
			//library is present in directory and preferences and loads correctly
			libsPresentMap.put(lName, true);
		}
		iter = null;
		//check for libraries not stored in preferences
		for (Map.Entry<String,Boolean> entry : libsPresentMap.entrySet()) {
			if (!entry.getValue()) {
				listDirty = true; //library added
				failed.add(entry.getKey());
			}
		}
		if (!listDirty) {
			//nothing unexpected, we're done
			return true; 
		}

		//non-standard loop to set flag on last iteration
		for (int i = 1; failed.size() > 0 && i <= NUM_ATTEMPTS; i++) {
			if (i == NUM_ATTEMPTS) {  //record error messages on last go through
				setFailErrors();
			}
			Iterator<String> fIter = failed.iterator();
			while (fIter.hasNext()) {
				String lib = fIter.next();
				if (attemptLoad(lib)) {
					storedLoadOrder.add(lib);
					fIter.remove( );
				}
			}
		}
		checkForFailure(failed);
		storeListToPreferences();
		return true;
	}
	
	/**
	 * throw detailed exception if couldn't load everything
	 */
	private void checkForFailure(List<String> failed) { 
		if (failed.size( ) > 0) {
			String msg = "After " + NUM_ATTEMPTS + " attempts, unable to load native libs: ";
			for (Error e : failErrors) {
				msg += "\n" + e.getMessage();
			}
		throw new IllegalStateException(msg);
		}
	}
	

	/**
	 * load {@link #libsPresentMap} with listing of files in native lib directory
	 */
	private void loadMap() {
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dirPath + " is not directory");
		}
		File list[] = dir.listFiles();
		for (File f : list) {
			if (f.isFile()) {
				String name = f.getName();
				if (name.startsWith(namePattern) && name.matches(NativeLoader.systemLibRegex)) {
					libsPresentMap.put(name, false);
				}
			}
		}
		if (libsPresentMap.size() <= 0) {
			throw new IllegalArgumentException("no files matching " +namePattern + "*" + NativeLoader.systemLibRegex +
					" in directory " + dirPath);
		}
	}
	
	/**
	 * load {@link #storedLoadOrder} list from user preferences
	 */
	private void loadListFromPreferences( ) {
		final int count = pref.getInt(NUM_PREFS, 0);
		for (int i = 0; i < count;i++) {
			String depsBlob = pref.get(PREFS_KEY + i, "");
			Collection<String> paths = FileUtils.splitPathString(depsBlob);
			storedLoadOrder.addAll(paths);
		}
		listDirty = false;
	}

	/**
	 * store loadOrder list to user preferences
	 */
	private void storeListToPreferences( ) {
		String depsBlob = FileUtils.pathJoinStrings(storedLoadOrder);
		//split blob into appropriately sized chunks
		int blobCount = 0;
		while (depsBlob.length() > Preferences.MAX_VALUE_LENGTH) {
			int sep = depsBlob.lastIndexOf(FileUtils.PATHSEP, Preferences.MAX_VALUE_LENGTH); 
			String chunk = depsBlob.substring(0, sep);
			pref.put(PREFS_KEY + blobCount++,chunk);
			depsBlob = depsBlob.substring(sep + 1);
		}
		
		pref.put(PREFS_KEY + blobCount++, depsBlob);
		pref.put(NUM_PREFS,Integer.toString(blobCount));
		listDirty = false;
	}
	/**
	 * attempt to load library in #dirPath
	 * @param lib to load 
	 * @return true if loads okay
	 */
	private boolean attemptLoad(String lib)  {
		String fullpath = dirPath + NativeLoader.FILESEP + lib;
		try {
			System.load(fullpath);
			//String stub = lib.substring(0,lib.lastIndexOf('.'));
			//System.loadLibrary(stub);
			
			
			if (lg.isDebugEnabled()) {
				lg.debug("loaded "  + fullpath);
			}
			return true;
		}
		catch (Error e) {
			if (lg.isDebugEnabled()) {
				lg.debug("load attempt "  + fullpath + " failed");
			}
			//System.err.println(new File (fullpath).getAbsolutePath());
			if (isFailErrors()) {
				recordError(e);
			}
			//System.err.println(e.getMessage());
			return false;
		}
	}
	/**
	 * are we recording failure errors?
	 * @return true if yes
	 */
	private boolean isFailErrors() {
		return failErrors != null;
	}

	/**
	 * activate recording of failure errors
	 */
	private void setFailErrors() {
		failErrors = new LinkedList<Error>( );
	}
	
	/**
	 * record link #Error
	 * @param e to record
	 * @throws AssertionError if {@link #setFailErrors()} not set
	 */
	private void recordError(Error e) {
		assert failErrors != null : "logic error";
		failErrors.add(e);
	}
	
	static void clean( ) {
		Preferences pref = Preferences.userNodeForPackage(NativeGroup.class);
		final int count = pref.getInt(NUM_PREFS, 1000);
		for (int i = 0; i < count; i++) {
			pref.remove(PREFS_KEY + i);
		}
		pref.remove(PREFS_KEY);
		pref.remove(NUM_PREFS);
	}
}
