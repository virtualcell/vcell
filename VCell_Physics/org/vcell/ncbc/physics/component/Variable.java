package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 8:09:33 PM)
 * @author: Jim Schaff
 */
public class Variable extends Identifier {
	public static final Variable TIME_SECONDS = new Variable("t",VCUnitDefinition.UNIT_s);

/**
 * Variable constructor comment.
 */
public Variable(String argName, VCUnitDefinition argUnit) {
	super(argName,argUnit);
}
}