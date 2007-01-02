package org.vcell.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * This is designed to hold quantities such as x,y,z,t
 * Creation date: (11/17/2005 3:59:48 PM)
 * @author: Jim Schaff
 */
public class IndependentVariable extends PhysicalSymbol {
/**
 * IndependentVariable constructor comment.
 * @param argName java.lang.String
 */
public IndependentVariable(String argName, VCUnitDefinition vcUnit) {
	super(argName,vcUnit);
}
}
