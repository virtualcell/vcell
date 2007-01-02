package org.vcell.physics.component;

import cbit.vcell.units.VCUnitDefinition;
import jscl.plugin.Expression;
import jscl.plugin.ParseException;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:02:56 PM)
 * @author: Jim Schaff
 */
public class LumpedLocation extends Location {

/**
 * UnresolvedMembrane constructor comment.
 * @param argName java.lang.String
 */
public LumpedLocation(String argName, int dimension) {
	super(argName,dimension);
	addSymbol(new Parameter("size",VCUnitDefinition.UNIT_um3));
	try {
		addEquation(Expression.valueOf("size-1"));
	} catch (ParseException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}

}