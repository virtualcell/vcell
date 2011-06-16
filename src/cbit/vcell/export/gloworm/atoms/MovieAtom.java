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
public class MovieAtom extends Atoms {

	public static final String type = "moov";
	protected MovieHeader movieHeader;
	protected ClippingAtom clippingAtom;
	protected TrackAtom[] trackAtoms;
	protected UserData userData;
	protected ColorTable colorTable;
	
/**
 * This method was created in VisualAge.
 * @param mvhd MovieHeader
 * @param clip ClippingAtom
 * @param traks TrackAtom[]
 * @param udta UserData
 * @param ctab ColorTable
 */
public MovieAtom(MovieHeader mvhd, TrackAtom[] traks) {
	this(mvhd, null, traks, null, null);
}
/**
 * This method was created in VisualAge.
 * @param mvhd MovieHeader
 * @param clip ClippingAtom
 * @param traks TrackAtom[]
 * @param udta UserData
 * @param ctab ColorTable
 */
public MovieAtom(MovieHeader mvhd, TrackAtom[] traks, UserData udta) {
	this(mvhd, null, traks, udta, null);
}
/**
 * This method was created in VisualAge.
 * @param mvhd MovieHeader
 * @param clip ClippingAtom
 * @param traks TrackAtom[]
 * @param udta UserData
 * @param ctab ColorTable
 */
public MovieAtom(MovieHeader mvhd, ClippingAtom clip, TrackAtom[] traks, UserData udta, ColorTable ctab) {
	movieHeader = mvhd;
	clippingAtom = clip;
	trackAtoms = traks;
	userData = udta;
	colorTable = ctab;
	size = 8 + mvhd.size;
	if (clippingAtom != null) size += clippingAtom.size;
	if (trackAtoms != null) for (int i=0;i<trackAtoms.length;i++) size += trackAtoms[i].size;
	if (userData != null) size += userData.size;
	if (colorTable != null) size += colorTable.size;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		movieHeader.writeData(out);
		if (clippingAtom != null) clippingAtom.writeData(out);
		if (trackAtoms != null) for (int i=0;i<trackAtoms.length;i++) trackAtoms[i].writeData(out);
		if (userData != null) userData.writeData(out);
		if (colorTable != null) colorTable.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
