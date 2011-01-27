package cbit.vcell.export.gloworm.quicktime;


import java.util.zip.DataFormatException;

import cbit.vcell.export.gloworm.atoms.AtomConstants;
import cbit.vcell.export.gloworm.atoms.SampleDescriptionEntry;
import cbit.vcell.export.gloworm.atoms.VideoSampleDescriptionEntryJPEG;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

/**
 * This type was created in VisualAge.
 */
public class VideoMediaSampleJPEG extends VideoMediaSample {
	private short colorDepth;
	private boolean isGrayscale;
	private int width;
	private int height;
	private byte[] dataBytes = null;
	private int duration;
	private VideoSampleDescriptionEntryJPEG sampleDescriptionEntry = null;
	public final static String dataFormat = "jpeg";

/**
 * VideoMediaSampleRaw constructor comment.
 */
public VideoMediaSampleJPEG(int width, int height, int duration, byte[] data, int bitsPerPixel, boolean grayscale) throws DataFormatException {
	if
	(
		((grayscale) && (bitsPerPixel != 8)) ||
		((bitsPerPixel != 8) && (bitsPerPixel != 16) && (bitsPerPixel != 24) && (bitsPerPixel != 32))
	)
	throw new DataFormatException("Raw video data invalid !");
	setWidth(width);
	setHeight(height);
	setDataBytes(data);
	setDuration(duration);
	setIsGrayscale(grayscale);
	setColorDepth((short)bitsPerPixel);
	if (grayscale) setColorDepth((short)(getColorDepth() + 32));
	setSampleDescriptionEntry(new VideoSampleDescriptionEntryJPEG(width, height, AtomConstants.defaultFrameResolution, getColorDepth(), (short)0));
}


/**
 * This method was created in VisualAge.
 * @return short
 */
public short getColorDepth() {
	return colorDepth;
}


/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getDataBytes() {
	return dataBytes;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public final String getDataFormat() {
	return dataFormat;
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
 * @return short
 */
public int getHeight() {
	return height;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getIsGrayscale() {
	return isGrayscale;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public final String getMediaType() {
	return mediaType;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.atoms.VideoSampleDescriptionEntry
 */
public SampleDescriptionEntry getSampleDescriptionEntry() {
	return sampleDescriptionEntry;
}


/**
 * getSize method comment.
 */
public int getSize() {
	return dataBytes.length;
}


/**
 * This method was created in VisualAge.
 * @return short
 */
public int getWidth() {
	return width;
}


/**
 * isKeyFrame method comment.
 */
public boolean isKeyFrame() {
	return true;
}


/**
 * This method was created in VisualAge.
 * @param newValue short
 */
private void setColorDepth(short newValue) {
	this.colorDepth = newValue;
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
 * @param newValue int
 */
private void setDuration(int newValue) {
	this.duration = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue short
 */
private void setHeight(int newValue) {
	this.height = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue boolean
 */
private void setIsGrayscale(boolean newValue) {
	this.isGrayscale = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue cbit.vcell.export.quicktime.atoms.VideoSampleDescriptionEntry
 */
private void setSampleDescriptionEntry(VideoSampleDescriptionEntryJPEG newValue) {
	this.sampleDescriptionEntry = newValue;
}


/**
 * This method was created in VisualAge.
 * @param newValue short
 */
private void setWidth(int newValue) {
	this.width = newValue;
}
}