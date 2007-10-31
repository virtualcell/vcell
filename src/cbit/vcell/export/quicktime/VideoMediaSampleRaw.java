package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;

import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class VideoMediaSampleRaw extends VideoMediaSample implements MediaSample{
	private short colorDepth;
	private boolean isGrayscale;
	private int width;
	private int height;
	private int duration;
	private MediaSample.MediaSampleStream dataInputStream;
	private int dataBytesSize;
	private VideoSampleDescriptionEntryRaw sampleDescriptionEntry = null;
	public final static String dataFormat = "raw ";
/**
 * VideoMediaSampleRaw constructor comment.
 */
public VideoMediaSampleRaw(int width, int height, int duration, MediaSample.MediaSampleStream dataIS, int dataBytesSize,int bitsPerPixel, boolean grayscale) throws DataFormatException {
	if
	(
		((grayscale) && (bitsPerPixel != 8)) ||
		((bitsPerPixel != 8) && (bitsPerPixel != 16) && (bitsPerPixel != 24) && (bitsPerPixel != 32)) ||
		(dataBytesSize != width * height * bitsPerPixel / 8)
	)
	throw new DataFormatException("Raw video data invalid !");
	setWidth(width);
	setHeight(height);
	setDataInputStream(dataIS);
	setDataBytesSize(dataBytesSize);
	setDuration(duration);
	setIsGrayscale(grayscale);
	setColorDepth((short)bitsPerPixel);
	if (grayscale) setColorDepth((short)(getColorDepth() + 32));
	setSampleDescriptionEntry(new VideoSampleDescriptionEntryRaw(width, height, AtomConstants.defaultFrameResolution, bitsPerPixel, (short)0));
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
public MediaSample.MediaSampleStream getDataInputStream() {
	return dataInputStream;
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
	return dataBytesSize;
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
private void setDataInputStream(MediaSample.MediaSampleStream dataIS) {
	this.dataInputStream = dataIS;
}
private void setDataBytesSize(int dataSize) {
	this.dataBytesSize = dataSize;
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
private void setSampleDescriptionEntry(VideoSampleDescriptionEntryRaw newValue) {
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
