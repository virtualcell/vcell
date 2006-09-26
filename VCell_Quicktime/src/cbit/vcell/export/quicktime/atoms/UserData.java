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
