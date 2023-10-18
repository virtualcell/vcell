package cbit.vcell.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.util.NativeLoader;

/**
 * Known / named collections of libraries VCell needs to dynamically load
 */
public enum NativeLib {
	// enum values
	combinej("combinej", true),
	HDF5("jhdf5", false,
			(OperatingSystemInfo.getInstance().isWindows()) ? "msvcr100" : null );

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
