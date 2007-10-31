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
public class MediaData extends Atoms {

	public static final String type = "mdat";
	protected MediaChunk[] mediaChunks;
	
/**
 * This method was created in VisualAge.
 * @param dReference cbit.vcell.export.quicktime.DataReference
 */
public MediaData(MediaChunk[] chunks) {
	mediaChunks = chunks;
	size = 8;
	for (int i=0;i<mediaChunks.length;i++) size += mediaChunks[i].getSize();
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		for (int i=0;i<mediaChunks.length;i++) {
			mediaChunks[i].writeBytes(out);
		}
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
