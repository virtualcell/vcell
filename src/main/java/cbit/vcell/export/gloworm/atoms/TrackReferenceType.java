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
public class TrackReferenceType extends LeafAtom {

	private String type;
	protected int numberOfEntries;
	protected int[] references;

/**
 * This method was created in VisualAge.
 * @param eList EditList
 */
public TrackReferenceType(String type, int[] refs) {
	this.type = type;
	this.references = refs;
	this.numberOfEntries = refs.length;
	size = 8 + 4 * numberOfEntries;
}


/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<numberOfEntries;i++) {
			out.writeInt(references[i]);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
