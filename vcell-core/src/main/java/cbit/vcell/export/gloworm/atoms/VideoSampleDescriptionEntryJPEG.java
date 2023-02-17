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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
/**
 * This type was created in VisualAge.
 */
public class VideoSampleDescriptionEntryJPEG extends VideoSampleDescriptionEntry {
	private final static Logger lg = LogManager.getLogger(VideoSampleDescriptionEntryJPEG.class);

	public final static int SIZE = 86;
	public final static String DATA_FORMAT = "jpeg";

public VideoSampleDescriptionEntryJPEG(int frameWidth, int frameHeight) {
	this((short)frameWidth, (short)frameHeight, defaultFrameResolution, defaultColorDepth, (short)0);
}


public VideoSampleDescriptionEntryJPEG(int frameWidth, int frameHeight, int referenceIndex) {
	this((short)frameWidth, (short)frameHeight, defaultFrameResolution, defaultColorDepth, (short)referenceIndex);
}


public VideoSampleDescriptionEntryJPEG(int frameWidth, int frameHeight, int frameResolution, int colorDepth, int referenceIndex) {
	this((short)frameWidth, (short)frameHeight, frameResolution, (short)colorDepth, (short)referenceIndex);
}


public VideoSampleDescriptionEntryJPEG(short frameWidth, short frameHeight, int frameResolution, short colorDepth, short referenceIndex) {
	size = SIZE;
	dataFormat = DATA_FORMAT;
	dataReferenceIndex = referenceIndex;
	version = (short)0;
	revisionLevel = (short)0;
	vendor = "appl";
	temporalQuality = lossLessQuality;
	spatialQuality = lossLessQuality;
	width = frameWidth;
	height = frameHeight;
	horizontalResolution = frameResolution;
	verticalResolution = frameResolution;
	dataSize = defaultDataSize;
	frameCount = (short)1;
	compressorName[0] = (byte)13; for (int i=0;i<12;i++) compressorName[i+1] = "Photo - JPEG".getBytes()[i];
	depth = colorDepth;
	colorTableID = defaultColorTableID;
}


public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(dataFormat);
		out.write(reserved);
		out.writeShort(dataReferenceIndex);
		out.writeShort(version);
		out.writeShort(revisionLevel);
		out.writeBytes(vendor);
		out.writeInt(temporalQuality);
		out.writeInt(spatialQuality);
		out.writeShort(width);
		out.writeShort(height);
		out.writeInt(horizontalResolution);
		out.writeInt(verticalResolution);
		out.writeInt(dataSize);
		out.writeShort(frameCount);
		out.write(compressorName);
		out.writeShort(depth);
		out.writeShort(colorTableID);
		return true;
	} catch (IOException e) {
		lg.error("Unable to write: " + e.getMessage(), e);
		return false;
	}
}
}
