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
public class DataReferenceEntry extends LeafAtom {

	public static String type = "alis";
	protected String dataReference;

/**
 * This method was created in VisualAge.
 */
public DataReferenceEntry() {
	dataReference = "self";
	flags[2] = selfReference;
	size = 12;
}


/**
 * This method was created in VisualAge.
 */
public DataReferenceEntry(String dReference, String type) {
	this();
	this.type = type;
	if (! dReference.equals("self")) {
		flags[2] = flags[1];
		dataReference = dReference;
		size = 12 + dataReference.length();
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
		if (! dataReference.equals("self")) out.writeBytes(dataReference);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
