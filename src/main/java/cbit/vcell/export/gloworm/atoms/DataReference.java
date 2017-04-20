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
public class DataReference extends LeafAtom {

	public static final String type = "dref";
	protected int numberOfEntries;
	protected DataReferenceEntry[] dataReferences;

/**
 * This method was created in VisualAge.
 */
public DataReference(DataReferenceEntry[] entries) {
	numberOfEntries = entries.length;
	dataReferences = entries;
	size = 16;
	for (int i=0;i<entries.length;i++) size += entries[i].size;
}
/**
 * This method was created in VisualAge.
 */
public DataReference(DataReferenceEntry entry) {
	this(new DataReferenceEntry[] {entry});
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
		for (int i=0;i<dataReferences.length;i++)
			dataReferences[i].writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
