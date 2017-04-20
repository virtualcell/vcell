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
/**
 * Insert the type's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @author: Ion Moraru
 */
public class VRObjectSampleAtom extends VRAtom {
	// required by constructor; private setters
	private int viewDuration;
	private int columns;
	private int rows;
	private float defaultViewCenterH;
	private float defaultViewCenterV;

	// default values; public setters
	private short movieType = 1;
	private short viewStateCount = 1;
	private short defaultViewState = 1;
	private short mouseDownViewState = 1;
	private float mouseMotionScale = 180f;
	private float minPan = 0f;
	private float maxPan = 360f;
	private float defaultPan = 0f;
	private float minTilt = 90f;
	private float maxTilt = -90f;
	private float defaultTilt = 0f;
	private float minFieldOfView = 8f;
	private float fieldOfView = 64f;
	private float defaultFieldOfView = 64f;
	private float viewRate = 1f;
	private float frameRate = 1f;
	private int animationSettings = (1<<5);
	private int controlSettings = (1<<0) + (1<<1) + (1<<2) + (1<<6);

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:03:34 PM)
 * @param columns int
 * @param rows int
 * @param viewDuration int
 * @param defaultViewCenterH float
 * @param defaultViewCenterV float
 */
public VRObjectSampleAtom(int columns, int rows, int viewDuration, float defaultViewCenterH, float defaultViewCenterV) {
	setColumns(columns);
	setRows(rows);
	setViewDuration(viewDuration);
	setDefaultViewCenterH(defaultViewCenterH);
	setDefaultViewCenterV(defaultViewCenterV);
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return int
 */
public int getAnimationSettings() {
	return animationSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @return int
 */
public int getColumns() {
	return columns;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:10:55 PM)
 * @return int
 */
public int getControlSettings() {
	return controlSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getDefaultFieldOfView() {
	return defaultFieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getDefaultPan() {
	return defaultPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getDefaultTilt() {
	return defaultTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @return float
 */
public float getDefaultViewCenterH() {
	return defaultViewCenterH;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @return float
 */
public float getDefaultViewCenterV() {
	return defaultViewCenterV;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return short
 */
public short getDefaultViewState() {
	return defaultViewState;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getFieldOfView() {
	return fieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getFrameRate() {
	return frameRate;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMaxPan() {
	return maxPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMaxTilt() {
	return maxTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMinFieldOfView() {
	return minFieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMinPan() {
	return minPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMinTilt() {
	return minTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return short
 */
public short getMouseDownViewState() {
	return mouseDownViewState;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getMouseMotionScale() {
	return mouseMotionScale;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return short
 */
public short getMovieType() {
	return movieType;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @return int
 */
public int getRows() {
	return rows;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 6:26:07 PM)
 * @return int
 */
public int getSize() {
	return 108;
}


/**
 * Insert the method's description here.
 * Creation date: (11/7/2005 10:18:36 PM)
 * @return java.lang.String
 */
public String getType() {
	return VR_OBJECT_SAMPLE_ATOM_TYPE;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @return int
 */
public int getViewDuration() {
	return viewDuration;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return float
 */
public float getViewRate() {
	return viewRate;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @return short
 */
public short getViewStateCount() {
	return viewStateCount;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newAnimationSettings int
 */
public void setAnimationSettings(int newAnimationSettings) {
	animationSettings = newAnimationSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @param newColumns int
 */
private void setColumns(int newColumns) {
	columns = newColumns;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:10:55 PM)
 * @param newControlSettings int
 */
public void setControlSettings(int newControlSettings) {
	controlSettings = newControlSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newDefaultFieldOfView float
 */
public void setDefaultFieldOfView(float newDefaultFieldOfView) {
	defaultFieldOfView = newDefaultFieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newDefaultPan float
 */
public void setDefaultPan(float newDefaultPan) {
	defaultPan = newDefaultPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newDefaultTilt float
 */
public void setDefaultTilt(float newDefaultTilt) {
	defaultTilt = newDefaultTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @param newDefaultViewCenterH float
 */
private void setDefaultViewCenterH(float newDefaultViewCenterH) {
	defaultViewCenterH = newDefaultViewCenterH;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @param newDefaultViewCenterV float
 */
private void setDefaultViewCenterV(float newDefaultViewCenterV) {
	defaultViewCenterV = newDefaultViewCenterV;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newDefaultViewState short
 */
public void setDefaultViewState(short newDefaultViewState) {
	defaultViewState = newDefaultViewState;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newFieldOfView float
 */
public void setFieldOfView(float newFieldOfView) {
	fieldOfView = newFieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newFrameRate float
 */
public void setFrameRate(float newFrameRate) {
	frameRate = newFrameRate;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMaxPan float
 */
public void setMaxPan(float newMaxPan) {
	maxPan = newMaxPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMaxTilt float
 */
public void setMaxTilt(float newMaxTilt) {
	maxTilt = newMaxTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMinFieldOfView float
 */
public void setMinFieldOfView(float newMinFieldOfView) {
	minFieldOfView = newMinFieldOfView;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMinPan float
 */
public void setMinPan(float newMinPan) {
	minPan = newMinPan;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMinTilt float
 */
public void setMinTilt(float newMinTilt) {
	minTilt = newMinTilt;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMouseDownViewState short
 */
public void setMouseDownViewState(short newMouseDownViewState) {
	mouseDownViewState = newMouseDownViewState;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMouseMotionScale float
 */
public void setMouseMotionScale(float newMouseMotionScale) {
	mouseMotionScale = newMouseMotionScale;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newMovieType short
 */
public void setMovieType(short newMovieType) {
	movieType = newMovieType;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @param newRows int
 */
private void setRows(int newRows) {
	rows = newRows;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:01:46 PM)
 * @param newViewDuration int
 */
private void setViewDuration(int newViewDuration) {
	viewDuration = newViewDuration;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newViewRate float
 */
public void setViewRate(float newViewRate) {
	viewRate = newViewRate;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 7:02:26 PM)
 * @param newViewStateCount short
 */
public void setViewStateCount(short newViewStateCount) {
	viewStateCount = newViewStateCount;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public void writeData(java.io.DataOutputStream out) throws java.io.IOException {
	out.writeInt(getSize());
	out.writeBytes(getType());
	out.writeInt(getAtomID());
	out.writeInt(getChildCount());
	out.writeInt(getIndex());
	out.writeShort(VR_MAJOR_VERSION);
	out.writeShort(VR_MINOR_VERSION);
	out.writeShort(getMovieType());
	out.writeShort(getViewStateCount());
	out.writeShort(getDefaultViewState());
	out.writeShort(getMouseDownViewState());
	out.writeInt(getViewDuration());
	out.writeInt(getColumns());
	out.writeInt(getRows());
	out.writeFloat(getMouseMotionScale());
	out.writeFloat(getMinPan());
	out.writeFloat(getMaxPan());
	out.writeFloat(getDefaultPan());
	out.writeFloat(getMinTilt());
	out.writeFloat(getMaxTilt());
	out.writeFloat(getDefaultTilt());
	out.writeFloat(getMinFieldOfView());
	out.writeFloat(getFieldOfView());
	out.writeFloat(getDefaultFieldOfView());
	out.writeFloat(getDefaultViewCenterH());
	out.writeFloat(getDefaultViewCenterV());
	out.writeFloat(getViewRate());
	out.writeFloat(getFrameRate());
	out.writeInt(getAnimationSettings());
	out.writeInt(getControlSettings());
}
}
