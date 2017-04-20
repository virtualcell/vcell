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

import cbit.vcell.export.gloworm.quicktime.MediaMethods;
/**
 * This type was created in VisualAge.
 */
public class MediaHeader extends LeafAtom {

	public static final int size = 32;
	public static final String type = "mdhd";
	protected int creationTime;
	protected int modificationTime;
	protected int timeScale;
	protected int mediaDuration;
	protected short language = english;
	protected short quality = defaultPlayBackQuality;
/**
 * This method was created in VisualAge.
 * @param cTime int
 * @param mTime int
 * @param tScale int
 * @param duration int
 */
public MediaHeader(int tScale, int duration) {
	this(MediaMethods.getMacintoshTime(), MediaMethods.getMacintoshTime(), tScale, duration);
}
/**
 * This method was created in VisualAge.
 * @param cTime int
 * @param mTime int
 * @param tScale int
 * @param duration int
 */
public MediaHeader(int cTime, int mTime, int tScale, int duration) {
	creationTime = cTime;
	modificationTime = mTime;
	timeScale = tScale;
	mediaDuration = duration;
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
		out.writeInt(creationTime);
		out.writeInt(modificationTime);
		out.writeInt(timeScale);
		out.writeInt(mediaDuration);
		out.writeShort(language);
		out.writeShort(quality);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
