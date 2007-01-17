package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
import java.util.*;
/**
 * This type was created in VisualAge.
 */
public class SyncSample extends LeafAtom {

	public static final String type = "stss";
	protected int numberOfEntries;
	protected boolean[] keyIDs;
	protected boolean allKey;

/**
 * This method was created in VisualAge.
 * @param durations int[]
 */
public SyncSample(boolean[] keys) {
	keyIDs = keys;
	int count = 0;
	for (int i=0;i<keys.length;i++)
		if (keys[i]) count++;
	numberOfEntries = count;
	size = 16 + 4 * numberOfEntries;
	if (count == keys.length) allKey = true;
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
		for (int i=0;i<keyIDs.length;i++)
			if (keyIDs[i]) out.writeInt(i + 1);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
