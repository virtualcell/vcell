package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 5:18:17 PM)
 * @author: Jim Schaff
 */
public class Derivative extends Identifier {
	private Variable var1 = null;
	private Variable var2 = null;

/**
 * Derivative constructor comment.
 * @param argName java.lang.String
 * @param argUnit ucar.units.Unit
 */
public Derivative(Variable argVariable1, Variable argVariable2, int order, VCUnitDefinition argUnit) {
	super("Der"+order+"("+argVariable1.getName()+","+argVariable2.getName()+")", argUnit);
	if (!argUnit.isCompatible(argVariable1.getUnit().divideBy(argVariable2.getUnit()))){
		throw new RuntimeException("units are not compatable");
	}
	this.var1 = argVariable1;
	this.var2 = argVariable2;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 5:34:19 PM)
 * @return ncbc.physics.component.Variable
 */
public Variable getVar1() {
	return var1;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 5:34:19 PM)
 * @return ncbc.physics.component.Variable
 */
public Variable getVar2() {
	return var2;
}
}