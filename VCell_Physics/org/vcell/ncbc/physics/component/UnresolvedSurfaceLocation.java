package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:02:56 PM)
 * @author: Jim Schaff
 */
public class UnresolvedSurfaceLocation extends SurfaceLocation {
	public static final String VAR_NAME_SURFACE_AREA_PER_TOTAL_VOLUME = "svr";

/**
 * UnresolvedMembrane constructor comment.
 * @param argName java.lang.String
 */
public UnresolvedSurfaceLocation(String argName) {
	super(argName);
	//addIdentifier(new Parameter(VAR_NAME_SURFACE_AREA_PER_TOTAL_VOLUME,Units.PER_MICRON));
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:34 AM)
 * @return boolean
 */
public boolean getResolved() {
	return false;
}
}