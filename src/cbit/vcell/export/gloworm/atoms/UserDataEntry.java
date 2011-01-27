package cbit.vcell.export.gloworm.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
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
		this.type = "©" + buffer.toString();
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
