package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2004 11:52:58 AM)
 * @author: Jim Schaff
 */
public class CurrentSource extends ElectricalDevice {
/**
 * LumpedCapacitor constructor comment.
 * @param argName java.lang.String
 */
public CurrentSource(String argName, Location argLocation, VCUnitDefinition unit, String expression) {
	super(argName, argLocation);

	ElectricalConnector negConnector = (ElectricalConnector)getConnectorByName(CONNECTOR_NAME_NEGATIVE);
	negConnector.getCurrentPort().setRole(Port.ROLE_DEFINES);
	
	ElectricalConnector posConnector = (ElectricalConnector)getConnectorByName(CONNECTOR_NAME_NEGATIVE);
	posConnector.getCurrentPort().setRole(Port.ROLE_DEFINES);
	
	Variable currentSource = new Variable("currentSource",unit);
	addIdentifier(currentSource);
	addEquation(new Equation(posConnector.getCurrentPort().getVariable().getName()+" - "+currentSource.getName()));
}
}