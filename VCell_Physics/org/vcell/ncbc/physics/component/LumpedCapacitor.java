package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2004 11:52:58 AM)
 * @author: Jim Schaff
 */
public class LumpedCapacitor extends ElectricalDevice {
/**
 * LumpedCapacitor constructor comment.
 * @param argName java.lang.String
 */
public LumpedCapacitor(String argName, Location argLocation) {
	super(argName, argLocation);
	Variable voltageDifference = (Variable)getIdentifierByName(VARIABLE_NAME_VOLTAGEDIFFERENCE);
	TimeDerivative dVdt = new TimeDerivative(voltageDifference);
	addIdentifier(dVdt);
	addIdentifier(new Parameter("C",VCUnitDefinition.UNIT_pF));
	addEquation(new Equation("C"+"*"+dVdt.getName()+" - pos.i"));
}
}