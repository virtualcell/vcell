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
