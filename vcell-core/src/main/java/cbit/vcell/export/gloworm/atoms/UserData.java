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
public class UserData extends Atoms {

	public static final String type = "udta";
	protected UserDataEntry[] entries;

/**
 * This method was created in VisualAge.
 * @param entries UserDataEntry[]
 */
public UserData(UserDataEntry[] entries) {
	this.entries = entries;
	if (entries == null) {
		size = 12;
	} else {
		size = 8;
		for (int i=0;i<entries.length;i++) size += entries[i].size;
	}
}
/**
 * This method was created in VisualAge.
 * @param entry UserDataEntry
 */
public UserData(UserDataEntry entry) {
	this(new UserDataEntry[] {entry});
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		if (entries == null)
			out.writeInt(0);
		else
			for (int i=0;i<entries.length;i++)
				entries[i].writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
