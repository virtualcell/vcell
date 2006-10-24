package org.vcell.ncbc.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 1:19:35 PM)
 * @author: Jim Schaff
 */
public class SpeciesSurfaceFluxDensityPort extends Port {
	public static final String NAME = "flux";

/**
 * ConcentrationFluxDensity constructor comment.
 * @param argVariable ncbc.physics.component.Variable
 * @param argRole int
 */
public SpeciesSurfaceFluxDensityPort(int argRole) {
	super(new Variable(NAME, VCUnitDefinition.UNIT_molecules_per_um_per_s), argRole);
}
}