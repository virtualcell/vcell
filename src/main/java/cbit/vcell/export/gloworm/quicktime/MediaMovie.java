/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;


import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.export.gloworm.atoms.Atoms;
import cbit.vcell.export.gloworm.atoms.UserDataEntry;
/**
 * This type was created in VisualAge.
 */
public class MediaMovie extends MediaMethods {
	private int numberOfTracks;
	private int timeScale;
	private int preferredMediaRate;
	private short preferredVolume;
	private MediaTrack[] tracks = null;
	private int duration;
	private Vector selfreferencedChunks = new Vector();
	private Vector userData = new Vector();
/**
 * This method was created in VisualAge.
 * @param tracks Track[]
 * @param duration int
 */
public MediaMovie(MediaTrack[] tracks, int duration) {
	this(tracks, duration, Atoms.defaultTimeScale);
}
/**
 * This method was created in VisualAge.
 * @param tracks Track[]
 * @param duration int
 */
public MediaMovie(MediaTrack[] tracks, int duration, int timeScale) {
	setTracks(tracks);
	setDuration(duration);
	setNumberOfTracks(tracks.length);
	setTimeScale(timeScale);
	setPreferredMediaRate(Atoms.defaultMediaRate);
	setPreferredVolume(Atoms.defaultVolume);
	for (int i=0;i<tracks.length;i++) {
		for (int j=0;j<tracks[i].getChunks().length;j++) {
			if (tracks[i].getChunks()[j].getDataReference().equals("self"))
				selfreferencedChunks.addElement(tracks[i].getChunks()[j]);
		}
	}
}
/**
 * This method was created in VisualAge.
 * @param tracks Track[]
 * @param duration int
 */
public MediaMovie(MediaTrack track, int duration) {
	this(new MediaTrack[] {track}, duration);
}
/**
 * This method was created in VisualAge.
 * @param tracks Track[]
 * @param duration int
 */
public MediaMovie(MediaTrack track, int duration, int timeScale) {
	this(new MediaTrack[] {track}, duration, timeScale);
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void addUserDataEntry(UserDataEntry entry) {
	userData.addElement(entry);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDuration() {
	return duration;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfTracks() {
	return numberOfTracks;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getPreferredMediaRate() {
	return preferredMediaRate;
}
/**
 * This method was created in VisualAge.
 * @return short
 */
public short getPreferredVolume() {
	return preferredVolume;
}
/**
 * This method was created in VisualAge.
 * @return MediaChunk
 */
public MediaChunk[] getSelfreferencedChunks() {
	return (MediaChunk[])BeanUtils.getArray(selfreferencedChunks, MediaChunk.class);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getTimeScale() {
	return timeScale;
}
/**
 * This method was created in VisualAge.
 * @return Track[]
 */
public MediaTrack[] getTracks() {
	return tracks;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public UserDataEntry[] getUserDataEntries() {
	if (userData.isEmpty()) {
		return null;
	} else {
		Object[] entries = new Object[userData.size()];
		userData.copyInto(entries);
		UserDataEntry[] userDataEntries = new UserDataEntry[entries.length];
		System.arraycopy(entries, 0, userDataEntries, 0, entries.length);
		return userDataEntries;
	}		
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setDuration(int newValue) {
	this.duration = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setNumberOfTracks(int newValue) {
	this.numberOfTracks = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setPreferredMediaRate(int newValue) {
	this.preferredMediaRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue short
 */
public void setPreferredVolume(short newValue) {
	this.preferredVolume = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setTimeScale(int newValue) {
	this.timeScale = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue Track[]
 */
private void setTracks(MediaTrack[] newValue) {
	this.tracks = newValue;
}
}
