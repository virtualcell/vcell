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
public class TrackReference extends Atoms {

	public static final String type = "tref";
	private TrackReferenceType[] refs;

/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 2:18:37 AM)
 * @param refs TrackReferenceType[]
 */
public TrackReference(TrackReferenceType[] refs) {
	this.refs = refs;
	size = 8;
	for (int i=0;i<refs.length;i++) size += refs[i].size;
}


/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<refs.length;i++) {
			refs[i].writeData(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
