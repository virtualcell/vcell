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
public class BaseMediaHeader extends Atoms {

	public static final String type = "gmhd";
	protected BaseMediaInfo baseMediaInfo;
	
/**
 * This method was created in VisualAge.
 * @param baseMediaInfo cbit.vcell.export.quicktime.BaseMediaInfo
 */
public BaseMediaHeader(BaseMediaInfo baseMediaInfo) {
	this.baseMediaInfo = baseMediaInfo;
	size = 8 + baseMediaInfo.size;
}


/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		baseMediaInfo.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}