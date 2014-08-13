package cbit.vcell.resource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cbit.vcell.util.NativeLoader;

/**
 * Known / named collections of library
 * @author gweatherby
 */
public enum NativeLib {
	VTK("vtk"),
	NATIVE_SOLVERS("NativeSolvers"),
	SBML("sbmlj"),
	COPASI("vcellCopasiOptDriver");
	
	private final String libName;
	private boolean loaded = false;

	private NativeLib(String libName) {
		this.libName = libName;
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
			e.printStackTrace();
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
