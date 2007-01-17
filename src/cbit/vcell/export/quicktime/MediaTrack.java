package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.atoms.*;
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
				dataFormats[index] = mediaChunks[j].getMediaSamples()[k].getDataFormat();
				keyFrames[index] = mediaChunks[j].getMediaSamples()[k].isKeyFrame();
				sampleDurations[index] = mediaChunks[j].getMediaSamples()[k].getDuration();
				sampleSizes[index] = mediaChunks[j].getMediaSamples()[k].getSize();
				index++;
			}
		}
		Vector references = new Vector();
		Vector IDs = new Vector();
		for (int j=0;j<mediaChunks.length;j++) {
			if (! references.contains(mediaChunks[j].getDataReference()))
				references.addElement(mediaChunks[j].getDataReference());
			if (! IDs.contains(mediaChunks[j].getDataReference()+mediaChunks[j].getDataFormat()))
				IDs.addElement(mediaChunks[j].getDataReference()+mediaChunks[j].getDataFormat());
		}
		String[] dataReferences = new String[references.size()];
		references.copyInto(dataReferences);
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
		setChunkIDs(chunkIDs);
		setSampleDescriptionEntries(sampleDescriptionEntries);
		setSampleSizes(sampleSizes);
	}
}
/**
 * This method was created in VisualAge.
 * @param mediaChunk cbit.vcell.export.quicktime.MediaChunk
 */
public MediaTrack(MediaChunk mediaChunk) throws DataFormatException {
	this(new MediaChunk[] {mediaChunk});
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.ChunkID[]
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
 * @return cbit.vcell.export.quicktime.MediaChunk[]
 */
public cbit.vcell.export.quicktime.MediaChunk[] getChunks() {
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
 * @return int
 */
public int getDuration() {
	return duration;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.quicktime.Edit[]
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
 * @return cbit.vcell.export.quicktime.atoms.SampleDescriptionEntry[]
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
 * @param newValue cbit.vcell.export.quicktime.ChunkID[]
 */
private void setChunkIDs(ChunkID[] newValue) {
	this.chunkIDs = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue cbit.vcell.export.quicktime.MediaChunk[]
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
 * @param newValue int
 */
private void setDuration(int newValue) {
	this.duration = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue cbit.vcell.export.quicktime.Edit[]
 */
public void setEdits(Edit[] newValue) {
	this.edits = newValue;
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
 * @param newValue cbit.vcell.export.quicktime.atoms.SampleDescriptionEntry[]
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
private void setWidth(int newValue) {
	this.width = newValue;
}
}
