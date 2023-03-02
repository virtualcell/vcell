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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
/**
 * This type was created in VisualAge.
 */
public class BaseMediaHeader extends Atoms {
	private final static Logger lg = LogManager.getLogger(BaseMediaHeader.class);

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
		lg.error("Unable to write: " + e.getMessage(), e);
		return false;
	}
}
}
