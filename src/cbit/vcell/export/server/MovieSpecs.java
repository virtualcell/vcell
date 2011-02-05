package cbit.vcell.export.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.gui.*;
import java.io.*;
import cbit.image.*;
/**
 * This type was created in VisualAge.
 */
public class MovieSpecs extends FormatSpecificSpecs implements Serializable {
	private double duration;
	private boolean overlayMode;
	private DisplayPreferences[] displayPreferences;
	private int encodingFormat;
	private int mirroringType;
	private boolean bHideMembraneOutline;
	private int imageScaling;
	private int membraneScaling;
	private int meshMode;
	private int viewZoom;
	private int compressionType;
	private float compressionQuality;
	private boolean bQTVR;


/**
 * This method was created in VisualAge.
 * @param duration double
 * @param overlay boolean
 * @param displayPreferences cbit.vcell.simdata.gui.DisplayPreferences[]
 * @param encoding int
 * @param mirroring int
 */
public MovieSpecs(double duration, boolean overlayMode, DisplayPreferences[] displayPreferences, int encodingFormat,
		int mirroringType, boolean hideMembraneOutline,
		int imageScaling,int membraneScaling,int meshMode,int compressionType,float compressionQuality,boolean bQTVR) {
	this.duration = duration;
	this.overlayMode = overlayMode;
	this.displayPreferences = displayPreferences;
	this.encodingFormat = encodingFormat;
	this.mirroringType = mirroringType;
	this.bHideMembraneOutline = hideMembraneOutline;
	this.imageScaling = imageScaling;
	this.membraneScaling = membraneScaling;
	this.meshMode = meshMode;
	this.compressionType = compressionType;
	this.compressionQuality = compressionQuality;
	this.bQTVR = bQTVR;
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
public void setViewZoom(int viewZoom){
	this.viewZoom = viewZoom;
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
			bHideMembraneOutline == movieSpecs.isHideMembraneOutline() &&
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
public int getEncodingFormat() {
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
public boolean isHideMembraneOutline() {
	return bHideMembraneOutline;
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
	buf.append("compressionType: " + compressionType + ", ");
	buf.append("compressionQual: " + compressionQuality + ", ");
	buf.append("overlayMode: " + overlayMode + ", ");
	buf.append("encodingFormat: " + encodingFormat + ", ");
	buf.append("mirroringType: " + mirroringType + ", ");
	buf.append("duration: " + duration + ", ");
	buf.append("hideMembraneOutline: " + bHideMembraneOutline + ", ");
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
	return (getMeshMode() == ImagePaneModel.MESH_MODE?viewZoom:imageScaling);
}
public int getMembraneScaling() {
	return membraneScaling;
}
public int getMeshMode(){
	return meshMode;
}
}
