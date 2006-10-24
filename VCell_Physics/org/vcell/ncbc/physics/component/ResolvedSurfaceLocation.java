package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 6:57:50 AM)
 * @author: Jim Schaff
 */
public class ResolvedSurfaceLocation extends SurfaceLocation {
	private cbit.vcell.geometry.surface.SurfaceGeometricRegion surfaceGeometricRegion = null;
/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 8:59:17 AM)
 * @param argName java.lang.String
 */
public ResolvedSurfaceLocation(String argName) {
	super(argName);
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:23 AM)
 * @return boolean
 */
public boolean getResolved() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 2:30:01 PM)
 * @param newSizeUnit cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.geometry.surface.SurfaceGeometricRegion getSurfaceGeometricRegion() {
	return surfaceGeometricRegion;
}
/**
 * Insert the method's description here.
 * Creation date: (5/26/2004 2:30:01 PM)
 * @param newSize java.lang.Double
 */
public void setSurfaceGeometricRegion(cbit.vcell.geometry.surface.SurfaceGeometricRegion argSurfaceGeometricRegion){
	surfaceGeometricRegion = argSurfaceGeometricRegion;
}
}
