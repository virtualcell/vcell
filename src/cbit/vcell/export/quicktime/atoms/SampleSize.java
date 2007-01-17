package cbit.vcell.export.quicktime.atoms;

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
