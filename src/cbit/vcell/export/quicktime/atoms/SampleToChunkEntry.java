package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
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
