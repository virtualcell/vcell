package cbit.vcell.export.gloworm.atoms;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class BaseMediaInfo extends LeafAtom {

	public static final int size = 24;
	public static final String type = "gmin";
	protected short graphicsMode = defaultGraphicsMode;
	protected short[] opColor = defaultOpcolor;
	protected short balance = defaultBalance;
	protected short reserved = (short)0;

/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		out.writeByte(version);
		out.write(flags);
		out.writeShort(graphicsMode);
		for (int i=0;i<opColor.length;i++) out.writeShort(opColor[i]);
		out.writeShort(balance);
		out.writeShort(reserved);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}