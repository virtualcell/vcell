package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 10:14:06 PM)
 * @author: Jim Schaff
 */
public class NeumannBoundaryCondition extends BoundaryCondition {
/**
 * NeumannBoundaryCondition constructor comment.
 * @param argName java.lang.String
 * @param argLocation ncbc.physics.component.Location
 */
NeumannBoundaryCondition(String argName, SurfaceLocation argSurfaceLocation) {
	super(argName, argSurfaceLocation);
	addConnector(new VolumeFluxDensityConnector(this,CONNECTOR_NAME_FLUX,Port.ROLE_NONE,Port.ROLE_DEFINES));
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 4:53:49 PM)
 * @return ncbc.physics.component.ConcentrationConnector
 */
public ConcentrationConnector getFluxConnector() {
	return (ConcentrationConnector)getConnectorByName(CONNECTOR_NAME_FLUX);
}
}