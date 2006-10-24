package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:03:14 PM)
 * @author: Jim Schaff
 */
public class UnresolvedVolumeLocation extends VolumeLocation {
/**
 * UnresolvedVolume constructor comment.
 * @param argName java.lang.String
 */
public UnresolvedVolumeLocation(String argName) {
	super(argName);
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:43 AM)
 * @return boolean
 */
public boolean getResolved() {
	return false;
}
}