package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public interface SimDataConstants {

	public static final String ODE_DATA_IDENTIFIER = "ODEData logfile";
	public static final String SIMPLE_ODE_DATA_FORMAT_ID = "SimpleODEData binary format version 1";
	public static final String GENERIC_ODE_DATA_FORMAT_ID = "GenericODEData binary format version 1";
	public static final String COMPACT_ODE_DATA_FORMAT_ID = "CompactODEData binary format version 1";
	public static final int STATE_VARIABLE = 1;
	public static final String NO_VARIABLE = "";	
	public static final double NO_TIME = -1;	
		
	public static final String LOGFILE_EXTENSION = ".log";
	public static final String PDE_DATA_EXTENSION = ".sim";
	public static final String ODE_DATA_EXTENSION = ".ode";
	public static final String PARTICLE_DATA_EXTENSION = ".particle";

	public static final int PDE_DATA = 0;
	public static final int ODE_DATA = 1;
	public static final int PARTICLE_DATA = 2;
	
}
