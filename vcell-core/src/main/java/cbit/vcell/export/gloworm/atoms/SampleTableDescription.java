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
public class SampleTableDescription extends LeafAtom {

	public static final String type = "stsd";
	protected int numberOfEntries;
	protected SampleDescriptionEntry[] descriptions;

/**
 * This method was created in VisualAge.
 */
public SampleTableDescription(SampleDescriptionEntry[] entries) {
	numberOfEntries = entries.length;
	descriptions = entries;
	size = 16;
	for (int i=0;i<entries.length;i++) size += entries[i].size;
}
/**
 * This method was created in VisualAge.
 */
public SampleTableDescription(SampleDescriptionEntry entry) {
	this(new SampleDescriptionEntry[] {entry});
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
		for (int i=0;i<descriptions.length;i++)
			descriptions[i].writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
