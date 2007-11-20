package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 11:43:09 AM)
 * @author: Jim Schaff
 */
public class SpeciesSurfaceDensityRatePort extends Port {
	public static final String NAME = "r";

/**
 * ConcentrationPort constructor comment.
 * @param argVariable ncbc.physics.component.Variable
 * @param argRole int
 */
public SpeciesSurfaceDensityRatePort(int argRole) {
	super(new Variable(NAME, VCUnitDefinition.UNIT_molecules_per_um2), argRole);
}
}