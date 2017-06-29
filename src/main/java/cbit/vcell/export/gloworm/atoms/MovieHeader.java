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


import java.io.DataOutputStream;
import java.io.IOException;

import cbit.vcell.export.gloworm.quicktime.MediaMethods;
/**
 * This type was created in VisualAge.
 */
public class MovieHeader extends LeafAtom {

	public static final int size = 108;
	public static final String type = "mvhd";
	protected int creationTime;
	protected int modificationTime;
	protected int timeScale;
	protected int movieDuration;
	protected int preferredRate;
	protected short preferredVolume;
	protected byte[] reserved = new byte[10];
	protected int[] matrix;
	protected int previewTime;
	protected int previewDuration;
	protected int posterTime;
	protected int selectionTime;
	protected int selectionDuration;
	protected int currentTime;
	protected int nextTrackID;
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public MovieHeader(int tScale, int duration, int numberOfTracks) {
	this(MediaMethods.getMacintoshTime(), MediaMethods.getMacintoshTime(), tScale, duration, Atoms.defaultMediaRate, Atoms.defaultVolume, 0, 0, 0, 0, 0, 0, numberOfTracks);
}
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public MovieHeader(int creationT, int modificationT, int tScale, int duration, int numberOfTracks) {
	this(creationT, modificationT, tScale, duration, Atoms.defaultMediaRate, Atoms.defaultVolume, 0, 0, 0, 0, 0, 0, numberOfTracks);
}
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public MovieHeader(int creationT, int modificationT, int tScale, int duration, int rate, int volume, int previewT, int previewD, int posterT, int selectionT, int selectionD, int currentT, int numberOfTracks) {
	creationTime = creationT;
	modificationTime = modificationT;
	timeScale = tScale;
	movieDuration = duration;
	preferredRate = rate;
	preferredVolume = (short)volume;
	matrix = defaultMatrix;
	previewTime = previewT;
	previewDuration = previewD;
	posterTime = posterT;
	selectionTime = selectionT;
	selectionDuration = selectionD;
	currentTime = currentT;
	nextTrackID = numberOfTracks;
}
/**
 * This method was created in VisualAge.
 * @param ID int
 * @param duration int
 * @param width int
 * @param height int
 */
public MovieHeader(int creationT, int modificationT, int tScale, int duration, int rate, short volume, int previewT, int previewD, int posterT, int selectionT, int selectionD, int currentT, int numberOfTracks) {
	creationTime = creationT;
	modificationTime = modificationT;
	timeScale = tScale;
	movieDuration = duration;
	preferredRate = rate;
	preferredVolume = volume;
	matrix = defaultMatrix;
	previewTime = previewT;
	previewDuration = previewD;
	posterTime = posterT;
	selectionTime = selectionT;
	selectionDuration = selectionD;
	currentTime = currentT;
	nextTrackID = numberOfTracks + 1;
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
		out.writeInt(movieDuration);
		out.writeInt(preferredRate);
		out.writeShort(preferredVolume);
		out.write(reserved);
		for (int i=0;i<9;i++) out.writeInt(matrix[i]);
		out.writeInt(previewTime);
		out.writeInt(previewDuration);
		out.writeInt(posterTime);
		out.writeInt(selectionTime);
		out.writeInt(selectionDuration);
		out.writeInt(currentTime);
		out.writeInt(nextTrackID);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
