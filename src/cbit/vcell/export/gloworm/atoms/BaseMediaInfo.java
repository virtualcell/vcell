/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.atoms;

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
