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
