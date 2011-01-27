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
public class TrackReference extends Atoms {

	public static final String type = "tref";
	private TrackReferenceType[] refs;

/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 2:18:37 AM)
 * @param refs cbit.vcell.export.quicktime.atoms.TrackReferenceType[]
 */
public TrackReference(TrackReferenceType[] refs) {
	this.refs = refs;
	size = 8;
	for (int i=0;i<refs.length;i++) size += refs[i].size;
}


/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<refs.length;i++) {
			refs[i].writeData(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}