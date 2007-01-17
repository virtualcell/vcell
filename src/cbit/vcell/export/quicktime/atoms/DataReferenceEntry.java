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
public class DataReferenceEntry extends LeafAtom {

	public static final String type = "alis";
	protected String dataReference;

/**
 * This method was created in VisualAge.
 */
public DataReferenceEntry() {
	dataReference = "self";
	flags[2] = selfReference;
	size = 12;
}
/**
 * This method was created in VisualAge.
 */
public DataReferenceEntry(String dReference) {
	this();
	if (! dReference.equals("self")) {
		flags[2] = flags[1];
		dataReference = dReference;
		size = 12 + dataReference.length();
	}
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeByte(version);
		out.write(flags);
		if (! dataReference.equals("self")) out.writeBytes(dataReference);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
