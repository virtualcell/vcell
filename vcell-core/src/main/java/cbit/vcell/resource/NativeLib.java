package cbit.vcell.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.util.NativeLoader;

/**
 * Known / named collections of libraries VCell needs to dynamically load
 */
public enum NativeLib {
	// enum values
	combinej("combinej", true), HDF5("jhdf5", false);

	private static final Logger logger = LogManager.getLogger(NativeLib.class);

	private final String libName;
	private boolean loaded;
	public boolean autoLoad;

	private NativeLib(String libName, boolean autoLoad) {
		this.libName = libName;
		this.autoLoad = autoLoad;
		this.loaded = false;
	}

	public void load( ) {
		if (this.loaded) return;
		logger.info("Loading " + this.libName);
		NativeLoader.load(this.libName);
		this.loaded = true;
	}

	public String toString(){
		return this.libName;
	}
}
