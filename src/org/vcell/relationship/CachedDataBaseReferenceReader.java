package org.vcell.relationship;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class to cache values returned from {@link DataBaseReferenceReader} during program execution for
 * faster resolving on subsequent queries.
 * 
 * A Singleton object is used; it is stored using a {@link SoftReference} to allow it to be garbage collected if necessary
 * @author gweatherby
 *
 */
public class CachedDataBaseReferenceReader {
	/**
	 * initial size of maps; concurrent maps may be expensive to resize
	 */
	private static final int INITIAL_MAP_SIZE= 200;

	/**
	 * hash of QuickGo queries 
	 */
	private Map<String,String> goMap = new ConcurrentHashMap<String, String>(INITIAL_MAP_SIZE);
	/**
	 * hash molecule lookups
	 */
	private Map<String,String> moleculeIdMap = new ConcurrentHashMap<String, String>(INITIAL_MAP_SIZE);
	/**
	 * hash chebi lookups
	 */
	private Map<String,String> chebiMap = new ConcurrentHashMap<String, String>(INITIAL_MAP_SIZE);
	/**
	 * hash uniprot lookups
	 */
	private Map<String,String> uniprotMap = new ConcurrentHashMap<String, String>(INITIAL_MAP_SIZE);

	private interface Fetcher {
		String lookup(String x) throws Exception;
	}

	private Fetcher fetchGo = new Fetcher( ) { public String lookup(String goId) throws Exception 
		{ return DataBaseReferenceReader.getGOTerm(goId); } 
	};

	private Fetcher fetchMol = new Fetcher( ) { public String lookup(String moleculeId) throws Exception 
		{ return DataBaseReferenceReader.getMoleculeDataBaseReference(moleculeId); } 
	};

	private Fetcher fetchChebi = new Fetcher( ) { public String lookup(String chebiId) throws Exception 
		{ return DataBaseReferenceReader.getChEBIName(chebiId); } 
	};

	/**
	 *  use soft reference to make eligible for garbage collection if memory runs tight
	 */
	private static SoftReference<CachedDataBaseReferenceReader> dbReader = new SoftReference<CachedDataBaseReferenceReader>(null);
	
	/**
	 * force clients to use static method so at most only single object will exists at once
	 */
	private CachedDataBaseReferenceReader( ) {
		
	}

	/**
	 * return reference to new or existing cached reader. Return value should only be assigned to local (method) variables
	 * to prevent a hard reference locking in memory
	 * @return new or existing reader
	 */
	public static CachedDataBaseReferenceReader getCachedReader( ) {
		CachedDataBaseReferenceReader r = dbReader.get();
		if (r != null) {
			return r;
		}
		//use synchronized section for thread safety
		synchronized(dbReader) {
			r = dbReader.get();
			if (r != null) {
				return r;
			}
			r = new CachedDataBaseReferenceReader();
			dbReader = new SoftReference<CachedDataBaseReferenceReader>(r);
		}
		return r;
	}
	
	//package level for unit testing
	static void clearExisting( ) {
		dbReader = new SoftReference<CachedDataBaseReferenceReader>(null);
	}

	/** 
	 * generic look in cache, then call method if miss implementation
	 */
	private String cacheLookupOrFetch(Map<String,String> map, Fetcher fetcher, String key) throws Exception {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		String rval = fetcher.lookup(key);
		if (rval != null) {
			map.put(key, rval);
		}
		return rval;
	}

	public String getMoleculeDataBaseReference(String molId) throws Exception {
		return cacheLookupOrFetch(moleculeIdMap,fetchMol,molId);
	}

	public String getChEBIName(String chebiId) throws Exception {
		return cacheLookupOrFetch(chebiMap,fetchChebi,chebiId);
	}

	public String getGOTerm(String goId) throws Exception {
		return cacheLookupOrFetch(goMap,fetchGo,goId);
	}

	/** 
	 * specific look in cache, then call method if miss implementation because two Strings
	 * are needed for lookup
	 */
	public String getMoleculeDataBaseReference(String db, String id) throws Exception { 
		String key = db + id;
		if (uniprotMap.containsKey(key)) {
			return uniprotMap.get(key);
		}
		String rval = DataBaseReferenceReader.getMoleculeDataBaseReference(db, id);
		if (rval != null) {
			uniprotMap.put(key, rval);
		}
		return rval;
	}

}
