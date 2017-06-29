/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;

import java.io.DataOutputStream;
import java.io.IOException;
/**
 * This type was created in VisualAge.
 */
public class SampleToChunkEntry extends Atoms {
	protected int firstChunk;
	protected int samplesPerChunk;
	protected int sampleDescriptionID;
/**
 * This method was created in VisualAge.
 * @param count int
 * @param duration int
 */
public SampleToChunkEntry(int first, int count, int ID) {
	firstChunk = first;
	samplesPerChunk = count;
	sampleDescriptionID = ID;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(firstChunk);
		out.writeInt(samplesPerChunk);
		out.writeInt(sampleDescriptionID);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
