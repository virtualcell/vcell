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
public class TimeToSample extends LeafAtom {

	public static final String type = "stts";
	protected int numberOfEntries;
	protected Vector entries;

/**
 * This method was created in VisualAge.
 * @param durations int[]
 */
public TimeToSample(int[] durations) {
	entries = new Vector();
	int d = -1;
	int c = 0;
	for (int i=0;i<durations.length;i++) {
		if (durations[i] != d) {
			if (d != -1) entries.addElement(new TimeToSampleEntry(c, d));
			d = durations[i];
			c = 1;
		} else {
			c++;
		}
	}
	entries.addElement(new TimeToSampleEntry(c, d));
	numberOfEntries = entries.size();
	size = 16 + 8 * numberOfEntries;
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
			TimeToSampleEntry ttse = (TimeToSampleEntry)en.nextElement();
			ttse.writeData(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
