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
import cbit.vcell.export.gloworm.atoms.*;
import java.util.zip.*;
import java.util.*;
/**
 * This type was created in VisualAge.
 */
public class MediaTrack extends MediaMethods {
	private MediaChunk[] chunks;
	private int[] sampleDurations = null;
	private boolean[] keyFrames = null;
	private int numberOfSamples;
	private String[] dataReferences;
	private String[] dataReferenceTypes;
	private int duration;
	private String mediaType = null;
	private int width;
	private int height;
	private String[] dataFormats = null;
	private ChunkID[] chunkIDs = null;
	private SampleDescriptionEntry[] sampleDescriptionEntries;
	private int[] sampleSizes = null;
	private Edit[] edits = null;

/**
 * Media constructor comment.
 */
public MediaTrack(MediaChunk[] mediaChunks) throws DataFormatException {
	String mediaType = mediaChunks[0].getMediaType();
	int width = 0;
	int height = 0;
	boolean video = mediaChunks[0] instanceof VideoMediaChunk;
	if (video) {
		width = ((VideoMediaChunk)mediaChunks[0]).getWidth();
		height = ((VideoMediaChunk)mediaChunks[0]).getHeight();
	}
	boolean goodChunks = true;
	int i = 0;
	while ((goodChunks) && (i < mediaChunks.length)) {
		if (! mediaChunks[i].getMediaType().equals(mediaType)) goodChunks = false;
		if (video)
			if (
				(((VideoMediaChunk)mediaChunks[i]).getWidth() != width) ||
				(((VideoMediaChunk)mediaChunks[i]).getHeight() != height)
			   ) goodChunks = false;
		i++;
	}
	if (! goodChunks) throw new DataFormatException("Bad Media Chunk Array !");
	else {
		int numberOfSamples = 0;
		int duration = 0;
		for (int j=0;j<mediaChunks.length;j++) {
			numberOfSamples += mediaChunks[j].getNumberOfSamples();
			duration += mediaChunks[j].getDuration();
		}
		String[] dataFormats = new String[numberOfSamples];
		boolean[] keyFrames = new boolean[numberOfSamples];
		int[] sampleDurations = new int[numberOfSamples];
		int[] sampleSizes = new int[numberOfSamples];
		int index = 0;
		for (int j=0;j<mediaChunks.length;j++) {
			for (int k=0;k<mediaChunks[j].getNumberOfSamples();k++) {
				dataFormats[index] = mediaChunks[j].getMediaSampleInfos()[k].getDataFormat();
				keyFrames[index] = mediaChunks[j].getMediaSampleInfos()[k].isKeyFrame();
				sampleDurations[index] = mediaChunks[j].getMediaSampleInfos()[k].getDuration();
				sampleSizes[index] = mediaChunks[j].getMediaSampleInfos()[k].getSize();
				index++;
			}
		}
		Vector references = new Vector();
		Hashtable refTypeHash = new Hashtable();
		Vector IDs = new Vector();
		for (int j=0;j<mediaChunks.length;j++) {
			if (! references.contains(mediaChunks[j].getDataReference())) {
				references.addElement(mediaChunks[j].getDataReference());
				refTypeHash.put(mediaChunks[j].getDataReference(), mediaChunks[j].getDataReferenceType());
			}
			if (! IDs.contains(mediaChunks[j].getDataReference()+mediaChunks[j].getDataFormat()))
				IDs.addElement(mediaChunks[j].getDataReference()+mediaChunks[j].getDataFormat());
		}
		String[] dataReferences = new String[references.size()];
		String[] dataReferenceTypes = new String[references.size()];
		references.copyInto(dataReferences);
		for (int j = 0; j < dataReferences.length; j++){
			dataReferenceTypes[j] = (String)refTypeHash.get(dataReferences[j]);
		}
		ChunkID[] chunkIDs = new ChunkID[mediaChunks.length];
		SampleDescriptionEntry[] sampleDescriptionEntries = new SampleDescriptionEntry[IDs.size()];
		int referenceIndex = 0;
		int descriptionID = 0;
		for (int j=0;j<mediaChunks.length;j++) {
			referenceIndex = references.indexOf(mediaChunks[j].getDataReference());
			mediaChunks[j].getSampleDescriptionEntry().setDataReferenceIndex(referenceIndex + 1);
			descriptionID = IDs.indexOf(mediaChunks[j].getDataReference()+mediaChunks[j].getDataFormat());
			sampleDescriptionEntries[descriptionID] = mediaChunks[j].getSampleDescriptionEntry();
			chunkIDs[j] = new ChunkID(mediaChunks[j].getNumberOfSamples(), descriptionID + 1);
		}
		setChunks(mediaChunks);
		setMediaType(mediaType);
		setWidth(width);
		setHeight(height);
		setNumberOfSamples(numberOfSamples);
		setDuration(duration);
		setDataFormats(dataFormats);
		setKeyFrames(keyFrames);
		setSampleDurations(sampleDurations);
		setDataReferences(dataReferences);
		setDataReferenceTypes(dataReferenceTypes);
		setChunkIDs(chunkIDs);
		setSampleDescriptionEntries(sampleDescriptionEntries);
		setSampleSizes(sampleSizes);
	}
}


/**
 * This method was created in VisualAge.
 * @param mediaChunk MediaChunk
 */
public MediaTrack(MediaChunk mediaChunk) throws DataFormatException {
	this(new MediaChunk[] {mediaChunk});
}


/**
 * This method was created in VisualAge.
 * @return ChunkID[]
 */
public ChunkID[] getChunkIDs() {
	return chunkIDs;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public int[] getChunkOffsets() {
	int[] offsets = new int[getChunks().length];
	for (int i=0;i<offsets.length;i++) offsets[i] = getChunks()[i].getOffset();
	return offsets;
}


/**
 * This method was created in VisualAge.
 * @return MediaChunk[]
 */
public MediaChunk[] getChunks() {
	return chunks;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public java.lang.String[] getDataFormats() {
	return dataFormats;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String[] getDataReferences() {
	return dataReferences;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String[] getDataReferenceTypes() {
	return dataReferenceTypes;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDuration() {
	return duration;
}


/**
 * This method was created in VisualAge.
 * @return Edit[]
 */
public Edit[] getEdits() {
	return edits;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getHeight() {
	return height;
}


/**
 * This method was created in VisualAge.
 * @return boolean[]
 */
public boolean[] getKeyFrames() {
	return keyFrames;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getMediaType() {
	return mediaType;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfSamples() {
	return numberOfSamples;
}


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry[]
 */
public SampleDescriptionEntry[] getSampleDescriptionEntries() {
	return sampleDescriptionEntries;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public int[] getSampleDurations() {
	return sampleDurations;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public int[] getSampleSizes() {
	return sampleSizes;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getWidth() {
	return width;
}


/**
 * This method was created in VisualAge.
 * @param newValue ChunkID[]
 */
private void setChunkIDs(ChunkID[] newValue) {
	this.chunkIDs = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue MediaChunk[]
 */
private void setChunks(MediaChunk[] newValue) {
	this.chunks = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String[]
 */
private void setDataFormats(java.lang.String[] newValue) {
	this.dataFormats = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
private void setDataReferences(String[] newValue) {
	this.dataReferences = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
private void setDataReferenceTypes(String[] newValue) {
	this.dataReferenceTypes = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setDuration(int newValue) {
	this.duration = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue Edit[]
 */
public void setEdits(Edit[] newValue) {
	this.edits = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setHeight(int newValue) {
	this.height = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue boolean[]
 */
private void setKeyFrames(boolean[] newValue) {
	this.keyFrames = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
private void setMediaType(String newValue) {
	this.mediaType = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setNumberOfSamples(int newValue) {
	this.numberOfSamples = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue SampleDescriptionEntry[]
 */
private void setSampleDescriptionEntries(SampleDescriptionEntry[] newValue) {
	this.sampleDescriptionEntries = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int[]
 */
private void setSampleDurations(int[] newValue) {
	this.sampleDurations = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int[]
 */
private void setSampleSizes(int[] newValue) {
	this.sampleSizes = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setWidth(int newValue) {
	this.width = newValue;
}
}
