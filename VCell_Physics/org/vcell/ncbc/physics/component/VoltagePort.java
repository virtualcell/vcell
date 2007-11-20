package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 11:43:09 AM)
 * @author: Jim Schaff
 */
public class VoltagePort extends Port {
	public static final String NAME = "v";

/**
 * ConcentrationPort constructor comment.
 * @param argVariable ncbc.physics.component.Variable
 * @param argRole int
 */
public VoltagePort(int argRole) {
	super(new Variable(NAME, VCUnitDefinition.UNIT_mV), argRole);
}
}