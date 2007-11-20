package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2004 11:52:58 AM)
 * @author: Jim Schaff
 */
public class VoltageSource extends ElectricalDevice {
/**
 * LumpedCapacitor constructor comment.
 * @param argName java.lang.String
 */
public VoltageSource(String argName, Location argLocation, VCUnitDefinition unit, String expression) {
	super(argName, argLocation);

	Variable voltageDifference = (Variable)getIdentifierByName(VARIABLE_NAME_VOLTAGEDIFFERENCE);
	addEquation(new Equation(voltageDifference.getName()+" - "+expression));
}
}