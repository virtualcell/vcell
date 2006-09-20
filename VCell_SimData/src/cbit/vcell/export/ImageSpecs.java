package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.DisplayPreferences;
import java.io.*;
import cbit.image.*;
/**
 * This type was created in VisualAge.
 */
public class ImageSpecs extends FormatSpecificSpecs implements Serializable {
	private DisplayPreferences[] displayPreferences;
	private int format;
	private int compression;
	private int mirroringType;
	private double duration;
	private int loopingMode;
	private boolean bHideMembraneOutline;
/**
 * Insert the method's description here.
 * Creation date: (3/1/2001 12:13:46 PM)
 * @param displayPreferences cbit.vcell.simdata.gui.DisplayPreferences[]
 * @param format int
 * @param compression int
 * @param mirroringType int
 * @param duration double
 * @param loopingMode int
 */
public ImageSpecs(DisplayPreferences[] displayPreferences, int format, int compression, int mirroringType, double duration, int loopingMode, boolean hideMembraneOutline) {
	this.displayPreferences = displayPreferences;
	this.format = format;
	this.compression = compression;
	this.mirroringType = mirroringType;
	this.duration = duration;
	this.loopingMode = loopingMode;
	this.bHideMembraneOutline = hideMembraneOutline;
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
			bHideMembraneOutline == imageSpecs.isHideMembraneOutline() &&
			displayPreferences.length == imageSpecs.getDisplayPreferences().length
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
public int getFormat() {
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
public boolean isHideMembraneOutline() {
	return bHideMembraneOutline;
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
	buf.append("compression: " + compression + ", ");
	buf.append("mirroringType: " + mirroringType + ", ");
	buf.append("duration: " + duration + ", ");
	buf.append("loopingMode: " + loopingMode + ", ");
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
}
