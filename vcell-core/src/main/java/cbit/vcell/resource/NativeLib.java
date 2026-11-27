package cbit.vcell.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.util.NativeLoader;
import org.vcell.util.OperatingSystemInfo;

/**
 * Known / named collections of libraries VCell needs to dynamically load
 */
public enum NativeLib {
	// No native libraries at present. HDF5 was the last one: writing HDF5 exports moved to the
	// pure-java io.jhdf in #2001, and reading moved earlier in #1903 and #1906.
	//
	// The loading machinery is kept rather than deleted, because whether VCell needs a native
	// library again is not something this file should decide. Add an entry here and it works.
	;

	private static final Logger logger = LogManager.getLogger(NativeLib.class);

	private final String libName;
	public final boolean autoLoad;
	private boolean loaded;
	private final String[] dependentLibNames;

	private NativeLib(String libName, boolean autoLoad, String... dependentLibNames) {
		this.libName = libName;
		this.autoLoad = autoLoad;
		this.loaded = false;
		this.dependentLibNames = dependentLibNames;
	}

	public void load( ) {
		if (this.loaded) return;
		logger.info("Loading " + this.libName);
		for (String name: this.dependentLibNames){
			if (name == null) continue;
			NativeLoader.load(name);
		}

		NativeLoader.load(this.libName);
		this.loaded = true;
	}

	public String toString(){
		return this.libName;
	}
}
