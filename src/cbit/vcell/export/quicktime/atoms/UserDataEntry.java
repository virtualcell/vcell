package cbit.vcell.export.quicktime.atoms;

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
	StringBuffer buffer = new StringBuffer(type);
	buffer.setLength(3);
	this.type = "©" + buffer.toString();
	this.userData = userData;
	this.size = 12 + userData.length();
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeShort(userData.length());
		out.writeShort(english);
		out.writeBytes(userData);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
