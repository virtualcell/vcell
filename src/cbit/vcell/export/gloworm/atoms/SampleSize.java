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


import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class SampleSize extends LeafAtom {

	public static final String type = "stsz";
	protected int sampleSize = 0;
	protected int numberOfEntries;
	protected boolean allSameSize;
	protected int[] sampleSizes;

/**
 * This method was created in VisualAge.
 * @param durations int[]
 */
public SampleSize(int[] sizes) {
	sampleSizes = sizes;
	boolean same = true;
	int i = 0;
	while (same && (i < sizes.length)) {
		if (sizes[i] != sizes[0]) same = false;
		i++;
	}
	allSameSize = same;
	numberOfEntries = sampleSizes.length;
	size = 20 + 4 * numberOfEntries;
	if (allSameSize) {
		sampleSize = sizes[0];
		size = 20;
	}
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
		out.writeInt(sampleSize);
		out.writeInt(numberOfEntries);
		if (allSameSize == false)
			for (int i=0;i<sampleSizes.length;i++) out.writeInt(sampleSizes[i]);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
