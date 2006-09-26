package cbit.vcell.export.quicktime;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ChunkID {
	private int numberOfSamples;
	private int sampleDescriptionID;
/**
 * This method was created in VisualAge.
 * @param count int
 * @param duration int
 */
public ChunkID(int count, int description) {
	setNumberOfSamples(count);
	setSampleDescriptionID(description);
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
public int getSampleDescriptionID() {
	return sampleDescriptionID;
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
private void setSampleDescriptionID(int newValue) {
	this.sampleDescriptionID = newValue;
}
}
