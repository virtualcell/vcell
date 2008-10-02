package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public interface SimDataConstants {

	public static final String KEEP_MOST = "KeepMost";
	
	public static final String ODE_DATA_IDENTIFIER = "ODEData logfile";
	public static final String IDA_DATA_IDENTIFIER = "IDAData logfile";
	public static final String STOCH_DATA_IDENTIFIER = "StochData logfile";//stoch
	public static final String SIMPLE_ODE_DATA_FORMAT_ID = "SimpleODEData binary format version 1";
	public static final String GENERIC_ODE_DATA_FORMAT_ID = "GenericODEData binary format version 1";
	public static final String COMPACT_ODE_DATA_FORMAT_ID = "CompactODEData binary format version 1";
	public static final String COMPACT_STOCH_DATA_FORMAT_ID = "CompactSTOCHData binary format version 1"; //stoch
	public static final String IDA_DATA_FORMAT_ID = "IDAData text format version 1";
	public static final int STATE_VARIABLE = 1;
	public static final String NO_VARIABLE = "";	
	public static final double NO_TIME = -1;	
		
	public static final String FUNCTIONFILE_EXTENSION = ".functions";
	public static final String MESHFILE_EXTENSION = ".mesh";
	public static final String LOGFILE_EXTENSION = ".log";
	public static final String PDE_DATA_EXTENSION = ".sim";
	public static final String ODE_DATA_EXTENSION = ".ode";
	public static final String IDA_DATA_EXTENSION = ".ida";
	public static final String IDAINPUT_DATA_EXTENSION = ".idaInput";
	public static final String CVODEINPUT_DATA_EXTENSION = ".cvodeInput";
	public static final String STOCH_DATA_EXTENSION = ".stochbi"; //stoch
	public static final String PARTICLE_DATA_EXTENSION = ".particle";
	public static final String ZIPFILE_EXTENSION = ".zip";
	public static final String FIELDDATARESAMP_EXTENSION = ".fdat";

	public static final int PDE_DATA = 0;
	public static final int ODE_DATA = 1;
	public static final int PARTICLE_DATA = 2;
	public static final int STOCH_DATA = 3; //stoch
	
}
