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
public class ImageSpecs extends FormatSpecificSpecs implements Serializable {
	private DisplayPreferences[] displayPreferences;
	private ExportFormat format;
	private int compression;
	private int mirroringType;
	private double duration;
	private int loopingMode;
	private int volVarMembrOutlineThickness;
	private int imageScaling;
	private int membraneScaling;
	private int meshMode;
	private float compressionQuality;
	private boolean bOverlay;
	private int particleMode;
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 12:13:46 PM)
 * @param displayPreferences cbit.vcell.simdata.gui.DisplayPreferences[]
 * @param mediaType int
 * @param compression int
 * @param mirroringType int
 * @param duration double
 * @param loopingMode int
 */
public ImageSpecs(DisplayPreferences[] displayPreferences, ExportFormat mediaType,
		int compression, int mirroringType,
		double duration, int loopingMode, int volVarMembrOutlineThickness,
		int imageScaling,int membraneScaling,int meshMode,float compressionQuality,boolean bOverlay,int particleMode) {
	this.displayPreferences = displayPreferences;
	this.format = mediaType;
	this.compression = compression;
	this.mirroringType = mirroringType;
	this.duration = duration;
	this.loopingMode = loopingMode;
	this.volVarMembrOutlineThickness = volVarMembrOutlineThickness;
	this.imageScaling = imageScaling;
	this.membraneScaling = membraneScaling;
	this.meshMode = meshMode;
	this.compressionQuality = compressionQuality;
	this.bOverlay = bOverlay;
	this.particleMode = particleMode;
}
public boolean getOverlayMode(){
	return bOverlay;
}
public float getcompressionQuality(){
	return compressionQuality;
}
public int getParticleMode(){
	return particleMode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:11:26 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(java.lang.Object object) {
	if (object instanceof ImageSpecs) {
		ImageSpecs imageSpecs = (ImageSpecs)object;
		if (
			format == imageSpecs.getFormat() &&
			compression == imageSpecs.getCompression() &&
			mirroringType == imageSpecs.getMirroringType() &&
			duration == imageSpecs.getDuration() &&
			loopingMode == imageSpecs.getLoopingMode() &&
			volVarMembrOutlineThickness == imageSpecs.volVarMembrOutlineThickness &&
			displayPreferences.length == imageSpecs.getDisplayPreferences().length &&
			imageScaling == imageSpecs.imageScaling &&
			membraneScaling == imageSpecs.membraneScaling &&
			meshMode == imageSpecs.meshMode &&
			compressionQuality == imageSpecs.compressionQuality
		) {
			for (int i = 0; i < displayPreferences.length; i++){
				if (! displayPreferences[i].equals(imageSpecs.getDisplayPreferences()[i])) {
					return false;
				}
			}
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getCompression() {
	return compression;
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
 * @return int
 */
public double getDuration() {
	return duration;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public ExportFormat getFormat() {
	return format;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getLoopingMode() {
	return loopingMode;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getMirroringType() {
	return mirroringType;
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
 * Creation date: (4/2/2001 5:33:23 PM)
 * @return java.lang.String
 */
public java.lang.String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("ImageSpecs [");
	buf.append("format: " + format + ", ");
	buf.append("imgScaling: " + imageScaling + ", ");
	buf.append("compression: " + compression + ", ");
	buf.append("compressionQual: " + compressionQuality + ", ");
	buf.append("mirroringType: " + mirroringType + ", ");
	buf.append("duration: " + duration + ", ");
	buf.append("loopingMode: " + loopingMode + ", ");
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
