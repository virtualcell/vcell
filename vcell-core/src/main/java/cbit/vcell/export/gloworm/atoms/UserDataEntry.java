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
public class UserDataEntry extends Atoms {

	protected String type;
	protected String userData;
	protected boolean international;

/**
 * This method was created in VisualAge.
 */
public UserDataEntry(String type, String userData) {
	this(type, userData, true);
}
/**
 * This method was created in VisualAge.
 */
public UserDataEntry(String type, String userData, boolean usePrefix) {
	StringBuffer buffer = new StringBuffer(type);
	if (usePrefix) {
		buffer.setLength(3);
		this.type = "�" + buffer.toString();
		this.international = true;
	} else {
		buffer.setLength(4);
		this.type = buffer.toString();
	}
	this.userData = userData;
	this.size = 12 + userData.toCharArray().length;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size - 4);
		out.writeBytes(type);
		if (international) {
			out.writeShort(userData.length());
			out.writeShort(english);
		}
		out.writeBytes(userData);
		out.writeInt(0);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
