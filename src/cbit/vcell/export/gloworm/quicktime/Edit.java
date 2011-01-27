package cbit.vcell.export.gloworm.quicktime;

import cbit.vcell.export.gloworm.atoms.Atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

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
