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
public class SampleTableDescription extends LeafAtom {

	public static final String type = "stsd";
	protected int numberOfEntries;
	protected SampleDescriptionEntry[] descriptions;

/**
 * This method was created in VisualAge.
 */
public SampleTableDescription(SampleDescriptionEntry[] entries) {
	numberOfEntries = entries.length;
	descriptions = entries;
	size = 16;
	for (int i=0;i<entries.length;i++) size += entries[i].size;
}
/**
 * This method was created in VisualAge.
 */
public SampleTableDescription(SampleDescriptionEntry entry) {
	this(new SampleDescriptionEntry[] {entry});
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
		out.writeInt(numberOfEntries);
		for (int i=0;i<descriptions.length;i++)
			descriptions[i].writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
