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
public class VideoMediaInformationHeader extends LeafAtom {

	public static final String type = "vmhd";
	protected short graphicsMode;
	protected short[] opcolor = new short[3];

/**
 * This method was created in VisualAge.
 */
public VideoMediaInformationHeader() {
	for (int i=0;i<3;i++) opcolor[i] = defaultColorValue;
	graphicsMode = ditherCopy;
	flags[2] = noLeanAhead;
	size = 20;
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
		out.writeShort(graphicsMode);
		for (int i=0;i<opcolor.length;i++) out.writeShort(opcolor[i]);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
