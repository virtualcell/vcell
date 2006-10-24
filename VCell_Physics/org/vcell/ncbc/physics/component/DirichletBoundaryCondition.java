package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 10:14:45 PM)
 * @author: Jim Schaff
 */
public class DirichletBoundaryCondition extends BoundaryCondition {
/**
 * DirichletBoundaryCondition constructor comment.
 * @param argName java.lang.String
 * @param argLocation ncbc.physics.component.Location
 */
public DirichletBoundaryCondition(String argName, SurfaceLocation argSurfaceLocation) {
	super(argName, argSurfaceLocation);
	addConnector(new ConcentrationConnector(this,CONNECTOR_NAME_CONCENTRATION,Port.ROLE_DEFINES,Port.ROLE_NONE));
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 4:53:49 PM)
 * @return ncbc.physics.component.ConcentrationConnector
 */
public ConcentrationConnector getConcentrationConnector() {
	return (ConcentrationConnector)getConnectorByName(CONNECTOR_NAME_CONCENTRATION);
}
}