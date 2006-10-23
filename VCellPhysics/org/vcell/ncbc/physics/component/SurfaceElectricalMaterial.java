package org.vcell.ncbc.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:52:59 PM)
 * @author: Jim Schaff
 */
public abstract class SurfaceElectricalMaterial extends ElectricalMaterial {
	public static final String CONNECTOR_NAME_NEGATIVE = "neg";
	public static final String CONNECTOR_NAME_POSITIVE = "pos";
	public static final String VARIABLE_NAME_VOLTAGEDIFFERENCE = "V";

/**
 * SurfaceElectricalMaterial constructor comment.
 * @param argName java.lang.String
 * @param argLocation ncbc.physics.component.Location
 */
public SurfaceElectricalMaterial(String argName, SurfaceLocation argSurfaceLocation) {
	super(argName, argSurfaceLocation);
	addConnector(new ElectricalConnector(this, CONNECTOR_NAME_POSITIVE, Port.ROLE_INFLUENCES, Port.ROLE_INFLUENCES));
	addConnector(new ElectricalConnector(this, CONNECTOR_NAME_NEGATIVE, Port.ROLE_INFLUENCES, Port.ROLE_INFLUENCES));
	
	Variable voltageDifference = new Variable("V",VCUnitDefinition.UNIT_mV);
	addIdentifier(voltageDifference);
	addEquation(new Equation("pos.v - neg.v - V"));
	addEquation(new Equation("pos.i + neg.i"));
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:53:27 PM)
 * @return ncbc.physics.component.ElectricalConnector
 */
public ElectricalConnector getNegativeConnector() {
	return (ElectricalConnector)getConnectorByName(CONNECTOR_NAME_NEGATIVE);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:53:27 PM)
 * @return ncbc.physics.component.ElectricalConnector
 */
public ElectricalConnector getPositiveConnector() {
	return (ElectricalConnector)getConnectorByName(CONNECTOR_NAME_POSITIVE);
}
}