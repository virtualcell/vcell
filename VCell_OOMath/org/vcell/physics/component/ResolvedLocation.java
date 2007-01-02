package org.vcell.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:02:02 PM)
 * @author: Jim Schaff
 */
public class ResolvedLocation extends Location {
	private cbit.vcell.geometry.surface.GeometricRegion geometricRegion = null;
/**
 * ResolvedVolume constructor comment.
 * @param argName java.lang.String
 */
public ResolvedLocation(String argName, int dimension) {
	super(argName, dimension);
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:39 AM)
 * @return boolean
 */
public boolean getResolved() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:40:32 PM)
 * @return int
 */
public cbit.vcell.geometry.surface.GeometricRegion getGeometricRegion() {
	return geometricRegion;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:40:32 PM)
 * @param newRegionID int
 */
public void setGeometricRegion(cbit.vcell.geometry.surface.GeometricRegion argGeometricRegion){
	geometricRegion = argGeometricRegion;
}
}
