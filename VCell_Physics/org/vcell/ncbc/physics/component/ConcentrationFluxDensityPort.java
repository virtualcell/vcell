package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 1:19:35 PM)
 * @author: Jim Schaff
 */
public class ConcentrationFluxDensityPort extends Port {
	public static final String NAME = "flux";

/**
 * ConcentrationFluxDensity constructor comment.
 * @param argVariable ncbc.physics.component.Variable
 * @param argRole int
 */
public ConcentrationFluxDensityPort(int argRole) {
	super(new Variable(NAME, VCUnitDefinition.UNIT_uM_um_per_s), argRole);
}
}