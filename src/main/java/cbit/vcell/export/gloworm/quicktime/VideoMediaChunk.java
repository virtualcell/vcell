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


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

import cbit.vcell.export.gloworm.atoms.AtomConstants;
import cbit.vcell.export.gloworm.atoms.MediaData;
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;
import cbit.vcell.export.server.FileDataContainerManager;
/**
 * This type was created in VisualAge.
 */
public class VideoMediaChunk implements MediaChunk {
	private File dataFile;
	private String dataFormat;
	private int width;
	private int height;
	private int offset;
	private int size;
	private int duration;
	private String dataReference = "self";
	private String dataReferenceType = "alis";
	private SampleDescriptionEntry sampleDescriptionEntry = null;
	private VideoMediaSampleInfo sampleInfo = null;

	class VideoMediaSampleInfo implements MediaSampleInfo {
		private VideoMediaSampleInfo(String argFormat, int argDuration, String argType, SampleDescriptionEntry argEntry, int argSize, boolean argKey) {
			sFormat = argFormat;
			sDuration = argDuration;
			sType = argType;
			sEntry = argEntry;
			sSize = argSize;
			sKey = argKey;
		}
		private String sFormat;
		private int sDuration;
		private String sType;
		private SampleDescriptionEntry sEntry;
		private int sSize;
		private boolean sKey;
		public String getDataFormat() {
			return sFormat;
		}
		public int getDuration() {
			return sDuration;
		}
		public String getMediaType() {
			return sType;
		}
		public SampleDescriptionEntry getSampleDescriptionEntry() {
			return sEntry;
		}
		public int getSize() {
			return sSize;
		}
		public boolean isKeyFrame() {
			return sKey;
		}
	};

private void writeChunks(byte[] dataBytes, boolean bInitializeFile) throws IOException {
	// append data bytes to file and clear memory copy
	RandomAccessFile fw = null;
	try{
		fw = new RandomAccessFile(dataFile, "rw");
		fw.seek(dataFile.length());
		if (bInitializeFile) {
			fw.writeInt(0);
			fw.writeBytes(MediaData.type);
			setOffset(8);
		} else {
			setOffset((int)dataFile.length());
		}
		fw.write(dataBytes);
		// update the media data atom header
		fw.seek(0);
		fw.writeInt((int)dataFile.length());
	}finally{
		if(fw!= null){try{fw.close();}catch(Exception e){e.printStackTrace();}}
	}
}

public VideoMediaChunk(VideoMediaSample sample,FileDataContainerManager fileDataContainerManager) throws IOException{
	init(sample);
	File tempMovieFile = File.createTempFile("VideoMediaChunk", "temp");
	this.dataFile = tempMovieFile;
			
	FileDataContainerManager.FileDataContainerID fileDataContainerID =   fileDataContainerManager.getNewFileDataContainerID();
	fileDataContainerManager.manageExistingTempFile(fileDataContainerID, tempMovieFile);
	boolean bInitializeFile = true;

	writeChunks(sample.getDataBytes(),bInitializeFile);
}

/**
 * VideoMediaChunk constructor comment.
 * @throws IOException 
 */
public VideoMediaChunk(VideoMediaSample sample, File outputFile) throws IOException  {
	/* creates a chunk that puts data bytes into a file instead of holding them in memory */
	// if the file does not exist, we create it and initialize the mdat atom header
	// it the file exists it is expected that it already has a valid mdat atom header
	init(sample);
	
	if (outputFile==null){
		throw new RuntimeException("VideoMediaChunk file is null");
	}
	this.dataFile = outputFile;
	boolean bInitializeFile = !dataFile.exists();

	writeChunks(sample.getDataBytes(), bInitializeFile);
}


/**
 * VideoMediaChunk constructor comment.
 * @throws IOException 
 */
public VideoMediaChunk(VideoMediaSample sample, String dataReference, String dataReferenceType) throws DataFormatException, IOException {
	/* creates a chunk that holds only a reference, used by QuickTime VR */
	init(sample);
	
	setDataReference(dataReference);
	setDataReferenceType(dataReferenceType);
	
	this.dataFile = null;
}

private void init(VideoMediaSample sample){
	setDataFormat(sample.getDataFormat());
	setWidth(sample.getWidth());
	setHeight(sample.getHeight());
	setSampleDescriptionEntry(sample.getSampleDescriptionEntry());
	setSize(sample.getSize());
	setDuration(sample.getDuration());
	sampleInfo = new VideoMediaSampleInfo(sample.getDataFormat(), sample.getDuration(), sample.getMediaType(), sample.getSampleDescriptionEntry(), sample.getSize(), sample.isKeyFrame());
}

/**
 * This method was created in VisualAge.
 * @return byte[]
 * @throws IOException 
 */
public byte[] getDataBytes() throws IOException {
	if (dataFile==null){
		return null; // this happens for QTVR.
	}else{
		byte[] bytes = new byte[getSize()];
		RandomAccessFile fw = new RandomAccessFile(dataFile, "rw");
		fw.seek(getOffset());
		fw.read(bytes);
		fw.close();
		return bytes;
	}
}



/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataFormat() {
	return dataFormat;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataReference() {
	return dataReference;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getDataReferenceType() {
	return dataReferenceType;
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
 * @return int
 */
public int getHeight() {
	return height;
}


/**
 * This method was created in VisualAge.
 * @return SampleDescriptionEntry
 */
public MediaSampleInfo[] getMediaSampleInfos() {
	return new MediaSampleInfo[] { sampleInfo };
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public final String getMediaType() {
	return AtomConstants.MEDIA_TYPE_VIDEO;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfSamples() {
	return 1;
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
	return sampleDescriptionEntry;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getSize() {
	return size;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getWidth() {
	return width;
}


/**
 * Insert the method's description here.
 * Creation date: (11/26/2005 7:27:07 PM)
 * @return boolean
 * @param file java.io.File
 */
public boolean isDataInFile(java.io.File file) {
	return (dataFile == file);
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
private void setDataFormat(String newValue) {
	this.dataFormat = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setDataReference(String newValue) {
	this.dataReference = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setDataReferenceType(String newValue) {
	this.dataReferenceType = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setDuration(int newValue) {
	this.duration = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setHeight(int newValue) {
	this.height = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setOffset(int newValue) {
	this.offset = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue SampleDescriptionEntry
 */
private void setSampleDescriptionEntry(SampleDescriptionEntry newValue) {
	this.sampleDescriptionEntry = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setSize(int newValue) {
	this.size = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setWidth(int newValue) {
	this.width = newValue;
}
}
