/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
/**
 * This type was created in VisualAge.
 */
public interface ExportConstants {

	// image mirroring types
	public final static int NO_MIRRORING = 0;
	public final static int MIRROR_LEFT = 1;
	public final static int MIRROR_TOP = 2;
	public final static int MIRROR_RIGHT = 3;
	public final static int MIRROR_BOTTOM = 4;

	// color modes
	public final static int GRAYSCALE = 0;
	public final static int COLOR1 = 1;
	public final static int RAW_RGB = 0;

	// image file formats
	public final static int GIF = 0;
	public final static int ANIMATED_GIF = 1;
	public final static int TIFF = 2;
	public final static int JPEG = 3;

	// image compression formats
	public final static int UNCOMPRESSED = 0;
	public final static int COMPRESSED_GIF_DEFAULT = 1;
	public final static int COMPRESSED_LZW = 2;
	public final static int COMPRESSED_JPEG_DEFAULT = 3;

	// ascii file formats
	public final static int CSV = 0;

	// raster file formats
	public final static int NRRD_SINGLE = 0;
	public final static int NRRD_BY_TIME = 1;
	public final static int NRRD_BY_VARIABLE = 2;
		
	// export destination
	public static final int LOCAL_FILES = 0;
	public static final int REMOTE_ZIPFILE = 1;

	// exportable data types
	enum DataType {
		ODE_VARIABLE_DATA,
		PDE_VARIABLE_DATA,
		PDE_PARTICLE_DATA
	} 

	// simulation data types
	public static final int NO_DATA_AVAILABLE = 0;
	public static final int ODE_SIMULATION = 1;
	public static final int PDE_SIMULATION_NO_PARTICLES = 2;
	public static final int PDE_SIMULATION_WITH_PARTICLES = 3;

	// export modes
	public static final int TIME_POINT = 0;
	public static final int TIME_RANGE = 1;
	public static final int VARIABLE_ONE = 0;
	public static final int VARIABLE_MULTI = 1;
	public static final int VARIABLE_ALL = 2;
	public static final int GEOMETRY_SELECTIONS = 0;
	public static final int GEOMETRY_SLICE = 1;
	public static final int GEOMETRY_FULL = 2;	
	
}
