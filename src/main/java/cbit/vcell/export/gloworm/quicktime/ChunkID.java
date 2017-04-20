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
