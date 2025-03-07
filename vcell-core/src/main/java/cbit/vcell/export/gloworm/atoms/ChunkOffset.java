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
public class ChunkOffset extends LeafAtom {

	public static final String type = "stco";
	protected int numberOfEntries;
	protected int[] chunkOffsets;

/**
 * This method was created in VisualAge.
 * @param durations int[]
 */
public ChunkOffset(int[] offsets) {
	chunkOffsets = offsets;
	numberOfEntries = chunkOffsets.length;
	size = 16 + 4 * numberOfEntries;
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
		for (int i=0;i<chunkOffsets.length;i++) out.writeInt(chunkOffsets[i]);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		lg.error(e);
		return false;
	}
}
}
