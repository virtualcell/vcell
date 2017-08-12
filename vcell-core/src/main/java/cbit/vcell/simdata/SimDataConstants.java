/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;


/**
 * This type was created in VisualAge.
 */
public interface SimDataConstants {

	public static final String KEEP_MOST = "KeepMost";
	
	public static final String PROPERTY_NAME_DATAIDENTIFIERS = "dataIdentifiers";
	public static final String PDE_DATA_MANAGER_CHANGED = "PDE_DATA_MANAGER_CHANGED";
	
	public static final String ODE_DATA_IDENTIFIER = "ODEData logfile";
	public static final String IDA_DATA_IDENTIFIER = "IDAData logfile";
	public static final String NFSIM_DATA_IDENTIFIER = "NFSimData logfile";
	public static final String NETCDF_DATA_IDENTIFIER = "NetCDFData logfile";//stoch
	public static final String SIMPLE_ODE_DATA_FORMAT_ID = "SimpleODEData binary format version 1";
	public static final String GENERIC_ODE_DATA_FORMAT_ID = "GenericODEData binary format version 1";
	public static final String COMPACT_ODE_DATA_FORMAT_ID = "CompactODEData binary format version 1";
	public static final String NETCDF_DATA_FORMAT_ID = "NetCDFData binary format version 2"; //netcdf for hybrid stoch
	public static final String IDA_DATA_FORMAT_ID = "IDAData text format version 1";
	public static final int STATE_VARIABLE = 1;
	public static final String NO_VARIABLE = "";	
	public static final double NO_TIME = -1;	
		
	public static final String FUNCTIONFILE_EXTENSION = ".functions";
	public static final String MESHFILE_EXTENSION = ".mesh";
	public static final String MESHMETRICSFILE_EXTENSION = ".meshmetrics";
	public static final String LOGFILE_EXTENSION = ".log";
	public static final String PDE_DATA_EXTENSION = ".sim";
	public static final String ODE_DATA_EXTENSION = ".ode";
	public static final String IDA_DATA_EXTENSION = ".ida";
	public static final String NETCDF_DATA_EXTENSION = ".nc";
	public static final String IDAINPUT_DATA_EXTENSION = ".idaInput";
	public static final String CVODEINPUT_DATA_EXTENSION = ".cvodeInput";
	public static final String STOCHINPUT_DATA_EXTENSION = ".stochInput";
	public static final String STOCH_DATA_EXTENSION = ".stochbi"; //stoch
	public static final String MOVINGBOUNDARYINDEX_FILE_EXTENSION = ".movingboundaryindex";
	public static final String MOVINGBOUNDARY_OUTPUT_EXTENSION = ".h5";
	public static final String COMSOL_OUTPUT_EXTENSION = ".comsoldat";
	public static final String SIMTASKXML_EXTENSION = ".simtask.xml";
//	public static final String PARTICLE_DATA_EXTENSION = ".particle";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION = ".dataProcOutput";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION_HDF5 = ".hdf5";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION_TIMES = "Times";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION_TIMEPREFIX = "Time";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS = "VariableStatistics";
	public static final String DATA_PROCESSING_OUTPUT_EXTENSION_POSTPROCESSING = "PostProcessing";
	
	public static final String DATA_PROCESSING_OUTPUT_ORIGINX = "OriginX";
	public static final String DATA_PROCESSING_OUTPUT_ORIGINY = "OriginY";
	public static final String DATA_PROCESSING_OUTPUT_ORIGINZ = "OriginZ";
	public static final String DATA_PROCESSING_OUTPUT_EXTENTX = "ExtentX";
	public static final String DATA_PROCESSING_OUTPUT_EXTENTY = "ExtentY";
	public static final String DATA_PROCESSING_OUTPUT_EXTENTZ = "ExtentZ";
	
	public static final String ZIPFILE_EXTENSION = ".zip";
	public static final String FIELDDATARESAMP_EXTENSION = ".fdat";
	
	public static final String JAVA_INPUT_EXTENSION = ".javaInput";
	public static final String HISTOGRAM_INDEX_NAME = "TrialNo";

	public static final int PDE_DATA = 0;
	public static final int ODE_DATA = 1;
	public static final int PARTICLE_DATA = 2;
	public static final int STOCH_DATA = 3; //stoch
	
	public static final String NFSIM_INPUT_FILE_EXTENSION = ".nfsimInput";
	public static final String NFSIM_OUTPUT_FILE_EXTENSION = ".gdat";
	public static final String NFSIM_OUTPUT_LOG_EXTENSION = ".log";
	
	public static final String SMOLDYN_INPUT_FILE_EXTENSION = ".smoldynInput";
	public static final String SMOLDYN_OUTPUT_FILE_EXTENSION = ".smoldynOutput";
	public static final String SMOLDYN_HIGH_RES_VOLUME_SAMPLES_EXTENSION = ".hrvs";

	public static final String SUBDOMAINS_FILE_SUFFIX = ".subdomains";

	public static final String VCG_FILE_EXTENSION = ".vcg";
	
	public static final double BASEFAB_REAL_SETVAL = 1.23456789e+300;

	
}
