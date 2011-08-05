/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;

/**
 * Insert the type's description here.
 * Creation date: (11/8/2005 10:54:49 PM)
 * @author: Ion Moraru
 */
public class ObjectMediaChunk implements MediaChunk {
	private ObjectMediaSample[] samples;
	private int offset;
	private String dataReference = "self";
	private String dataReferenceType = "alis";

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:58:05 PM)
 * @param vrWorld VRWorld
 */
public ObjectMediaChunk(VRWorld vrWorld) {
	samples = new ObjectMediaSample[vrWorld.getObjectNodeIndices().length];
	for (int i = 0; i < samples.length; i++){
		samples[i] = new ObjectMediaSample(vrWorld, vrWorld.getObjectNodeIndices()[i]);
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Byte[]
 */
public byte[] getDataBytes() {
	byte[] bytes = new byte[getSize()];
	int idx = 0;
	for (int i = 0; i < samples.length; i++){
		byte[] sampleBytes = samples[i].getDataBytes();
		for (int j = 0; j < sampleBytes.length ; j++){
			bytes[idx] = sampleBytes[j];
			idx++;
		}
	}
	return bytes;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataFormat() {
	return samples[0].getDataFormat();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public String getDataReference() {
	return dataReference;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public String getDataReferenceType() {
	return dataReferenceType;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDuration() {
	return samples[0].getDuration();
}


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
public MediaSampleInfo[] getMediaSampleInfos() {
	return samples;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getMediaType() {
	return samples[0].getMediaType();
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfSamples() {
	return samples.length;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getOffset() {
	return offset;
}


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
public SampleDescriptionEntry getSampleDescriptionEntry() {
	return samples[0].getSampleDescriptionEntry();
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getSize() {
	int size = 0;
	for (int i = 0; i < samples.length; i++){
		size += samples[i].getSize();
	}
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (11/26/2005 7:25:24 PM)
 * @return boolean
 * @param file java.io.File
 */
public boolean isDataInFile(java.io.File file) {
	return false;
}


/**
 * This method was created in VisualAge.
 * @param dataReference java.lang.String
 */
public void setDataReference(String dataReference) {
	this.dataReference = dataReference;
}


/**
 * This method was created in VisualAge.
 * @param dataReference java.lang.String
 */
public void setDataReferenceType(String dataReferenceType) {
	this.dataReferenceType = dataReferenceType;
}


/**
 * This method was created in VisualAge.
 * @param offset int
 */
public void setOffset(int offset) {
	this.offset = offset;
}
}
