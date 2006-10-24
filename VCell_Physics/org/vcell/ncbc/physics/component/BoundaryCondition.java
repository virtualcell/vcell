package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 10:12:57 PM)
 * @author: Jim Schaff
 */
public abstract class BoundaryCondition extends Device {
	public static final String CONNECTOR_NAME_CONCENTRATION = "source";
	public static final String CONNECTOR_NAME_FLUX = "flux";

/**
 * BoundaryCondition constructor comment.
 * @param argName java.lang.String
 * @param argLocation ncbc.physics.component.Location
 */
BoundaryCondition(String argName, SurfaceLocation argSurfaceLocation) {
	super(argName, argSurfaceLocation);
}
}