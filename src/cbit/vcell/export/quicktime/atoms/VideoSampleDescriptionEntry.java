package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public abstract class VideoSampleDescriptionEntry extends SampleDescriptionEntry {
	protected short version;
	protected short revisionLevel;
	protected String vendor;
	protected int temporalQuality;
	protected int spatialQuality;
	protected short width;
	protected short height;
	protected int horizontalResolution;
	protected int verticalResolution;
	protected int dataSize;
	protected short frameCount;
	protected byte[] compressorName = new byte[32];
	protected short depth;
	protected short colorTableID;
}
