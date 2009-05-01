package cbit.vcell.simdata.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Range;

import cbit.image.*;
/**
 * Insert the type's description here.
 * Creation date: (2/28/2001 10:06:01 AM)
 * @author: Ion Moraru
 */
public class DisplayPreferences implements java.io.Serializable {
	private String colorMode;
	private org.vcell.util.Range scaleSettings;
	private boolean isDefaultScale;

/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 10:13:38 AM)
 * @param colorMode java.lang.String
 * @param scaleSettingd cbit.image.Range
 */
public DisplayPreferences(String colorMode, Range scaleSettings, boolean isDefaultScale) {
	this.colorMode = colorMode;
	this.scaleSettings = scaleSettings;
	this.isDefaultScale = isDefaultScale;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 1:26:25 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof DisplayPreferences) {
		DisplayPreferences displayPreferences = (DisplayPreferences)object;
		if (
			colorMode.equals(displayPreferences.getColorMode()) &&
			scaleSettings.equals(displayPreferences.getScaleSettings())
		) {
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 11:25:08 AM)
 * @return java.lang.String
 */
public String getColorMode() {
	return colorMode;
}


/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 11:25:39 AM)
 * @return cbit.image.Range
 */
public Range getScaleSettings() {
	return scaleSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:33:23 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 1:24:09 PM)
 * @return boolean
 */
public boolean scaleIsDefault() {
	return isDefaultScale;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public String toString() {
	return "DisplayPreferences [colorMode: " + colorMode + ", scaleSettings: " + scaleSettings + "]";
}
}