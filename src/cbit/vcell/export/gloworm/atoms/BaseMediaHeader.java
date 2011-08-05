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
public class BaseMediaHeader extends Atoms {

	public static final String type = "gmhd";
	protected BaseMediaInfo baseMediaInfo;
	
/**
 * This method was created in VisualAge.
 * @param baseMediaInfo BaseMediaInfo
 */
public BaseMediaHeader(BaseMediaInfo baseMediaInfo) {
	this.baseMediaInfo = baseMediaInfo;
	size = 8 + baseMediaInfo.size;
}


/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		baseMediaInfo.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
