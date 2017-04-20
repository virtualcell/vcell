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
public class TrackAtom extends Atoms {

	public static final String type = "trak";
	protected TrackHeader trackHeader;
	protected ClippingAtom clippingAtom;
	protected TrackMatte trackMatte;
	protected EditAtom editAtom;
	protected TrackReference trackReference;
	protected TrackLoadSettings trackLoadSettings;
	protected TrackInputMap trackInputMap;
	protected MediaAtom mediaAtom;
	protected UserData userData;
	
/**
 * This method was created in VisualAge.
 * @param dReference DataReference
 */
public TrackAtom(TrackHeader tkhd, ClippingAtom clip, TrackMatte matt, EditAtom edts, TrackReference tref, TrackLoadSettings load, TrackInputMap imap, MediaAtom mdia, UserData udta) {
	trackHeader = tkhd;
	clippingAtom = clip;
	trackMatte = matt;
	editAtom = edts;
	trackReference = tref;
	trackLoadSettings = load;
	trackInputMap = imap;
	mediaAtom = mdia;
	userData = udta;
	size = 8 + tkhd.size + mdia.size;
	if (clippingAtom != null) size += clippingAtom.size;
	if (trackMatte != null) size += trackMatte.size;
	if (editAtom != null) size += editAtom.size;
	if (trackReference != null) size += trackReference.size;
	if (trackLoadSettings != null) size += trackLoadSettings.size;
	if (trackInputMap != null) size += trackInputMap.size;
	if (userData != null) size += userData.size;
}
/**
 * This method was created in VisualAge.
 * @param dReference DataReference
 */
public TrackAtom(TrackHeader tkhd, EditAtom edts, MediaAtom mdia) {
	this(tkhd, null, null, edts, null, null, null, mdia, null);
}
/**
 * This method was created in VisualAge.
 * @param dReference DataReference
 */
public TrackAtom(TrackHeader tkhd, MediaAtom mdia) {
	this(tkhd, null, null, null, null, null, null, mdia, null);
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		trackHeader.writeData(out);
		if (clippingAtom != null) clippingAtom.writeData(out);
		if (trackMatte != null) trackMatte.writeData(out);
		if (editAtom != null) editAtom.writeData(out);
		if (trackReference != null) trackReference.writeData(out);
		if (trackLoadSettings != null) trackLoadSettings.writeData(out);
		if (trackInputMap != null) trackInputMap.writeData(out);
		mediaAtom.writeData(out);
		if (userData != null) userData.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
