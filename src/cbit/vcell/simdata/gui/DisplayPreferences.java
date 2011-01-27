package cbit.vcell.simdata.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Range;

/**
 * Insert the type's description here.
 * Creation date: (2/28/2001 10:06:01 AM)
 * @author: Ion Moraru
 */
public class DisplayPreferences implements java.io.Serializable {
	private String colorMode;
	private org.vcell.util.Range scaleSettings;
	private int[] specialColors;
	BitSet domainValid;

/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 10:13:38 AM)
 * @param colorMode java.lang.String
 * @param scaleSettingd cbit.image.Range
 */
public DisplayPreferences(String colorMode, Range scaleSettings,int[] specialColors) {
	this.colorMode = colorMode;
	this.scaleSettings = scaleSettings;
	this.specialColors = specialColors;
}

public DisplayPreferences(DisplayPreferences displayPreferences,BitSet domainValid){
	this(displayPreferences.colorMode,displayPreferences.scaleSettings,displayPreferences.specialColors);
	this.domainValid = domainValid;
}
public BitSet getDomainValid(){
	return domainValid;
}
public int[] getSpecialColors(){
	return specialColors;
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
			Compare.isEqualOrNull(scaleSettings, displayPreferences.getScaleSettings()) &&
			Arrays.equals(specialColors, displayPreferences.getSpecialColors()) &&
			((domainValid == null && displayPreferences.getDomainValid() == null) || domainValid.equals(displayPreferences.getDomainValid()))
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
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public String toString() {
	return "DisplayPreferences [colorMode: " + colorMode + ", scaleSettings: " + scaleSettings + "]";
}
}