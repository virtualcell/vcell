/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.quicktime.atoms;

import cbit.vcell.export.quicktime.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class MediaData extends Atoms {

	public static final String type = "mdat";
	protected MediaChunk[] mediaChunks;
	
/**
 * This method was created in VisualAge.
 * @param dReference cbit.vcell.export.quicktime.DataReference
 */
public MediaData(MediaChunk[] chunks) {
	mediaChunks = chunks;
	size = 8;
	for (int i=0;i<mediaChunks.length;i++) size += mediaChunks[i].getSize();
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<mediaChunks.length;i++) {
			mediaChunks[i].writeBytes(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
