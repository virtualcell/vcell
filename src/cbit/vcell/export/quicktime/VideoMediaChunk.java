package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;
import java.io.*;
import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class VideoMediaChunk implements MediaChunk {
	private final static String mediaType = "vide";
	private String dataFormat;
	private cbit.vcell.export.quicktime.MediaSample[] mediaSamples = null;
	private int numberOfSamples = 0;
	private int width;
	private int height;
	private int offset;
	private int size;
	private byte[] dataBytes = null;
	private int duration;
	private String dataReference = "self";
	private SampleDescriptionEntry sampleDescriptionEntry = null;
/**
 * VideoMediaChunk constructor comment.
 */
public VideoMediaChunk(VideoMediaSample[] samples) throws DataFormatException {
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
		setMediaSamples(samples);
		int size = 0;
		for (int j=0;j<samples.length;j++) size += samples[j].getSize();
		setSize(size);
		setDataBytes(new byte[size]);
		int counter = 0;
		int duration = 0;
		for (int j=0;j<samples.length;j++) {
			for (int k=0;k<samples[j].getDataBytes().length;k++) {
				getDataBytes()[counter] = samples[j].getDataBytes()[k];
				counter++;
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
	this(new VideoMediaSample[] {sample});
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
 * @return cbit.vcell.export.quicktime.MediaSample[]
 */
public cbit.vcell.export.quicktime.MediaSample[] getMediaSamples() {
	return mediaSamples;
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
 * @return cbit.vcell.export.quicktime.atoms.SampleDescriptionEntry
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
 * @param newValue cbit.vcell.export.quicktime.MediaSample[]
 */
private void setMediaSamples(cbit.vcell.export.quicktime.MediaSample[] newValue) {
	this.mediaSamples = newValue;
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
 * @param newValue cbit.vcell.export.quicktime.atoms.SampleDescriptionEntry
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
