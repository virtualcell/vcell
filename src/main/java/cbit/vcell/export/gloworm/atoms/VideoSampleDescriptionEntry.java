/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;
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
