package org.vcell.ncbc.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 11:31:38 AM)
 * @author: Jim Schaff
 */
public class Parameter extends Identifier {
/**
 * Parameter constructor comment.
 * @param argName java.lang.String
 * @param argUnit ucar.units.Unit
 */
public Parameter(String argName, VCUnitDefinition argUnit) {
	super(argName, argUnit);
}
}