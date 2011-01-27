package cbit.vcell.export.gloworm.quicktime;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/


import java.io.*;
import java.util.zip.*;

import cbit.vcell.export.gloworm.atoms.AtomConstants;
import cbit.vcell.export.gloworm.atoms.MediaData;
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;
/**
 * This type was created in VisualAge.
 */
public class VideoMediaChunk implements MediaChunk {
	private File dataFile;
	private String dataFormat;
	private int numberOfSamples = 0;
	private int width;
	private int height;
	private int offset;
	private int size;
	private byte[] dataBytes = null;
	private int duration;
	private String dataReference = "self";
	private String dataReferenceType = "alis";
	private SampleDescriptionEntry sampleDescriptionEntry = null;
	private VideoMediaSampleInfo[] sampleInfos = null;

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

/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample[] samples, File dataFile) throws DataFormatException, IOException {
	/* creates a chunk that puts data bytes into a file instead of holding them in memory */
	// if the file does not exist, we create it and initialize the mdat atom header
	// it the file exists it is expected that it already has a valid mdat atom header
	this(samples, true);
	this.dataFile = dataFile;
	boolean mustInit = !dataFile.exists();
	// append data bytes to file and clear memory copy
	RandomAccessFile fw = new RandomAccessFile(dataFile, "rw");
	fw.seek(dataFile.length());
	if (mustInit) {
		fw.writeInt(0);
		fw.writeBytes(MediaData.type);
		setOffset(8);
	} else {
		setOffset((int)dataFile.length());
	}
	fw.write(dataBytes);
	setDataBytes(null);
	// update the media data atom header
	fw.seek(0);
	fw.writeInt((int)dataFile.length());
	fw.close();
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample[] samples, String dataReference, String dataReferenceType) throws DataFormatException {
	/* creates a chunk that holds only a reference */
	this(samples, false);
	setDataReference(dataReference);
	setDataReferenceType(dataReferenceType);
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample[] samples, boolean selfReferenced) throws DataFormatException {
	boolean goodSamples = true;
	int i = 0;
	while ((goodSamples) && (i < samples.length)) {
		if (
			(! samples[i].getDataFormat().equals(samples[0].getDataFormat())) ||
			(samples[i].getWidth() != samples[0].getWidth()) ||
			(samples[i].getHeight() != samples[0].getHeight())
		   ) goodSamples = false;
		i++;
	}
	if (! goodSamples) throw new DataFormatException("Bad Media Sample Array !");
	else {
		setDataFormat(samples[0].getDataFormat());
		setWidth(samples[0].getWidth());
		setHeight(samples[0].getHeight());
		setSampleDescriptionEntry(samples[0].getSampleDescriptionEntry());
		setNumberOfSamples(samples.length);
		int size = 0;
		for (int j=0;j<samples.length;j++) size += samples[j].getSize();
		setSize(size);
		if (selfReferenced) {
			setDataBytes(new byte[size]);
		}
		int counter = 0;
		int duration = 0;
		sampleInfos = new VideoMediaSampleInfo[samples.length]; 
		for (int j=0;j<samples.length;j++) {
			sampleInfos[j] = new VideoMediaSampleInfo(samples[j].getDataFormat(), samples[j].getDuration(), samples[j].getMediaType(), samples[j].getSampleDescriptionEntry(), samples[j].getSize(), samples[j].isKeyFrame());
			if (selfReferenced) {
				for (int k=0;k<samples[j].getDataBytes().length;k++) {
					getDataBytes()[counter] = samples[j].getDataBytes()[k];
					counter++;
				}
			}
			duration += samples[j].getDuration();
		}
		setDuration(duration);
	}
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample sample) throws DataFormatException {
	this(sample, true);
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample sample, File dataFile) throws DataFormatException, IOException {
	this(new VideoMediaSample[] {sample}, dataFile);
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample sample, String dataReference, String dataReferenceType) throws DataFormatException {
	this(new VideoMediaSample[] {sample}, dataReference, dataReferenceType);
}


/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample sample, boolean selfReferenced) throws DataFormatException {
	this(new VideoMediaSample[] {sample}, selfReferenced);
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getDataBytes() {
	if (dataFile == null) {
		// we hold them
		return dataBytes;
	} else {
		// they have been written to a file
		try {
			byte[] bytes = new byte[getSize()];
			RandomAccessFile fw = new RandomAccessFile(dataFile, "rw");
			fw.seek(getOffset());
			fw.read(bytes);
			fw.close();
			return bytes;
		} catch (IOException exc) {
			exc.printStackTrace();
			return null;
		}
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
	return sampleInfos;
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
	return numberOfSamples;
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
 * @param newValue byte[]
 */
private void setDataBytes(byte[] newValue) {
	this.dataBytes = newValue;
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
private void setNumberOfSamples(int newValue) {
	this.numberOfSamples = newValue;
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