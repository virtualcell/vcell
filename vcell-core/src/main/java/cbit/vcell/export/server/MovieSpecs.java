/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;

import java.io.Serializable;

import cbit.image.DisplayPreferences;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class MovieSpecs extends FormatSpecificSpecs implements Serializable {
	private double duration;
	private boolean overlayMode;
	private DisplayPreferences[] displayPreferences;
	private ExportFormat encodingFormat;
	private int mirroringType;
	private int volVarMembrOutlineThickness;
	private int imageScaling;
	private int membraneScaling;
	private int meshMode;
	private int compressionType;
	private float compressionQuality;
	private boolean bQTVR;
	private int particleMode;


/**
 * This method was created in VisualAge.
 * @param duration double
 * @param overlay boolean
 * @param displayPreferences cbit.vcell.simdata.gui.DisplayPreferences[]
 * @param encoding int
 * @param mirroring int
 */
public MovieSpecs(double duration, boolean overlayMode, DisplayPreferences[] displayPreferences, ExportFormat format,
		int mirroringType, int volVarMembrOutlineThickness,
		int imageScaling,int membraneScaling,int meshMode,int compressionType,float compressionQuality,boolean bQTVR,int particleMode) {
	this.duration = duration;
	this.overlayMode = overlayMode;
	this.displayPreferences = displayPreferences;
	this.encodingFormat = format;
	this.mirroringType = mirroringType;
	this.volVarMembrOutlineThickness = volVarMembrOutlineThickness;
	this.imageScaling = imageScaling;
	this.membraneScaling = membraneScaling;
	this.meshMode = meshMode;
	this.compressionType = compressionType;
	this.compressionQuality = compressionQuality;
	this.bQTVR = bQTVR;
	this.particleMode = particleMode;
}

public boolean isQTVR(){
	return bQTVR;
}
public float getcompressionQuality(){
	return compressionQuality;
}
public int getCompressionType(){
	return compressionType;
}
public int getParticleMode(){
	return particleMode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:20:28 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(java.lang.Object object) {
	if (object instanceof MovieSpecs) {
		MovieSpecs movieSpecs = (MovieSpecs)object;
		if (
			encodingFormat == movieSpecs.getEncodingFormat() &&
			overlayMode == movieSpecs.getOverlayMode() &&
			mirroringType == movieSpecs.getMirroringType() &&
			duration == movieSpecs.getDuration() &&
			volVarMembrOutlineThickness == movieSpecs.volVarMembrOutlineThickness &&
			displayPreferences.length == movieSpecs.getDisplayPreferences().length &&
			imageScaling == movieSpecs.imageScaling &&
			membraneScaling == movieSpecs.membraneScaling &&
			meshMode == movieSpecs.meshMode &&
			compressionType == movieSpecs.compressionType &&
			compressionQuality == movieSpecs.compressionQuality &&
			bQTVR == movieSpecs.bQTVR
		) {
			for (int i = 0; i < displayPreferences.length; i++){
				if (! displayPreferences[i].equals(movieSpecs.getDisplayPreferences()[i])) {
					return false;
				}
			}
			return true;
		}
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 12:09:34 PM)
 * @return cbit.vcell.simdata.gui.DisplayPreferences[]
 */
public DisplayPreferences[] getDisplayPreferences() {
	return displayPreferences;
}
/**
 * This method was created in VisualAge.
 * @return double
 */
public double getDuration() {
	return duration;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public ExportFormat getEncodingFormat() {
	return encodingFormat;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getMirroringType() {
	return mirroringType;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getOverlayMode() {
	return overlayMode;
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/01 9:57:49 AM)
 * @return boolean
 */
public int getVolVarMembrOutlineThickness() {
	return volVarMembrOutlineThickness;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 5:11:24 PM)
 * @return java.lang.String
 */
public java.lang.String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("MovieSpecs [");
	buf.append("quicktimeVR: " + bQTVR + ", ");
	buf.append("image scaling: " + imageScaling + ", ");
	buf.append("compressionType: " + compressionType + ", ");
	buf.append("compressionQual: " + compressionQuality + ", ");
	buf.append("overlayMode: " + overlayMode + ", ");
	buf.append("encodingFormat: " + encodingFormat + ", ");
	buf.append("mirroringType: " + mirroringType + ", ");
	buf.append("duration: " + duration + ", ");
	buf.append("volVarMembrOutlineThickness: " + volVarMembrOutlineThickness + ", ");
	buf.append("displayPreferences: ");
	if (displayPreferences != null) {
		buf.append("{");
		for (int i = 0; i < displayPreferences.length; i++){
			buf.append(displayPreferences[i]);
			if (i < displayPreferences.length - 1) buf.append(",");
		}
		buf.append("}");
	} else {
		buf.append("null");
	}
	buf.append("]");
	return buf.toString();
}
public int getImageScaling() {
	return imageScaling;
}
public int getMembraneScaling() {
	return membraneScaling;
}
public int getMeshMode(){
	return meshMode;
}
}
