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
public class EditList extends LeafAtom {

	public static final String type = "elst";
	protected int numberOfEntries;
	protected Edit[] edits;

/**
 * This method was created in VisualAge.
 * @param eList EditList
 */
public EditList(Edit[] e) {
	edits = e;
	numberOfEntries = edits.length;
	size = 16 + 12 * numberOfEntries;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.write(version);
		out.write(flags);
		out.writeInt(numberOfEntries);
		for (int i=0;i<numberOfEntries;i++) {
			out.writeInt(edits[i].getTrackDuration());
			out.writeInt(edits[i].getMediaTime());
			out.writeInt(edits[i].getMediaRate());
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
