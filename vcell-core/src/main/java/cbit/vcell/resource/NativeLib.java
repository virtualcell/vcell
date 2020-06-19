package cbit.vcell.resource;

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

	/**
	 * find whether underlying thread is complete
	 * @return
	 */
	public boolean isDone( ) {
		return NativeLoader.load(libName).isDone();
	}

}
