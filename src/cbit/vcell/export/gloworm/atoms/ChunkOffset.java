package cbit.vcell.export.gloworm.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
import java.util.*;
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
		e.printStackTrace();
		return false;
	}
}
}
