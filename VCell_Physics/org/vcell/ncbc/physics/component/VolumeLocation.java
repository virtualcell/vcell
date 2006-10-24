package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 6:57:06 AM)
 * @author: Jim Schaff
 */
public abstract class VolumeLocation extends Location {
	private String featureName = null;
/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 9:04:06 AM)
 * @param argName java.lang.String
 */
public VolumeLocation(String argName) {
	super(argName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 12:42:58 PM)
 * @return java.lang.String
 */
public java.lang.String getFeatureName() {
	return featureName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 12:42:58 PM)
 * @param newFeatureName java.lang.String
 */
public void setFeatureName(java.lang.String newFeatureName) {
	featureName = newFeatureName;
}
}
