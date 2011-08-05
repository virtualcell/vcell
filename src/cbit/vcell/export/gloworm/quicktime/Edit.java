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

import cbit.vcell.export.gloworm.atoms.Atoms;


/**
 * This type was created in VisualAge.
 */
public class Edit {
	private int trackDuration;
	private int mediaTime;
	private int mediaRate;
/**
 * Edit constructor comment.
 */
public Edit(int duration) {
	this(duration, 0, Atoms.defaultMediaRate);
}
/**
 * Edit constructor comment.
 */
public Edit(int duration, int time) {
	this(duration, time, Atoms.defaultMediaRate);
}
/**
 * Edit constructor comment.
 */
public Edit(int duration, int time, int rate) {
	setTrackDuration(duration);
	setMediaTime(time);
	setMediaRate(rate);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getMediaRate() {
	return mediaRate;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getMediaTime() {
	return mediaTime;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getTrackDuration() {
	return trackDuration;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setMediaRate(int newValue) {
	this.mediaRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setMediaTime(int newValue) {
	this.mediaTime = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setTrackDuration(int newValue) {
	this.trackDuration = newValue;
}
}
