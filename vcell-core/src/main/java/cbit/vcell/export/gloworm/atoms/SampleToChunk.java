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
import java.util.Enumeration;
import java.util.Vector;

import cbit.vcell.export.gloworm.quicktime.ChunkID;
/**
 * This type was created in VisualAge.
 */
public class SampleToChunk extends LeafAtom {

	public static final String type = "stsc";
	protected int numberOfEntries;
	protected Vector entries;

/**
 * This method was created in VisualAge.
 * @param durations int[]
 */
public SampleToChunk(ChunkID[] cIDs) {
	entries = new Vector();
	int n = -1;
	int d = -1;
	for (int i=0;i<cIDs.length;i++) {
		if ((cIDs[i].getNumberOfSamples() != n) || (cIDs[i].getSampleDescriptionID() != d)) {
			n = cIDs[i].getNumberOfSamples();
			d = cIDs[i].getSampleDescriptionID();
			entries.addElement(new SampleToChunkEntry(i + 1, n, d));
		}
	}
	numberOfEntries = entries.size();
	size = 16 + 12 * numberOfEntries;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeByte(version);
		out.write(flags);
		out.writeInt(numberOfEntries);
		Enumeration en = entries.elements();
		while (en.hasMoreElements()) {
			SampleToChunkEntry stce = (SampleToChunkEntry)en.nextElement();
			stce.writeData(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
