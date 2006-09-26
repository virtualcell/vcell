package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class TimeToSampleEntry extends Atoms {
	protected int sampleCount;
	protected int sampleDuration;
/**
 * This method was created in VisualAge.
 * @param count int
 * @param duration int
 */
public TimeToSampleEntry(int count, int duration) {
	sampleCount = count;
	sampleDuration = duration;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(sampleCount);
		out.writeInt(sampleDuration);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
